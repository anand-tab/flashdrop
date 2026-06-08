package com.flashdrop.inventoryService.repository;

import com.flashdrop.inventoryService.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    public Product findByProductCode(String productCode);
}
