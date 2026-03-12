package com.flashdrop.productService.services;

import com.flashdrop.productService.dto.ProductMapper;
import com.flashdrop.productService.dto.ProductResponse;
import com.flashdrop.productService.entity.Products;
import com.flashdrop.productService.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    public String addProduct(Products products) {

        productRepository.save(products);
        return "Product added successfully";

    }

    public List<ProductResponse> getAllProduct() {

        List<Products> products = productRepository.findAll();
        List<ProductResponse> productResponseList = productMapper.toProductResponseList(products);
        return productResponseList;

    }
}
