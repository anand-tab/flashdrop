package com.flashdrop.productService.repository;

import com.flashdrop.productService.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Products, String> {
}
