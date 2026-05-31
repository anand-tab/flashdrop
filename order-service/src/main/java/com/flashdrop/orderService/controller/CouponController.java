package com.flashdrop.orderService.controller;


import com.flashdrop.orderService.dto.CouponRequest;
import com.flashdrop.orderService.entity.Coupon;
import com.flashdrop.orderService.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RestController
@RequestMapping("/api/coupon")
public class CouponController {

    @Autowired
    private CouponService couponService;


    @PostMapping()
    public ResponseEntity<String> addCoupon(@RequestBody Coupon coupon) {
        return  ResponseEntity.ok(couponService.addCoupon(coupon));
    }


    @PostMapping("/fetchOff")
    public ResponseEntity<Double> fetchOffAmt(@RequestBody CouponRequest couponRequest) {
        try{
            double offAmt = couponService.fetchOffAmt(couponRequest);
            BigDecimal bd = new BigDecimal(Double.toString(offAmt));
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            return ResponseEntity.ok(bd.doubleValue());
        }catch(Exception e){
            return ResponseEntity.badRequest().build();
        }

    }

}
