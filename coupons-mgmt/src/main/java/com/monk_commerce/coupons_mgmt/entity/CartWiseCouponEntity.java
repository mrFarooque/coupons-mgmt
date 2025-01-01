package com.monk_commerce.coupons_mgmt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "cart_wise_coupons")
@Getter
@Setter
@ToString
public class CartWiseCouponEntity extends CouponEntity {
    @Column(name = "threshold", nullable = false)
    private Double threshold;

    @Column(name = "discount", nullable = false)
    private Double discount;
}
