package com.monk_commerce.coupons_mgmt.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartWiseCouponDetail {
    private Double threshold;
    private Double discount;
}
