package com.monk_commerce.coupons_mgmt.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends BaseException {
   public ResourceNotFoundException(ErrorCode errorCode, String errorMessage, String... args) {
      super(errorCode, errorMessage, args);
   }
}
