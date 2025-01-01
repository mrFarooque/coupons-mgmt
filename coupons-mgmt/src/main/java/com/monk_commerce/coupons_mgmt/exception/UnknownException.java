package com.monk_commerce.coupons_mgmt.exception;

public class UnknownException extends BaseException {
   public UnknownException(ErrorCode errorCode, String errorMessage, String string) {
      super(errorCode, errorMessage, null);
   }
}
