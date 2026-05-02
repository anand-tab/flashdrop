package com.flashdrop.orderService.controller;


import com.flashdrop.orderService.dto.CouponRequest;
import com.flashdrop.orderService.entity.Coupon;
import com.flashdrop.orderService.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Integer> fetchOffAmt(@RequestBody CouponRequest couponRequest) {
        try{
            int integer = couponService.fetchOffAmt(couponRequest);
            return ResponseEntity.ok(integer);
        }catch(Exception e){
            return ResponseEntity.badRequest().build();
        }

    }

}
