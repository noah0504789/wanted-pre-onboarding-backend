package com.wantedbackendassignment.api;

import com.wantedbackendassignment.api.dto.ResponseDto;
import com.wantedbackendassignment.api.dto.ValidationResult;
import com.wantedbackendassignment.api.utils.HttpUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final HttpUtils httpUtils;

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<ValidationResult>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        ValidationResult errors = ValidationResult.of(e);

        return new ResponseEntity<>(
                httpUtils.createFailureResponse(errors, badRequest.value()),
                badRequest
        );
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ResponseDto> handleIllegalArgumentException(IllegalArgumentException e) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(
                httpUtils.createFailureResponse(e.getMessage(), badRequest.value()),
                badRequest
        );
    }
}
