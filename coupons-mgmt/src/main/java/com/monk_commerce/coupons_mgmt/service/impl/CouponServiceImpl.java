package com.monk_commerce.coupons_mgmt.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monk_commerce.coupons_mgmt.entity.BxGyCouponEntity;
import com.monk_commerce.coupons_mgmt.entity.CartWiseCouponEntity;
import com.monk_commerce.coupons_mgmt.entity.CouponEntity;
import com.monk_commerce.coupons_mgmt.entity.ProductWiseCouponEntity;
import com.monk_commerce.coupons_mgmt.enums.CouponType;
import com.monk_commerce.coupons_mgmt.exception.InvalidRequestException;
import com.monk_commerce.coupons_mgmt.exception.ResourceNotFoundException;
import com.monk_commerce.coupons_mgmt.model.*;
import com.monk_commerce.coupons_mgmt.repository.CouponRepository;
import com.monk_commerce.coupons_mgmt.service.BxGyWiseCouponService;
import com.monk_commerce.coupons_mgmt.service.CartWiseCouponService;
import com.monk_commerce.coupons_mgmt.service.CouponService;
import com.monk_commerce.coupons_mgmt.service.ProductWiseCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.monk_commerce.coupons_mgmt.exception.ErrorCodes.COUPON_NOT_FOUND;
import static com.monk_commerce.coupons_mgmt.exception.ErrorCodes.INVALID_COUPON_TYPE;

@Service
public class CouponServiceImpl implements CouponService {
    @Autowired
    private CartWiseCouponService cartWiseCouponService;
    @Autowired
    private ProductWiseCouponService productWiseCouponService;
    @Autowired
    private BxGyWiseCouponService bxGyWiseCouponService;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private ObjectMapper mapper;

    @Override
    public Coupon createCoupon(Coupon coupon) {
        if (coupon.getType().equals(CouponType.CART_WISE)) {
            return cartWiseCouponService.createCoupon(coupon);
        } else if (coupon.getType().equals(CouponType.PRODUCT_WISE)) {
            return productWiseCouponService.createCoupon(coupon);
        } else if (coupon.getType().equals(CouponType.BXGY)) {
            return bxGyWiseCouponService.createCoupon(coupon);
        }
        throw new InvalidRequestException(INVALID_COUPON_TYPE, "Invalid coupon type");
    }

    @Override
    public Coupon updateCoupon(Long couponId, Coupon couponRequest) {
        getCouponById(couponId);
        couponRequest.setId(couponId);
        return createCoupon(couponRequest);
    }

    @Override
    public ApplicableCoupons applicableCoupons(Cart cart) {
        ApplicableCoupons applicableCoupons = new ApplicableCoupons();
        applicableCoupons.getApplicableCoupons().addAll(cartWiseCouponService.applicableCoupons(cart).getApplicableCoupons());
        applicableCoupons.getApplicableCoupons().addAll(productWiseCouponService.applicableCoupons(cart).getApplicableCoupons());
        applicableCoupons.getApplicableCoupons().addAll(bxGyWiseCouponService.applicableCoupons(cart).getApplicableCoupons());
        return applicableCoupons;
    }

    @Override
    public UpdatedCartDetails applyCoupon(Long couponId, Cart cart) {
        Coupon coupon = getCouponById(couponId);
        if (coupon.getType().equals(CouponType.CART_WISE)) {
            return cartWiseCouponService.applyCoupon(coupon, cart);
        } else if (coupon.getType().equals(CouponType.PRODUCT_WISE)) {
            return productWiseCouponService.applyCoupon(coupon, cart);
        } else if (coupon.getType().equals(CouponType.BXGY)) {
            return bxGyWiseCouponService.applyCoupon(coupon, cart);
        }
        throw new ResourceNotFoundException(COUPON_NOT_FOUND, "coupon not found");
    }

    @Override
    public Coupons getCoupons() {
        Coupons coupons = new Coupons();
        coupons.getCoupons().addAll(cartWiseCouponService.getCoupons());
        coupons.getCoupons().addAll(productWiseCouponService.getCoupons());
        coupons.getCoupons().addAll(bxGyWiseCouponService.getCoupons());
        return coupons;
    }

    @Override
    public Coupon getCouponById(Long couponId) {
        Optional<CouponEntity> couponEntityOptional = couponRepository.findById(couponId);
        if (couponEntityOptional.isEmpty() || couponEntityOptional.get().isDeleted()) {
            throw new ResourceNotFoundException(COUPON_NOT_FOUND, "coupon not found");
        }
        CouponEntity couponEntity = couponEntityOptional.get();
        if (couponEntity instanceof CartWiseCouponEntity) {
            return cartWiseCouponService.getCouponById(couponId);
        } else if (couponEntity instanceof ProductWiseCouponEntity) {
            return productWiseCouponService.getCouponById(couponId);
        } else if (couponEntity instanceof BxGyCouponEntity) {
            return bxGyWiseCouponService.getCouponById(couponId);
        }
        throw new InvalidRequestException(INVALID_COUPON_TYPE, "Invalid coupon type");
    }

    @Override
    public void deleteCouponById(Long couponId) {
        Coupon coupon = getCouponById(couponId);
        if (coupon.getType().equals(CouponType.CART_WISE)) {
            cartWiseCouponService.deleteCouponById(couponId);
            return;
        } else if (coupon.getType().equals(CouponType.PRODUCT_WISE)) {
            productWiseCouponService.deleteCouponById(couponId);
            return;
        } else if (coupon.getType().equals(CouponType.BXGY)) {
            bxGyWiseCouponService.deleteCouponById(couponId);
            return;
        }
        throw new InvalidRequestException(INVALID_COUPON_TYPE, "Invalid coupon type");
    }
}
