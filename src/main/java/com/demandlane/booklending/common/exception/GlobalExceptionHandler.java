package com.demandlane.booklending.common.exception;

import com.demandlane.booklending.common.dto.ResponseDto;
import com.demandlane.booklending.member.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ResponseDto> handleUnauthorized(BadCredentialsException ex) {
        LOGGER.error("Authentication failed: {}", ex.getMessage(), ex);
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
        LOGGER.error("Resource not found: {}", ex.getMessage(), ex);
        ResponseDto error = new ResponseDto(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                System.currentTimeMillis(),
                null
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataInvalidException.class)
    public ResponseEntity<ResponseDto> handleInvalidException(DataInvalidException ex) {
        LOGGER.error("Data invalid: {}", ex.getMessage(), ex);
        ResponseDto error = new ResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                System.currentTimeMillis(),
                null
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        LOGGER.error("Validation failed: {}", ex.getMessage(), ex);
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        ResponseDto error = new ResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                System.currentTimeMillis(),
                errors
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}