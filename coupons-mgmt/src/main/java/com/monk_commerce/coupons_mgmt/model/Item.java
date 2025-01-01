package com.monk_commerce.coupons_mgmt.model;

import lombok.Data;

@Data
public class Item {
    private Long productId;
    private Long quantity;
    private Double price;
}