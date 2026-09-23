package com.kafka.saga.ProductService.exception;

import com.kafka.saga.CoreService.exception.ErrorResponse;
import com.kafka.saga.CoreService.exception.ProductInsufficientQuantityException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductInsufficientQuantityException.class)
    public ResponseEntity<ErrorResponse> handleProductInsufficient(
            ProductInsufficientQuantityException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .message(ex.getMessage())
                        .code(String.valueOf(HttpStatus.BAD_REQUEST))
                        .build());
    }
}