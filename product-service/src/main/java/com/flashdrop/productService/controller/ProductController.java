package com.flashdrop.productService.controller;

import com.flashdrop.productService.dto.ProductResponse;
import com.flashdrop.productService.entity.Products;
import com.flashdrop.productService.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;


    @PostMapping("/addProduct")
    public ResponseEntity<String> addProduct(@RequestBody Products products){
        return ResponseEntity.ok(productService.addProduct(products));
    }


    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(){

        return ResponseEntity.ok(productService.getAllProduct());
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable String productId){
        return ResponseEntity.ok(productService.getProductById(productId));
    }
}
