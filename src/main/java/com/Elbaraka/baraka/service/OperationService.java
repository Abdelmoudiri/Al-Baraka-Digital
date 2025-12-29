package com.Elbaraka.baraka.service;

import com.Elbaraka.baraka.dto.OperationRequest;
import com.Elbaraka.baraka.dto.OperationResponse;
import com.Elbaraka.baraka.dto.TransferRequest;
import com.Elbaraka.baraka.entity.Account;
import com.Elbaraka.baraka.entity.Operation;
import com.Elbaraka.baraka.entity.User;
import com.Elbaraka.baraka.enums.OperationStatus;
import com.Elbaraka.baraka.enums.OperationType;
import com.Elbaraka.baraka.repository.AccountRepository;
import com.Elbaraka.baraka.repository.DocumentRepository;
import com.Elbaraka.baraka.repository.OperationRepository;
import com.Elbaraka.baraka.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OperationService {

    private static final BigDecimal VALIDATION_THRESHOLD = new BigDecimal("10000");

    private final OperationRepository operationRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;

    public OperationService(OperationRepository operationRepository,
                            AccountRepository accountRepository,
                            UserRepository userRepository,
                            DocumentRepository documentRepository) {
        this.operationRepository = operationRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    @Transactional
    public OperationResponse createDeposit(OperationRequest request) {
        User user = getCurrentUser();
        Account account = user.getAccount();

        if (account == null) {
            throw new RuntimeException("Compte non trouvé pour cet utilisateur");
        }

        Operation operation = new Operation();
        operation.setType(OperationType.DEPOSIT);
        operation.setAmount(request.getAmount());
        operation.setAccountSource(account);

        // Validation automatique si montant <= 10 000 DH
        if (request.getAmount().compareTo(VALIDATION_THRESHOLD) <= 0) {
            operation.setStatus(OperationStatus.COMPLETED);
            operation.setExecutedAt(LocalDateTime.now());
            
            // Mise à jour du solde
            account.setBalance(account.getBalance().add(request.getAmount()));
            accountRepository.save(account);
        } else {
            operation.setStatus(OperationStatus.PENDING);
        }

        Operation savedOperation = operationRepository.save(operation);
        return mapToResponse(savedOperation);
    }

    @Transactional
    public OperationResponse createWithdrawal(OperationRequest request) {
        User user = getCurrentUser();
        Account account = user.getAccount();

        if (account == null) {
            throw new RuntimeException("Compte non trouvé pour cet utilisateur");
        }

        // Vérifier solde suffisant
        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Solde insuffisant");
        }

        Operation operation = new Operation();
        operation.setType(OperationType.WITHDRAWAL);
        operation.setAmount(request.getAmount());
        operation.setAccountSource(account);

        // Validation automatique si montant <= 10 000 DH
        if (request.getAmount().compareTo(VALIDATION_THRESHOLD) <= 0) {
            operation.setStatus(OperationStatus.COMPLETED);
            operation.setExecutedAt(LocalDateTime.now());
            
            // Mise à jour du solde
            account.setBalance(account.getBalance().subtract(request.getAmount()));
            accountRepository.save(account);
        } else {
            operation.setStatus(OperationStatus.PENDING);
        }

        Operation savedOperation = operationRepository.save(operation);
        return mapToResponse(savedOperation);
    }

    @Transactional
    public OperationResponse createTransfer(TransferRequest request) {
        User user = getCurrentUser();
        Account sourceAccount = user.getAccount();

        if (sourceAccount == null) {
            throw new RuntimeException("Compte source non trouvé");
        }

        // Vérifier compte destination
        Account destinationAccount = accountRepository.findByAccountNumber(request.getDestinationAccountNumber())
                .orElseThrow(() -> new RuntimeException("Compte destination non trouvé"));

        // Vérifier que ce n'est pas le même compte
        if (sourceAccount.getId().equals(destinationAccount.getId())) {
            throw new RuntimeException("Impossible de transférer vers le même compte");
        }

        // Vérifier solde suffisant
        if (sourceAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Solde insuffisant");
        }

        Operation operation = new Operation();
        operation.setType(OperationType.TRANSFER);
        operation.setAmount(request.getAmount());
        operation.setAccountSource(sourceAccount);
        operation.setAccountDestination(destinationAccount);

        // Validation automatique si montant <= 10 000 DH
        if (request.getAmount().compareTo(VALIDATION_THRESHOLD) <= 0) {
            operation.setStatus(OperationStatus.COMPLETED);
            operation.setExecutedAt(LocalDateTime.now());
            
            // Mise à jour des soldes
            sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.getAmount()));
            destinationAccount.setBalance(destinationAccount.getBalance().add(request.getAmount()));
            accountRepository.save(sourceAccount);
            accountRepository.save(destinationAccount);
        } else {
            operation.setStatus(OperationStatus.PENDING);
        }

        Operation savedOperation = operationRepository.save(operation);
        return mapToResponse(savedOperation);
    }

    public List<OperationResponse> getClientOperations() {
        User user = getCurrentUser();
        Account account = user.getAccount();

        if (account == null) {
            throw new RuntimeException("Compte non trouvé pour cet utilisateur");
        }

        List<Operation> operations = operationRepository.findByAccountSourceOrAccountDestinationOrderByCreatedAtDesc(
                account, account);

        return operations.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<OperationResponse> getPendingOperations() {
        List<Operation> pendingOperations = operationRepository.findByStatusOrderByCreatedAtDesc(OperationStatus.PENDING);
        return pendingOperations.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<OperationResponse> getAllOperations() {
        List<Operation> operations = operationRepository.findAll();
        return operations.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public OperationResponse approveOperation(Long operationId) {
        Operation operation = operationRepository.findById(operationId)
                .orElseThrow(() -> new RuntimeException("Opération non trouvée"));

        if (operation.getStatus() != OperationStatus.PENDING) {
            throw new RuntimeException("Cette opération n'est pas en attente de validation");
        }

        // Vérifier qu'un document est fourni pour les montants > 10 000 DH
        if (operation.getAmount().compareTo(VALIDATION_THRESHOLD) > 0) {
            long documentCount = documentRepository.countByOperationId(operationId);
            if (documentCount == 0) {
                throw new RuntimeException("Un justificatif est requis pour les opérations supérieures à 10 000 DH");
            }
        }

        // Mettre à jour le statut
        operation.setStatus(OperationStatus.APPROVED);
        operation.setValidatedAt(LocalDateTime.now());
        operation.setExecutedAt(LocalDateTime.now());

        // Exécuter l'opération selon le type
        Account sourceAccount = operation.getAccountSource();
        Account destinationAccount = operation.getAccountDestination();

        switch (operation.getType()) {
            case DEPOSIT:
                sourceAccount.setBalance(sourceAccount.getBalance().add(operation.getAmount()));
                accountRepository.save(sourceAccount);
                break;

            case WITHDRAWAL:
                // Vérifier solde suffisant
                if (sourceAccount.getBalance().compareTo(operation.getAmount()) < 0) {
                    throw new RuntimeException("Solde insuffisant pour cette opération");
                }
                sourceAccount.setBalance(sourceAccount.getBalance().subtract(operation.getAmount()));
                accountRepository.save(sourceAccount);
                break;

            case TRANSFER:
                // Vérifier solde suffisant
                if (sourceAccount.getBalance().compareTo(operation.getAmount()) < 0) {
                    throw new RuntimeException("Solde insuffisant pour cette opération");
                }
                sourceAccount.setBalance(sourceAccount.getBalance().subtract(operation.getAmount()));
                destinationAccount.setBalance(destinationAccount.getBalance().add(operation.getAmount()));
                accountRepository.save(sourceAccount);
                accountRepository.save(destinationAccount);
                break;
        }

        Operation savedOperation = operationRepository.save(operation);
        return mapToResponse(savedOperation);
    }

    @Transactional
    public OperationResponse rejectOperation(Long operationId) {
        Operation operation = operationRepository.findById(operationId)
                .orElseThrow(() -> new RuntimeException("Opération non trouvée"));

        if (operation.getStatus() != OperationStatus.PENDING) {
            throw new RuntimeException("Cette opération n'est pas en attente de validation");
        }

        // Mettre à jour le statut
        operation.setStatus(OperationStatus.REJECTED);
        operation.setValidatedAt(LocalDateTime.now());

        Operation savedOperation = operationRepository.save(operation);
        return mapToResponse(savedOperation);
    }

    private OperationResponse mapToResponse(Operation operation) {
        return OperationResponse.builder()
                .id(operation.getId())
                .type(operation.getType())
                .amount(operation.getAmount())
                .status(operation.getStatus())
                .createdAt(operation.getCreatedAt())
                .executedAt(operation.getExecutedAt())
                .sourceAccountNumber(operation.getAccountSource() != null ? 
                        operation.getAccountSource().getAccountNumber() : null)
                .destinationAccountNumber(operation.getAccountDestination() != null ? 
                        operation.getAccountDestination().getAccountNumber() : null)
                .build();
    }
}
