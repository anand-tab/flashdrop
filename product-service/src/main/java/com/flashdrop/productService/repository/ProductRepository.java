package com.flashdrop.productService.repository;

import com.flashdrop.productService.dto.ProductResponse;
import com.flashdrop.productService.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Products, String> {


    Products findByProductId(String productId);


    @Query("SELECT p FROM Products p WHERE p.productCategory = :category")
    Optional<List<Products>> getByCategory(@Param("category") String productCategory);
}
