package com.monk_commerce.coupons_mgmt.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BxGyCouponDetail {
    private List<CouponProduct> buyProducts;
    private List<CouponProduct> getProducts;
    private Integer repitionLimit;
}
