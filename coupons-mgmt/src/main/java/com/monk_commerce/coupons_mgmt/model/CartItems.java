package com.monk_commerce.coupons_mgmt.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class CartItems {
    @NotEmpty(message = "cart has no items")
    private List<Item> items;
}