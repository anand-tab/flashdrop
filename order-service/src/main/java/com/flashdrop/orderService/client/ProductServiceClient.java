package com.flashdrop.orderService.client;

import com.flashdrop.orderService.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="product-service", url ="http://localhost:3003")
public interface ProductServiceClient {

    @GetMapping("/api/product/{id}")
    ProductResponse getProductById(@PathVariable("id") String id);

}
