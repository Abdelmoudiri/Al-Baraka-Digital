package com.Elbaraka.baraka.repository;

import com.Elbaraka.baraka.entity.Account;
import com.Elbaraka.baraka.entity.Operation;
import com.Elbaraka.baraka.enums.OperationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OperationRepository extends JpaRepository<Operation,Long> {
    List<Operation> findByAccountSourceOrAccountDestinationOrderByCreatedAtDesc(Account accountSource, Account accountDestination);
    List<Operation> findByStatusOrderByCreatedAtDesc(OperationStatus status);
}
