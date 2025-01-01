package com.monk_commerce.coupons_mgmt.model;

import com.monk_commerce.coupons_mgmt.enums.CouponType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ApplicableCoupons {
    List<ApplicableCoupon> applicableCoupons = new ArrayList<>();
}
