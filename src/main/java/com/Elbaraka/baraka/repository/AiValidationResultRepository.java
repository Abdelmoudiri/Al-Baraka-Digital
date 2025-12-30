package com.Elbaraka.baraka.repository;

import com.Elbaraka.baraka.entity.AiValidationResult;
import com.Elbaraka.baraka.enums.AiDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AiValidationResultRepository extends JpaRepository<AiValidationResult, Long> {
    
    Optional<AiValidationResult> findByOperationId(Long operationId);
    
    long countByDecision(AiDecision decision);
}
