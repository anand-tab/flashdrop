package com.flashdrop.orderService.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponRequest {

    private String couponCode;
    private double productPrice;
    private String productId;
    private int quantity;

}
