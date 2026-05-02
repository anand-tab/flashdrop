package com.flashdrop.orderService.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String couponId;
    private String couponCode;
    // will give the percentage or amount to be decreased from the amount.
    private int percentageOff;
    //price limit shows the list of available coupons.
    private double priceLimit;
    //Description of the coupon
    private String description;
}
