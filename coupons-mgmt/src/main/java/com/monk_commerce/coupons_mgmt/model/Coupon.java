package com.monk_commerce.coupons_mgmt.model;

import com.monk_commerce.coupons_mgmt.enums.CouponType;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Coupon {
    private Long id;
    @NotNull(message = "Coupon Type is mandatory")
    private CouponType type;
    @NotNull(message = "Coupon Detail is mandatory")
    private Object details;
}
