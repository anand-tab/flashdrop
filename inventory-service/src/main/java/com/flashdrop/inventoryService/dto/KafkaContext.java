package com.flashdrop.inventoryService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KafkaContext {
    private String orderId;
    private String email;
    private int quantity;
    private String remaining;
    private String skuID;
    private String status;
}
