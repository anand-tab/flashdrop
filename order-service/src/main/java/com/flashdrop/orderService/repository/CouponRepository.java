package com.flashdrop.orderService.repository;

import com.flashdrop.orderService.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon,String> {

    Coupon findByCouponCode(String couponCode);
}
