package com.monk_commerce.coupons_mgmt.model;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProductDiscountDetail {
    private Long productId;
    private Long quantity;
    private Double price;
    private Double totalDiscount;
}
