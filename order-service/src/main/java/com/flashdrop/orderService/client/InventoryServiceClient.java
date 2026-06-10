package com.flashdrop.orderService.client;


import com.flashdrop.orderService.dto.KafkaContext;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="inventory-service" ,url = "http://localhost:3005")
public interface InventoryServiceClient {

    @PostMapping("/inventory/api/purchaseProd")
    Void purchasedHot(@RequestBody KafkaContext kafkaContext);
}
