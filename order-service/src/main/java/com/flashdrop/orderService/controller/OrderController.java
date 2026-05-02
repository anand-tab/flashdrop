package com.flashdrop.orderService.controller;

import com.flashdrop.orderService.dto.OrderRequest;
import com.flashdrop.orderService.dto.OrderResponse;
import com.flashdrop.orderService.entity.Order;
import com.flashdrop.orderService.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;


    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        log.info("Order request received: {}", request);
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.ok(response);
    }

}
