package com.flashdrop.inventoryService.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sku_id")
    private Sku sku;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private Integer quantity;

    private String orderId;

    private String remarks;

    private String userId;

    @CreationTimestamp
    private LocalDateTime transactionTime;
}
