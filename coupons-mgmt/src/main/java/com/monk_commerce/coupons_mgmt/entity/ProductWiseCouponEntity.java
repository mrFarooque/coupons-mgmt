package com.monk_commerce.coupons_mgmt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "product_wise_coupons")
@Getter
@Setter
@ToString
public class ProductWiseCouponEntity extends CouponEntity {
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "discount", nullable = false)
    private Double discount;
}
