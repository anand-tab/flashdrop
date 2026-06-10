package com.flashdrop.inventoryService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductServiceReq {
    private String productId;
    private String productName;
    private String productDescription;
    private String productPrice;
    private String productImageUrl;
    private List<String> productImageUrlList;
    private String productCategory;
    private String productStatus;
    private Double rating;
}
