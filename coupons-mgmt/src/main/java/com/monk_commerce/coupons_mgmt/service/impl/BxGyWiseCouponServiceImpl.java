package com.monk_commerce.coupons_mgmt.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monk_commerce.coupons_mgmt.entity.BxGyCouponEntity;
import com.monk_commerce.coupons_mgmt.entity.CouponProductEntity;
import com.monk_commerce.coupons_mgmt.entity.ProductWiseCouponEntity;
import com.monk_commerce.coupons_mgmt.enums.CouponType;
import com.monk_commerce.coupons_mgmt.exception.InvalidRequestException;
import com.monk_commerce.coupons_mgmt.exception.ResourceNotFoundException;
import com.monk_commerce.coupons_mgmt.model.*;
import com.monk_commerce.coupons_mgmt.repository.BxGyCouponRepository;
import com.monk_commerce.coupons_mgmt.service.BxGyWiseCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.monk_commerce.coupons_mgmt.exception.ErrorCodes.COUPON_NOT_APPLICABLE;
import static com.monk_commerce.coupons_mgmt.exception.ErrorCodes.COUPON_NOT_FOUND;

@Service
public class BxGyWiseCouponServiceImpl implements BxGyWiseCouponService {
    @Autowired
    private BxGyCouponRepository bxGyCouponRepository;
    @Autowired
    private ObjectMapper mapper;

    @Override
    public Coupon createCoupon(Coupon couponRequest) {
        BxGyCouponDetail bxGyCouponDetail = mapper.convertValue(couponRequest.getDetails(), BxGyCouponDetail.class);
        BxGyCouponEntity bxGyCouponEntity = new BxGyCouponEntity();
        if (couponRequest.getId() != null) {
            bxGyCouponEntity.setId(couponRequest.getId());
        }
        bxGyCouponEntity.setType(CouponType.BXGY);
        List<CouponProductEntity> buyProducts = this.convertProductEntityList(bxGyCouponDetail.getBuyProducts());
        List<CouponProductEntity> getProducts = this.convertProductEntityList(bxGyCouponDetail.getGetProducts());
        bxGyCouponEntity.setBuyProducts(buyProducts);
        bxGyCouponEntity.setGetProducts(getProducts);
        bxGyCouponEntity.setRepitionLimit(bxGyCouponDetail.getRepitionLimit());
        BxGyCouponEntity savedBxGyCouponEntity = bxGyCouponRepository.save(bxGyCouponEntity);
        return mapToCoupon(savedBxGyCouponEntity);
    }

    @Override
    public Coupon getCouponById(Long couponId) {
        Optional<BxGyCouponEntity> bxGyCouponEntityOptional = bxGyCouponRepository.findById(couponId);
        if (bxGyCouponEntityOptional.isEmpty()) {
            throw new RuntimeException("no coupon");
        }
        return mapToCoupon(bxGyCouponEntityOptional.get());
    }

    @Override
    public void deleteCouponById(Long couponId) {
        Optional<BxGyCouponEntity> bxGyCouponEntity = bxGyCouponRepository.findById(couponId);
        if (bxGyCouponEntity.isEmpty()) {
            throw new ResourceNotFoundException(COUPON_NOT_FOUND, "coupon not found");
        }
        BxGyCouponEntity couponEntity = bxGyCouponEntity.get();
        couponEntity.setDeleted(true);
        bxGyCouponRepository.save(couponEntity);
    }

    @Override
    public List<Coupon> getCoupons() {
        return bxGyCouponRepository.findByIsDeletedFalse().stream()
                .map(this::mapToCoupon)
                .collect(Collectors.toList());
    }

    @Override
    public ApplicableCoupons applicableCoupons(Cart cart) {
        ApplicableCoupons applicableCoupons = new ApplicableCoupons();
        applicableCoupons.setApplicableCoupons(
                getCoupons().stream()
                .filter(coupon -> {
                    try {
                        isCouponApplicable(coupon, cart);
                        return true;
                    } catch (InvalidRequestException ex) {
                        return false;
                    }
                })
                .map(coupon -> new ApplicableCoupon(coupon.getId(), coupon.getType(), applicableDiscount(coupon, cart)))
                .collect(Collectors.toList())
        );
        return applicableCoupons;
    }

    private double applicableDiscount(Coupon coupon, Cart cart) {
        BxGyCouponDetail bxGyCouponDetail = mapper.convertValue(coupon.getDetails(), BxGyCouponDetail.class);
        List<CouponProduct> getCouponProducts = bxGyCouponDetail.getGetProducts();
        int repitionLimit = Math.min(maxRepitionLimit(coupon, cart), bxGyCouponDetail.getRepitionLimit());
        AtomicReference<Double> totalCartDiscount = new AtomicReference<>(0d);
        cart.getCart().getItems().forEach(item -> {
            getCouponProducts.forEach(getProduct -> {
                if (item.getProductId().equals(getProduct.getProductId())) {
                    double priceOfOneProduct = item.getPrice();
                    long quantityFree = Math.min(item.getQuantity(), getProduct.getQuantity() * repitionLimit);
                    double totalDiscount = priceOfOneProduct * quantityFree;
                    totalCartDiscount.updateAndGet(v -> v + totalDiscount);
                }
            });
        });
        return totalCartDiscount.get();
    }

