package com.monk_commerce.coupons_mgmt.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ErrorCodes {

   public static ErrorCode INVALID_COUPON_TYPE = new ErrorCode(1, "invalid.coupon.type");
   public static ErrorCode COUPON_NOT_FOUND = new ErrorCode(2, "coupon.not.found");
   public static ErrorCode COUPON_NOT_APPLICABLE = new ErrorCode(3, "coupon.not.applicable");

}
