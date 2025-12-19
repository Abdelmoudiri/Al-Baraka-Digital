package com.Elbaraka.baraka.repository;

import com.Elbaraka.baraka.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account,Long> {
}
