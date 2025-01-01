package com.monk_commerce.coupons_mgmt.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
   private ErrorCode errorCode;
   private String errorMessage;
   private Object[] arguments;

   public BaseException(ErrorCode errorCode, String errorMessage, Object[] arguments) {
      this.errorCode = errorCode;
      this.errorMessage = errorMessage;
      this.arguments = arguments;
   }
}
