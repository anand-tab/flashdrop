package com.flashdrop.productService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private String productId;
    private String productName;
    private String productDescription;
    private String productPrice;
    private String productImageUrl;
    private String productCategory;
    private String productStatus;
    private Integer rating;
}
