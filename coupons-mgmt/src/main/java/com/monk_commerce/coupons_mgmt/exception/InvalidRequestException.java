package com.monk_commerce.coupons_mgmt.exception;

import lombok.Getter;

@Getter
public class InvalidRequestException extends BaseException {
   public InvalidRequestException(ErrorCode errorCode, String errorMessage, String... arguments) {
      super(errorCode, errorMessage, arguments);
   }
}
