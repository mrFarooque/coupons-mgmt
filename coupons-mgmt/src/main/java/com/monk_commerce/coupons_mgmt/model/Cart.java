package com.monk_commerce.coupons_mgmt.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Cart {
    @NotNull(message = "cart is empty")
    private CartItems cart;
}