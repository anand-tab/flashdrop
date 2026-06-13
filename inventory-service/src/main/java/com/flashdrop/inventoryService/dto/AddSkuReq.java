package com.flashdrop.inventoryService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddSkuReq {
    private String skuCode;
    private String color;
    private String size;
    private String productImageUrl;
    private List<String> productImageUrlList;
    private String material;
    private String price;
    private Integer initialStock;
}
