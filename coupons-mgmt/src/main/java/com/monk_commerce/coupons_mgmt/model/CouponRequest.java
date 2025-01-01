package com.monk_commerce.coupons_mgmt.model;

import com.monk_commerce.coupons_mgmt.enums.CouponType;
import lombok.Data;

@Data
public class CouponRequest {
    private CouponType type;
    private Object details;
}
