package com.Elbaraka.baraka.repository;

import com.Elbaraka.baraka.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document,Long> {
    List<Document> findByOperationId(Long operationId);
    long countByOperationId(Long operationId);
}
