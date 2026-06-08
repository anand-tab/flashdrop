package com.flashdrop.inventoryService.repository;

import com.flashdrop.inventoryService.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    public Optional<Inventory> findBySku_SkuCode(String skuCode);
}
