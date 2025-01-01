package com.monk_commerce.coupons_mgmt.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monk_commerce.coupons_mgmt.entity.CouponEntity;
import com.monk_commerce.coupons_mgmt.enums.CouponType;
import com.monk_commerce.coupons_mgmt.exception.InvalidRequestException;
import com.monk_commerce.coupons_mgmt.exception.ResourceNotFoundException;
import com.monk_commerce.coupons_mgmt.model.*;
import com.monk_commerce.coupons_mgmt.entity.CartWiseCouponEntity;
import com.monk_commerce.coupons_mgmt.repository.CartWiseCouponRepository;
import com.monk_commerce.coupons_mgmt.service.CartWiseCouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.monk_commerce.coupons_mgmt.exception.ErrorCodes.COUPON_NOT_FOUND;

@Service
@Slf4j
public class CartWiseCouponServiceImpl implements CartWiseCouponService {

    @Autowired
    private CartWiseCouponRepository cartWiseCouponRepository;
    @Autowired
    private ObjectMapper mapper;

    @Override
    public Coupon createCoupon(Coupon couponRequest) {
        CartWiseCouponDetail cartWiseCouponDetail = mapper.convertValue(couponRequest.getDetails(), CartWiseCouponDetail.class);
        CartWiseCouponEntity cartWiseCouponEntity = new CartWiseCouponEntity();
        if (couponRequest.getId() != null) {
            cartWiseCouponEntity.setId(couponRequest.getId());
        }
        cartWiseCouponEntity.setType(CouponType.CART_WISE);
        cartWiseCouponEntity.setDiscount(cartWiseCouponDetail.getDiscount());
        cartWiseCouponEntity.setThreshold(cartWiseCouponDetail.getThreshold());
        CartWiseCouponEntity savedCartWiseCouponEntity = cartWiseCouponRepository.save(cartWiseCouponEntity);
        return mapToCoupon(savedCartWiseCouponEntity);
    }

    @Override
    public Coupon getCouponById(Long couponId) {
        Optional<CartWiseCouponEntity> cartWiseCouponEntityOptional = cartWiseCouponRepository.findById(couponId);
        if (cartWiseCouponEntityOptional.isEmpty()) {
            throw new ResourceNotFoundException(COUPON_NOT_FOUND, "coupon not found");
        }
        return mapToCoupon(cartWiseCouponEntityOptional.get());
    }

    @Override
    public void deleteCouponById(Long couponId) {
        Optional<CartWiseCouponEntity> couponEntityOptional = cartWiseCouponRepository.findById(couponId);
        CartWiseCouponEntity couponEntity = couponEntityOptional.get();
        couponEntity.setDeleted(true);
        cartWiseCouponRepository.save(couponEntity);
    }

    @Override
    public List<Coupon> getCoupons() {
        return cartWiseCouponRepository.findByIsDeletedFalse().stream()
                .map(this::mapToCoupon)
                .collect(Collectors.toList());
    }

    @Override
    public ApplicableCoupons applicableCoupons(Cart cart) {
        ApplicableCoupons applicableCoupons = new ApplicableCoupons();
        applicableCoupons.setApplicableCoupons(
                getCoupons().stream()
                        .filter(coupon -> isCouponApplicable(coupon, cart))
                        .map(coupon -> {
                            CartWiseCouponDetail cartWiseCoupon = mapper.convertValue(coupon.getDetails(), CartWiseCouponDetail.class);
                            return new ApplicableCoupon(coupon.getId(), coupon.getType(), cartWiseCoupon.getDiscount());
                        })
                        .collect(Collectors.toList())
        );
        return applicableCoupons;
    }

    private boolean isCouponApplicable(Coupon coupon, Cart cart) {
        double cartTotalPrice = getTotalPriceOfCart(cart);
        CartWiseCouponDetail cartWiseCoupon = mapper.convertValue(coupon.getDetails(), CartWiseCouponDetail.class);
        return cartTotalPrice >= cartWiseCoupon.getThreshold();

    }

    @Override
    public UpdatedCartDetails applyCoupon(Coupon coupon, Cart cart) {
        CartWiseCouponDetail cartWiseCoupon = mapper.convertValue(coupon.getDetails(), CartWiseCouponDetail.class);
        Double totalPrice = getTotalPriceOfCart(cart);
        UpdatedCartDetails updatedCartDetails = new UpdatedCartDetails();
        updatedCartDetails.setItems(cart.getCart().getItems().stream()
                .map(item -> new ProductDiscountDetail(item.getProductId(), item.getQuantity(), item.getPrice(), 0d))
                .collect(Collectors.toList()));
        updatedCartDetails.setTotalPrice(totalPrice);
        if (totalPrice >= cartWiseCoupon.getThreshold()) {
            double totalDiscount = totalPrice * cartWiseCoupon.getDiscount() / 100;
            updatedCartDetails.setTotalDiscount(totalDiscount);
            updatedCartDetails.setFinalPrice(totalPrice - totalDiscount);
        }
        return updatedCartDetails;
    }

    private Double getTotalPriceOfCart(Cart cart) {
        return cart.getCart().getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    private Coupon mapToCoupon(CartWiseCouponEntity cartWiseCouponEntity) {
        CartWiseCouponDetail cartWiseCouponDetail = new CartWiseCouponDetail(cartWiseCouponEntity.getThreshold(), cartWiseCouponEntity.getDiscount());
        return new Coupon(cartWiseCouponEntity.getId(), cartWiseCouponEntity.getType(), cartWiseCouponDetail);
    }
}
