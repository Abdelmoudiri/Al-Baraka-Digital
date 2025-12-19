package com.Elbaraka.baraka.entity;

import com.Elbaraka.baraka.enums.OperationStatus;
import com.Elbaraka.baraka.enums.OperationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "operations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Type d'opération est obligatoire")
    @Column(nullable = false)
    private OperationType type;

    @NotNull(message = "Montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Statut est obligatoire")
    @Column(nullable = false)
    private OperationStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime validatedAt;

    @Column
    private LocalDateTime executedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_source_id", nullable = false)
    private Account accountSource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_destination_id")
    private Account accountDestination;

    @OneToMany(mappedBy = "operation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents = new ArrayList<>();
}
