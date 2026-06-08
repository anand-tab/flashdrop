package com.flashdrop.inventoryService.service;

import com.flashdrop.inventoryService.dto.KafkaContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;   // ✅ correct import
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderConsumerService {

    @Autowired
    private ProductService productService;

    @KafkaListener(topics = "orders", groupId = "flash-order-v2")
    public void consume(
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Payload KafkaContext kafkaContext,
            Acknowledgment acknowledgment) {
        try {
            log.info("✅ Key: {}", key);
            log.info("✅ Payload: {}", kafkaContext);
            productService.purchasedHot(key, kafkaContext);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("❌ Error: ", e);  // full stack trace
            acknowledgment.acknowledge(); // move past the message
        }
    }
}