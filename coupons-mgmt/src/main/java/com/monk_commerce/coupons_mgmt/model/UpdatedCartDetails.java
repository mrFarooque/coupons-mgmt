package com.monk_commerce.coupons_mgmt.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class UpdatedCartDetails {
    private List<ProductDiscountDetail> items;
    private Double totalPrice;
    private Double totalDiscount;
    private Double finalPrice;
}
