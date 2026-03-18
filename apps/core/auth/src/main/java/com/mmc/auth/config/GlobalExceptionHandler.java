package com.mmc.auth.config;


import com.mmc.auth.domain.exceptions.InvalidJwt;
import com.mmc.auth.domain.exceptions.InvalidUserCredentials;
import com.mmc.auth.domain.exceptions.UsernameAlreadyExists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidUserCredentials.class)
    public ResponseEntity<String> exceptionResponseEntity(InvalidUserCredentials iuc) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(iuc.getMessage());
    }

    @ExceptionHandler(InvalidJwt.class)
    public ResponseEntity<String> exceptionResponseEntity(InvalidJwt iuc) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(iuc.getMessage());
    }

    @ExceptionHandler(UsernameAlreadyExists.class)
    public ResponseEntity<String> exceptionResponseEntity(UsernameAlreadyExists iuc) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(iuc.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> exceptionResponseEntity(MethodArgumentNotValidException iuc) {
        Map<String, String> errors = new HashMap<>();
        iuc.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> exceptionResponseEntity(RuntimeException iuc) {
        log.error(iuc.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unknown exceptions. Please retry later or write support");
    }
}
