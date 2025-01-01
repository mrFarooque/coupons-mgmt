package com.monk_commerce.coupons_mgmt.service;

import com.monk_commerce.coupons_mgmt.model.*;

public interface CouponService {
    Coupon createCoupon(Coupon coupon);

    Coupon updateCoupon(Long couponId, Coupon couponRequest);

    ApplicableCoupons applicableCoupons(Cart cart);

    UpdatedCartDetails applyCoupon(Long couponId, Cart cart);

    Coupons getCoupons();

    Coupon getCouponById(Long couponId);

    void deleteCouponById(Long couponId);
}
