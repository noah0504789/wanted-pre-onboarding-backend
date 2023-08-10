package com.wantedbackendassignment.api;

import com.wantedbackendassignment.api.dto.ResponseDto;
import com.wantedbackendassignment.api.dto.ValidationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<ValidationResult>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ValidationResult errors = ValidationResult.of(e);

        HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        return createClientErrorResponse(errors, badRequest);
    }

    private <T> ResponseEntity<ResponseDto<T>> createClientErrorResponse(T error, HttpStatus status) {
        ResponseDto<T> response = ResponseDto.failure(error, status.value());

        return new ResponseEntity<>(response, status);
    }
}
