package com.Elbaraka.baraka.service;

import com.Elbaraka.baraka.entity.Document;
import com.Elbaraka.baraka.entity.Operation;
import com.Elbaraka.baraka.exception.ResourceNotFoundException;
import com.Elbaraka.baraka.repository.DocumentRepository;
import com.Elbaraka.baraka.repository.OperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final OperationRepository operationRepository;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Transactional
    public Document uploadDocument(Long operationId, MultipartFile file) {
        // Vérifier que l'opération existe
        Operation operation = operationRepository.findById(operationId)
                .orElseThrow(() -> new ResourceNotFoundException("Opération non trouvée"));

        // Valider le fichier
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }

        // Vérifier le type de fichier (PDF, JPG, PNG uniquement)
        String contentType = file.getContentType();
        if (contentType == null || !isValidFileType(contentType)) {
            throw new IllegalArgumentException("Type de fichier non autorisé. Seuls PDF, JPG et PNG sont acceptés");
        }

        try {
            // Créer le répertoire s'il n'existe pas
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Générer un nom de fichier unique
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            // Enregistrer le fichier
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Créer l'entité Document
            Document document = new Document();
            document.setFileName(originalFilename);
            document.setFileType(contentType);
            document.setStoragePath(uniqueFilename);
            document.setOperation(operation);

            return documentRepository.save(document);

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du téléchargement du fichier: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Resource downloadDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document non trouvé"));

        try {
            Path filePath = Paths.get(uploadDir).resolve(document.getStoragePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("Fichier non trouvé ou illisible");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Document getDocument(Long documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document non trouvé"));
    }

    @Transactional(readOnly = true)
    public List<Document> getOperationDocuments(Long operationId) {
        Operation operation = operationRepository.findById(operationId)
                .orElseThrow(() -> new ResourceNotFoundException("Opération non trouvée"));

        // Utiliser une requête personnalisée pour éviter le lazy loading
        return documentRepository.findByOperationId(operationId);
    }

    @Transactional
    public void deleteDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document non trouvé"));

        try {
            // Supprimer le fichier physique
            Path filePath = Paths.get(uploadDir).resolve(document.getStoragePath());
            Files.deleteIfExists(filePath);

            // Supprimer l'enregistrement en base
            documentRepository.delete(document);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la suppression du fichier: " + e.getMessage());
        }
    }

    private boolean isValidFileType(String contentType) {
        return contentType.equals("application/pdf") ||
               contentType.equals("image/jpeg") ||
               contentType.equals("image/jpg") ||
               contentType.equals("image/png");
    }

    public long countDocumentsForOperation(Long operationId) {
        return documentRepository.countByOperationId(operationId);
    }
}
