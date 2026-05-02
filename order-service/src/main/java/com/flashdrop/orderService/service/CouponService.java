package com.flashdrop.orderService.service;

import com.flashdrop.orderService.dto.CouponRequest;
import com.flashdrop.orderService.entity.Coupon;
import com.flashdrop.orderService.repository.CouponRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CouponService {

    @Autowired
    private  CouponRepository couponRepository;

    public String addCoupon(Coupon coupon) {

        couponRepository.save(coupon);
        return "Coupon added successfully";

    }

    public Integer fetchOffAmt(CouponRequest couponRequest) {
        Coupon coupon = couponRepository.findByCouponCode(couponRequest.getCouponCode());
        int percet = coupon.getPercentageOff();
        int cst = couponRequest.getProductPrice();

        int res = cst*percet*(couponRequest.getQuantity());
        log.info("This is the res" + res + percet);
        return res/100;
    }
}
