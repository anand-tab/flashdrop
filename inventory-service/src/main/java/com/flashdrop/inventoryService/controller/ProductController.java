package com.flashdrop.inventoryService.controller;

import com.flashdrop.inventoryService.dto.AddProductReq;
import com.flashdrop.inventoryService.dto.AddProductRes;
import com.flashdrop.inventoryService.dto.KafkaContext;
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

    @PostMapping("/purchaseProd")
    public ResponseEntity<Void> purchasedHot(@RequestBody KafkaContext kafkaContext){
        try{
            productService.purchasedHot(kafkaContext.getOrderId(), kafkaContext);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

}
