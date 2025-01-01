package com.monk_commerce.coupons_mgmt.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CouponProduct {
    private Long productId;
    private Long quantity;
}
