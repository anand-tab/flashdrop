package com.flashdrop.productService.repository;

import com.flashdrop.productService.dto.ProductResponse;
import com.flashdrop.productService.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Products, String> {


    Products findProductId(String productId);

}
