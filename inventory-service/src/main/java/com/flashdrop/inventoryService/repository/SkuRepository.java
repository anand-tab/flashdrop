package com.flashdrop.inventoryService.repository;

import com.flashdrop.inventoryService.entity.Inventory;
import com.flashdrop.inventoryService.entity.Sku;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SkuRepository extends JpaRepository<Sku,Long> {

    public Optional<Sku> findBySkuCode(String skuCode);
}
