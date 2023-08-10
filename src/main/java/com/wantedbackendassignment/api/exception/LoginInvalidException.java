package com.wantedbackendassignment.api.exception;

import lombok.Getter;
import org.springframework.validation.Errors;

@Getter
public class LoginInvalidException extends RuntimeException {

    private final Errors errors;

    public LoginInvalidException(Errors errors) {
        this.errors = errors;
    }
}
