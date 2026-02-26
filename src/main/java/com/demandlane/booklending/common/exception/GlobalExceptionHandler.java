package com.demandlane.booklending.common.exception;

import com.demandlane.booklending.common.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ResponseDto> handleUnauthorized(BadCredentialsException ex) {
        ResponseDto error = new ResponseDto(
                HttpStatus.UNAUTHORIZED.value(),
                "Invalid username or password",
                System.currentTimeMillis(),
                null
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseDto> handleNotFoundException(ResourceNotFoundException ex) {
        ResponseDto error = new ResponseDto(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                System.currentTimeMillis(),
                null
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

}