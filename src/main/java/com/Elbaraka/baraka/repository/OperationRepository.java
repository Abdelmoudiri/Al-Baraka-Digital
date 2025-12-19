package com.Elbaraka.baraka.repository;

import com.Elbaraka.baraka.entity.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationRepository extends JpaRepository<Operation,Long> {
}