    @Override
    public UpdatedCartDetails applyCoupon(Coupon coupon, Cart cart) {
        BxGyCouponDetail bxGyCouponDetail = mapper.convertValue(coupon.getDetails(), BxGyCouponDetail.class);
        UpdatedCartDetails updatedCartDetails = new UpdatedCartDetails();
        double totalPrice = getTotalPriceOfCart(cart);

        List<CouponProduct> getCouponProducts = bxGyCouponDetail.getGetProducts();
        isCouponApplicable(coupon, cart);

        List<ProductDiscountDetail> updatedCartItems = new ArrayList<>();
        updatedCartDetails.setItems(updatedCartItems);
        AtomicReference<Double> totalCartDiscount = new AtomicReference<>(0d);
        int repitionLimit = Math.min(maxRepitionLimit(coupon, cart), bxGyCouponDetail.getRepitionLimit());
        cart.getCart().getItems().forEach(item -> {
            getCouponProducts.forEach(getProduct -> {
                if (item.getProductId().equals(getProduct.getProductId())) {
                    double priceOfOneProduct = item.getPrice();
                    long quantityFree = Math.min(item.getQuantity(), getProduct.getQuantity() * repitionLimit);
                    double totalDiscount = priceOfOneProduct * quantityFree;
                    totalCartDiscount.updateAndGet(v -> v + totalDiscount);
                    updatedCartItems.add(new ProductDiscountDetail(item.getProductId(), item.getQuantity(), item.getPrice(), totalDiscount));
                } else {
                    updatedCartItems.add(new ProductDiscountDetail(item.getProductId(), item.getQuantity(), item.getPrice(), 0d));
                }
            });
        });

        updatedCartDetails.setItems(updatedCartItems);
        updatedCartDetails.setTotalPrice(totalPrice);
        updatedCartDetails.setTotalDiscount(totalCartDiscount.get());
        updatedCartDetails.setFinalPrice(totalPrice - totalCartDiscount.get());
        return updatedCartDetails;
    }

    private int maxRepitionLimit(Coupon coupon, Cart cart) {
        AtomicInteger maxRepitionLimit = new AtomicInteger();
        BxGyCouponDetail bxGyCouponDetail = mapper.convertValue(coupon.getDetails(), BxGyCouponDetail.class);
        List<CouponProduct> couponBuyProducts = bxGyCouponDetail.getBuyProducts();
        cart.getCart().getItems().forEach(item -> couponBuyProducts.forEach(couponBuyProduct -> {
            if (item.getProductId().equals(couponBuyProduct.getProductId())) {
                if (item.getQuantity() >= couponBuyProduct.getQuantity()) {
                    maxRepitionLimit.getAndIncrement();
                }
            }
        }));
        return maxRepitionLimit.get();
    }

    private void isCouponApplicable(Coupon coupon, Cart cart) {
        BxGyCouponDetail bxGyCouponDetail = mapper.convertValue(coupon.getDetails(), BxGyCouponDetail.class);
        List<CouponProduct> couponBuyProducts = bxGyCouponDetail.getBuyProducts();
        cart.getCart().getItems().forEach(item -> couponBuyProducts.forEach(couponBuyProduct -> {
            if (item.getProductId().equals(couponBuyProduct.getProductId())) {
                if (item.getQuantity() < couponBuyProduct.getQuantity()) {
                    throw new InvalidRequestException(COUPON_NOT_APPLICABLE, "Coupon not applicable");
                }
            }
        }));
    }

    private Coupon mapToCoupon(BxGyCouponEntity savedBxGyCouponEntity) {
        BxGyCouponDetail bxGyCouponDetail = new BxGyCouponDetail(convertProductList(savedBxGyCouponEntity.getBuyProducts()), convertProductList(savedBxGyCouponEntity.getGetProducts()), savedBxGyCouponEntity.getRepitionLimit());
        return new Coupon(savedBxGyCouponEntity.getId(), savedBxGyCouponEntity.getType(), bxGyCouponDetail);
    }

    private List<CouponProductEntity> convertProductEntityList(List<CouponProduct> couponProducts) {
        return couponProducts.stream()
                .map(product -> {
                    CouponProductEntity couponProductEntity = new CouponProductEntity();
                    couponProductEntity.setProductId(product.getProductId());
                    couponProductEntity.setQuantity(product.getQuantity());
                    return couponProductEntity;
                })
                .collect(Collectors.toList());
    }

    private List<CouponProduct> convertProductList(List<CouponProductEntity> couponProductEntities) {
        return couponProductEntities.stream()
                .map(couponProductEntity -> new CouponProduct(couponProductEntity.getProductId(), couponProductEntity.getQuantity()))
                .collect(Collectors.toList());
    }

    private Double getTotalPriceOfCart(Cart cart) {
        return cart.getCart().getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }
}
