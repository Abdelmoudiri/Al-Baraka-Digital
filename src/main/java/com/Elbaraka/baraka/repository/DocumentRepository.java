package com.Elbaraka.baraka.repository;

import com.Elbaraka.baraka.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document,Long> {
}
