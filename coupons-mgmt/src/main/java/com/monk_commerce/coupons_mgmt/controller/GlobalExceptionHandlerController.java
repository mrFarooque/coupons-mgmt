package com.monk_commerce.coupons_mgmt.controller;

import com.monk_commerce.coupons_mgmt.exception.ErrorMessage;
import lombok.AllArgsConstructor;
import com.monk_commerce.coupons_mgmt.exception.ResourceNotFoundException;
import com.monk_commerce.coupons_mgmt.exception.InvalidRequestException;
import com.monk_commerce.coupons_mgmt.exception.UnknownException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@RestControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandlerController {

   @Autowired
   private MessageSource messageSource;

   @ExceptionHandler(value = { InvalidRequestException.class })
   @ResponseStatus(value = HttpStatus.BAD_REQUEST)
   public ErrorMessage invalidRequestException(InvalidRequestException ex, Locale locale) {
      return new ErrorMessage(400, ex.getErrorCode().getApiErrorCode(),
            messageSource.getMessage(ex.getErrorCode().getErrorCode(), ex.getArguments(), locale));
   }

   @ExceptionHandler(value = { ResourceNotFoundException.class })
   @ResponseStatus(value = HttpStatus.NOT_FOUND)
   public ErrorMessage resourceNotFoundException(ResourceNotFoundException ex, Locale locale) {
      return new ErrorMessage(404, ex.getErrorCode().getApiErrorCode(),
            messageSource.getMessage(ex.getErrorCode().getErrorCode(), ex.getArguments(), locale));
   }

   @ExceptionHandler(value = { UnknownException.class })
   @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
   public ErrorMessage unknownException(UnknownException ex, Locale locale) {
      return new ErrorMessage(500, ex.getErrorCode().getApiErrorCode(),
            messageSource.getMessage(ex.getErrorCode().getErrorCode(), null, locale));
   }
}
