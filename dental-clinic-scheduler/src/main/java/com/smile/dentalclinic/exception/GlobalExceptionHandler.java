package com.smile.dentalclinic.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>>
    handleIllegalArgumentException(
            IllegalArgumentException exception) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        Map.of(
                                "error",
                                exception.getMessage()
                        )
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>>
    handleGenericException(Exception exception) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        Map.of(
                                "error",
                                "An unexpected error occurred"
                        )
                );
    }

}
