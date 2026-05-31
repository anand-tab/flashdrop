package com.flashdrop.inventoryService.repository;

import com.flashdrop.inventoryService.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}
