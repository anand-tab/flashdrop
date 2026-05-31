package com.flashdrop.inventoryService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddProductReq {

    private String productCode;
    private String productName;
    private String description;
    private String brand;
    private Long categoryId;
    private List<AddSkuReq> skus;

}
