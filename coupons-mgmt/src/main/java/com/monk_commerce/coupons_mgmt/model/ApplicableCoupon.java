package com.monk_commerce.coupons_mgmt.model;

import com.monk_commerce.coupons_mgmt.enums.CouponType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApplicableCoupon {
    private Long couponId;
    private CouponType type;
    private Double discount;
}
