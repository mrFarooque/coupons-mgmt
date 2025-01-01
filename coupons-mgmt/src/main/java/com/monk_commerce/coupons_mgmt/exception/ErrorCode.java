package com.monk_commerce.coupons_mgmt.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorCode {
   private int apiErrorCode;
   private String errorCode;
}
