package com.flashdrop.inventoryService.repository;

import com.flashdrop.inventoryService.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<InventoryTransaction, Long> {
}
