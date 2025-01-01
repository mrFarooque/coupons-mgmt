package com.monk_commerce.coupons_mgmt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "bxgy_coupons")
@Getter
@Setter
@ToString
public class BxGyCouponEntity extends CouponEntity {
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "buy_x_get_y_buy_products", // create a new table and add a entry there
            joinColumns = @JoinColumn(name = "id"), // coupon-id
            inverseJoinColumns = @JoinColumn(name = "coupon_product_id") //product-id

    )
    private List<CouponProductEntity> buyProducts;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "buy_x_get_y_get_products", // create a new table and add a entry there
            joinColumns = @JoinColumn(name = "id"), // coupon-id
            inverseJoinColumns = @JoinColumn(name = "coupon_product_id") //product-id
    )
    private List<CouponProductEntity> getProducts;

    @Column(name = "repition_limit")
    private Integer repitionLimit;
}
