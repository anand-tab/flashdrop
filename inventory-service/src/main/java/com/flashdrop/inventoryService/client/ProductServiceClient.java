package com.flashdrop.inventoryService.client;

import com.flashdrop.inventoryService.dto.ProductServiceReq;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name="product-service", url ="http://localhost:3003")
public interface ProductServiceClient {

    @PostMapping("/api/product/addProduct")
    String addProduct(@RequestBody ProductServiceReq productServiceReq);

}
