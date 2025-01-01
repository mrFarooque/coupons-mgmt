package com.monk_commerce.coupons_mgmt.service;

import com.monk_commerce.coupons_mgmt.model.ApplicableCoupons;
import com.monk_commerce.coupons_mgmt.model.Cart;
import com.monk_commerce.coupons_mgmt.model.Coupon;
import com.monk_commerce.coupons_mgmt.model.UpdatedCartDetails;

import java.util.List;

public interface BxGyWiseCouponService {
    Coupon createCoupon(Coupon couponRequest);
    Coupon getCouponById(Long couponId);
    void deleteCouponById(Long couponId);
    List<Coupon> getCoupons();
    ApplicableCoupons applicableCoupons(Cart cart);
    UpdatedCartDetails applyCoupon(Coupon coupon, Cart cart);
}
