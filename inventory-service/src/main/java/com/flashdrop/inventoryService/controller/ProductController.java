package com.flashdrop.inventoryService.controller;

import com.flashdrop.inventoryService.dto.AddProductReq;
import com.flashdrop.inventoryService.dto.AddProductRes;
import com.flashdrop.inventoryService.repository.ProductRepository;
import com.flashdrop.inventoryService.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory/api")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/addProduct")
    public ResponseEntity<String> addProduct(@RequestBody AddProductReq addProductReq) {
            try{
               productService.addProduct(addProductReq);
                return ResponseEntity.ok("Successfully Added");
            }catch (Exception e){
                throw new RuntimeException(e.getMessage());
            }
    }

}
