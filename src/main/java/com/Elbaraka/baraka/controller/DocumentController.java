package com.Elbaraka.baraka.controller;

import com.Elbaraka.baraka.entity.Document;
import com.Elbaraka.baraka.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/client/operations/{operationId}/documents")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> uploadDocument(
            @PathVariable Long operationId,
            @RequestParam("file") MultipartFile file) {
        try {
            Document document = documentService.uploadDocument(operationId, file);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", document.getId());
            response.put("fileName", document.getFileName());
            response.put("fileType", document.getFileType());
            response.put("uploadedAt", document.getUploadedAt());
            response.put("message", "Document téléchargé avec succès");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors du téléchargement: " + e.getMessage()));
        }
    }

    @GetMapping("/documents/{documentId}/download")
    @PreAuthorize("hasAnyRole('CLIENT', 'AGENT', 'ADMIN')")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long documentId) {
        try {
            Document document = documentService.getDocument(documentId);
            Resource resource = documentService.downloadDocument(documentId);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(document.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + document.getFileName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/operations/{operationId}/documents")
    @PreAuthorize("hasAnyRole('CLIENT', 'AGENT', 'ADMIN')")
    public ResponseEntity<?> getOperationDocuments(@PathVariable Long operationId) {
        try {
            List<Document> documents = documentService.getOperationDocuments(operationId);
            
            List<Map<String, Object>> response = documents.stream()
                    .map(doc -> {
                        Map<String, Object> docMap = new HashMap<>();
                        docMap.put("id", doc.getId());
                        docMap.put("fileName", doc.getFileName());
                        docMap.put("fileType", doc.getFileType());
                        docMap.put("uploadedAt", doc.getUploadedAt());
                        return docMap;
                    })
                    .toList();
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/client/documents/{documentId}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> deleteDocument(@PathVariable Long documentId) {
        try {
            documentService.deleteDocument(documentId);
            return ResponseEntity.ok(Map.of("message", "Document supprimé avec succès"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
