package com.monk_commerce.coupons_mgmt.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "coupon_product")
@Getter
@Setter
@ToString
public class CouponProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "quantity", nullable = false)
    private Long quantity;
}
