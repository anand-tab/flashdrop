package com.flashdrop.inventoryService.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddSkuReq {
    private String skuCode;
    private String color;
    private String size;
    private BigDecimal price;
    private Integer initialStock;
}
