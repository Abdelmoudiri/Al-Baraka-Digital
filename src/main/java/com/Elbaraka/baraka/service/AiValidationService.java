package com.Elbaraka.baraka.service;

import com.Elbaraka.baraka.entity.AiValidationResult;
import com.Elbaraka.baraka.entity.Document;
import com.Elbaraka.baraka.entity.Operation;
import com.Elbaraka.baraka.enums.AiDecision;
import com.Elbaraka.baraka.repository.AiValidationResultRepository;
import com.Elbaraka.baraka.repository.DocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service de validation IA des opérations bancaires.
 * Note: L'intégration Spring AI est temporairement désactivée en raison de l'incompatibilité avec Spring Boot 4.0.0.
 * Le service retourne automatiquement NEED_HUMAN_REVIEW jusqu'à ce que Spring AI soit compatible.
 */
@Service
@Slf4j
public class AiValidationService {

    private final AiValidationResultRepository aiValidationResultRepository;
    private final DocumentRepository documentRepository;
    private final Tika tika = new Tika();
    
    // ChatModel désactivé temporairement - incompatible avec Spring Boot 4.0.0
    // private final ChatModel chatModel;

    @Autowired
    public AiValidationService(AiValidationResultRepository aiValidationResultRepository,
                               DocumentRepository documentRepository) {
        this.aiValidationResultRepository = aiValidationResultRepository;
        this.documentRepository = documentRepository;
    }

    private static final String VALIDATION_PROMPT_TEMPLATE = """
            Tu es un expert bancaire spécialisé dans la validation de documents financiers.
            
            Analyse le document suivant et détermine s'il justifie correctement l'opération bancaire :
            
            **Informations de l'opération :**
            - Type : {operationType}
            - Montant déclaré : {declaredAmount} DH
            - Date de création : {operationDate}
            - Compte source : {sourceAccount}
            
            **Contenu du document extrait :**
            {documentText}
            
            **Critères d'analyse :**
            1. Le montant dans le document correspond-il au montant déclaré ?
            2. Le document est-il lisible et de bonne qualité ?
            3. Le document contient-il les informations nécessaires (montant, date, référence) ?
            4. Y a-t-il des incohérences ou signaux d'alerte ?
            5. Le type de document est-il adapté à l'opération ?
            
            **Instructions :**
            - Si tout est cohérent et clair → Réponds "DECISION: APPROVE"
            - Si le document est invalide, falsifié ou incohérent → Réponds "DECISION: REJECT"
            - Si tu as des doutes ou besoin d'une vérification humaine → Réponds "DECISION: NEED_HUMAN_REVIEW"
            
            Fournis une réponse structurée sous ce format :
            DECISION: [APPROVE/REJECT/NEED_HUMAN_REVIEW]
            CONFIDENCE: [0.0 à 1.0]
            EXTRACTED_AMOUNT: [montant trouvé dans le document ou NA]
            QUALITY_SCORE: [0.0 à 1.0]
            SUMMARY: [résumé de ton analyse en 2-3 phrases]
            RISK_FACTORS: [liste des risques identifiés ou "Aucun"]
            """;

    @Transactional
    public AiValidationResult analyzeOperation(Operation operation) {
        log.info("Démarrage de l'analyse IA pour l'opération #{}", operation.getId());
        long startTime = System.currentTimeMillis();

        try {
            List<Document> documents = documentRepository.findByOperationId(operation.getId());
            
            if (documents.isEmpty()) {
                log.warn("Aucun document trouvé pour l'opération #{}", operation.getId());
                return createDefaultResult(operation, AiDecision.NEED_HUMAN_REVIEW, 
                    "Aucun justificatif fourni", startTime);
            }

            Document primaryDocument = documents.get(0);
            String extractedText = extractTextFromDocument(primaryDocument);

            if (extractedText == null || extractedText.trim().isEmpty()) {
                log.warn("Impossible d'extraire le texte du document #{}", primaryDocument.getId());
                return createDefaultResult(operation, AiDecision.NEED_HUMAN_REVIEW,
                    "Document illisible ou format non supporté", startTime);
            }

            // Spring AI désactivé - retourne NEED_HUMAN_REVIEW par défaut
            log.info("Spring AI temporairement désactivé - validation manuelle requise pour l'opération #{}", operation.getId());
            return createDefaultResult(operation, AiDecision.NEED_HUMAN_REVIEW,
                "Validation IA temporairement indisponible - vérification manuelle requise. " +
                "Document extrait avec succès (" + extractedText.length() + " caractères).", startTime);

            /* Code Spring AI désactivé temporairement - incompatible avec Spring Boot 4.0.0
            Prompt prompt = promptTemplate.create(promptVariables);
            String aiResponse = chatModel.call(prompt).getResult().getOutput().getContent();
            log.info("Réponse de l'IA reçue : {}", aiResponse);
            AiValidationResult result = parseAiResponse(aiResponse, operation, startTime);
            return aiValidationResultRepository.save(result);
            */

        } catch (Exception e) {
            log.error("Erreur lors de l'analyse IA de l'opération #{}", operation.getId(), e);
            return createDefaultResult(operation, AiDecision.NEED_HUMAN_REVIEW,
                "Erreur technique lors de l'analyse : " + e.getMessage(), startTime);
        }
    }

