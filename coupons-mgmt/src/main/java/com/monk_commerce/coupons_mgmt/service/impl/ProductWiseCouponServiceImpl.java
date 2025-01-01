package com.monk_commerce.coupons_mgmt.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monk_commerce.coupons_mgmt.entity.ProductWiseCouponEntity;
import com.monk_commerce.coupons_mgmt.exception.ResourceNotFoundException;
import com.monk_commerce.coupons_mgmt.model.*;
import com.monk_commerce.coupons_mgmt.repository.ProductWiseCouponRepository;
import com.monk_commerce.coupons_mgmt.service.ProductWiseCouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.monk_commerce.coupons_mgmt.exception.ErrorCodes.COUPON_NOT_FOUND;

@Service
@Slf4j
public class ProductWiseCouponServiceImpl implements ProductWiseCouponService {

    @Autowired
    private ProductWiseCouponRepository productWiseCouponRepository;
    @Autowired
    private ObjectMapper mapper;

    @Override
    public Coupon createCoupon(Coupon couponRequest) {
        ProductWiseCouponDetail productWiseCouponDetail = mapper.convertValue(couponRequest.getDetails(), ProductWiseCouponDetail.class);
        ProductWiseCouponEntity productWiseCouponEntity = new ProductWiseCouponEntity();
        if (couponRequest.getId() != null) {
            productWiseCouponEntity.setId(couponRequest.getId());
        }
        productWiseCouponEntity.setType(couponRequest.getType());
        productWiseCouponEntity.setDiscount(productWiseCouponDetail.getDiscount());
        productWiseCouponEntity.setProductId(productWiseCouponDetail.getProductId());
        ProductWiseCouponEntity savedProductWiseCouponEntity = productWiseCouponRepository.save(productWiseCouponEntity);
        return mapToCoupon(savedProductWiseCouponEntity);
    }

    @Override
    public Coupon getCouponById(Long couponId) {
        Optional<ProductWiseCouponEntity> productWiseCouponEntityOptional = productWiseCouponRepository.findById(couponId);
        if (productWiseCouponEntityOptional.isEmpty()) {
            throw new ResourceNotFoundException(COUPON_NOT_FOUND, "coupon not found");
        }
        return mapToCoupon(productWiseCouponEntityOptional.get());
    }

    @Override
    public void deleteCouponById(Long couponId) {
        Optional<ProductWiseCouponEntity> productWiseCouponEntityOptional = productWiseCouponRepository.findById(couponId);
        if (productWiseCouponEntityOptional.isEmpty()) {
            throw new ResourceNotFoundException(COUPON_NOT_FOUND, "coupon not found");
        }
        ProductWiseCouponEntity couponEntity = productWiseCouponEntityOptional.get();
        couponEntity.setDeleted(true);
        productWiseCouponRepository.save(couponEntity);
    }

    @Override
    public List<Coupon> getCoupons() {
        return productWiseCouponRepository.findByIsDeletedFalse().stream()
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
                            ProductWiseCouponDetail productWiseCouponDetail = mapper.convertValue(coupon.getDetails(), ProductWiseCouponDetail.class);
                            return new ApplicableCoupon(coupon.getId(), coupon.getType(), productWiseCouponDetail.getDiscount());
                        })
                        .collect(Collectors.toList())
        );
        return applicableCoupons;
    }

    private boolean isCouponApplicable(Coupon coupon, Cart cart) {
        ProductWiseCouponDetail productWiseCouponDetail = mapper.convertValue(coupon.getDetails(), ProductWiseCouponDetail.class);
        return cart.getCart().getItems().stream()
                .anyMatch(item -> item.getProductId().equals(productWiseCouponDetail.getProductId()));
    }


    @Override
    public UpdatedCartDetails applyCoupon(Coupon coupon, Cart cart) {
        ProductWiseCouponDetail productWiseCouponDetail = mapper.convertValue(coupon.getDetails(), ProductWiseCouponDetail.class);
        UpdatedCartDetails updatedCartDetails = new UpdatedCartDetails();
        AtomicReference<Double> totalDiscount = new AtomicReference<>(0d);
        updatedCartDetails.setItems(
                cart.getCart().getItems().stream().map(item -> {
                    if (item.getProductId().equals(productWiseCouponDetail.getProductId())) {
                        double discountOnOneProduct = item.getPrice() * productWiseCouponDetail.getDiscount() / 100;
                        totalDiscount.updateAndGet(v -> v + discountOnOneProduct * item.getQuantity());
                        return new ProductDiscountDetail(item.getProductId(), item.getQuantity(), item.getPrice() - discountOnOneProduct, discountOnOneProduct * item.getQuantity());
                    }
                    return new ProductDiscountDetail(item.getProductId(), item.getQuantity(), item.getPrice(), 0d);
                }).collect(Collectors.toList())
        );
        Double totalPrice = getTotalPriceOfCart(cart);
        updatedCartDetails.setTotalPrice(totalPrice);
        updatedCartDetails.setTotalDiscount(totalDiscount.get());
        updatedCartDetails.setFinalPrice(totalPrice - totalDiscount.get());
        return updatedCartDetails;
    }

    private Coupon mapToCoupon(ProductWiseCouponEntity productWiseCouponEntity) {
        ProductWiseCouponDetail productWiseCouponDetail = new ProductWiseCouponDetail(productWiseCouponEntity.getProductId(), productWiseCouponEntity.getDiscount());
        return new Coupon(productWiseCouponEntity.getId(), productWiseCouponEntity.getType(), productWiseCouponDetail);
    }

    private Double getTotalPriceOfCart(Cart cart) {
        return cart.getCart().getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }
}
