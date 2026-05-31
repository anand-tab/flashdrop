package com.flashdrop.inventoryService.repository;

import com.flashdrop.inventoryService.entity.Sku;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkuRepository extends JpaRepository<Sku,Long> {
}