    private String extractTextFromDocument(Document document) {
        try {
            File file = new File(document.getStoragePath());
            if (!file.exists()) {
                log.error("Fichier non trouvé : {}", document.getStoragePath());
                return null;
            }
            
            String text = tika.parseToString(file);
            log.debug("Texte extrait du document {} : {} caractères", document.getId(), text.length());
            return text;
            
        } catch (Exception e) {
            log.error("Erreur lors de l'extraction de texte du document #{}", document.getId(), e);
            return null;
        }
    }

    private AiValidationResult parseAiResponse(String aiResponse, Operation operation, long startTime) {
        AiDecision decision = extractDecision(aiResponse);
        Double confidence = extractConfidence(aiResponse);
        BigDecimal extractedAmount = extractAmount(aiResponse);
        Double qualityScore = extractQualityScore(aiResponse);
        String summary = extractSummary(aiResponse);
        String riskFactors = extractRiskFactors(aiResponse);

        return AiValidationResult.builder()
                .operation(operation)
                .decision(decision)
                .confidenceScore(confidence)
                .analysisSummary(summary)
                .extractedAmount(extractedAmount)
                .documentQualityScore(qualityScore)
                .riskFactors(riskFactors)
                .analyzedAt(LocalDateTime.now())
                .modelUsed("gpt-4o-mini")
                .processingTimeMs(System.currentTimeMillis() - startTime)
                .build();
    }

    private AiValidationResult createDefaultResult(Operation operation, AiDecision decision, 
                                                   String summary, long startTime) {
        AiValidationResult result = AiValidationResult.builder()
                .operation(operation)
                .decision(decision)
                .confidenceScore(0.0)
                .analysisSummary(summary)
                .analyzedAt(LocalDateTime.now())
                .modelUsed("gpt-4o-mini")
                .processingTimeMs(System.currentTimeMillis() - startTime)
                .build();
        
        return aiValidationResultRepository.save(result);
    }

    private AiDecision extractDecision(String response) {
        if (response.contains("DECISION: APPROVE")) return AiDecision.APPROVE;
        if (response.contains("DECISION: REJECT")) return AiDecision.REJECT;
        return AiDecision.NEED_HUMAN_REVIEW;
    }

    private Double extractConfidence(String response) {
        try {
            String[] lines = response.split("\n");
            for (String line : lines) {
                if (line.startsWith("CONFIDENCE:")) {
                    return Double.parseDouble(line.replace("CONFIDENCE:", "").trim());
                }
            }
        } catch (Exception e) {
            log.warn("Impossible d'extraire le score de confiance", e);
        }
        return 0.5;
    }

    private BigDecimal extractAmount(String response) {
        try {
            String[] lines = response.split("\n");
            for (String line : lines) {
                if (line.startsWith("EXTRACTED_AMOUNT:")) {
                    String amount = line.replace("EXTRACTED_AMOUNT:", "").trim();
                    if (!amount.equalsIgnoreCase("NA")) {
                        return new BigDecimal(amount.replaceAll("[^0-9.]", ""));
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Impossible d'extraire le montant", e);
        }
        return null;
    }

    private Double extractQualityScore(String response) {
        try {
            String[] lines = response.split("\n");
            for (String line : lines) {
                if (line.startsWith("QUALITY_SCORE:")) {
                    return Double.parseDouble(line.replace("QUALITY_SCORE:", "").trim());
                }
            }
        } catch (Exception e) {
            log.warn("Impossible d'extraire le score de qualité", e);
        }
        return 0.5;
    }

    private String extractSummary(String response) {
        try {
            String[] lines = response.split("\n");
            for (String line : lines) {
                if (line.startsWith("SUMMARY:")) {
                    return line.replace("SUMMARY:", "").trim();
                }
            }
        } catch (Exception e) {
            log.warn("Impossible d'extraire le résumé", e);
        }
        return "Analyse non disponible";
    }

    private String extractRiskFactors(String response) {
        try {
            String[] lines = response.split("\n");
            for (String line : lines) {
                if (line.startsWith("RISK_FACTORS:")) {
                    return line.replace("RISK_FACTORS:", "").trim();
                }
            }
        } catch (Exception e) {
            log.warn("Impossible d'extraire les facteurs de risque", e);
        }
        return "Aucun";
    }

    public AiValidationResult getValidationResult(Long operationId) {
        return aiValidationResultRepository.findByOperationId(operationId).orElse(null);
    }

    public Map<String, Long> getValidationStatistics() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("approved", aiValidationResultRepository.countByDecision(AiDecision.APPROVE));
        stats.put("rejected", aiValidationResultRepository.countByDecision(AiDecision.REJECT));
        stats.put("humanReview", aiValidationResultRepository.countByDecision(AiDecision.NEED_HUMAN_REVIEW));
        return stats;
    }
}
