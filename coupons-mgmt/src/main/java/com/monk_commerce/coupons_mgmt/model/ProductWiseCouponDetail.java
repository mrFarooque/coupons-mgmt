package com.monk_commerce.coupons_mgmt.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductWiseCouponDetail {
    private Long productId;
    private Double discount;
}
