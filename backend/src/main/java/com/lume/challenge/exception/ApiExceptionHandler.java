package com.lume.challenge.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ErrorResponse> handleApi(ApiException ex, HttpServletRequest req) {
    return ResponseEntity.status(ex.getStatus())
        .body(new ErrorResponse(
            Instant.now(),
            ex.getStatus().value(),
            ex.getStatus().getReasonPhrase(),
            ex.getMessage(),
            req.getRequestURI(),
            List.of()
        ));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
    List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors()
        .stream()
        .map(fe -> new ErrorResponse.FieldError(fe.getField(), fe.getDefaultMessage()))
        .toList();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(
            Instant.now(),
            400,
            "Bad Request",
            "Validation error",
            req.getRequestURI(),
            fieldErrors
        ));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest req) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse(
            Instant.now(),
            500,
            "Internal Server Error",
            "Unexpected error",
            req.getRequestURI(),
            List.of()
        ));
  }
}
