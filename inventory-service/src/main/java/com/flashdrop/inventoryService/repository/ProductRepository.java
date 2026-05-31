package com.flashdrop.inventoryService.repository;

import com.flashdrop.inventoryService.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
