package com.wantedbackendassignment.api;

import com.wantedbackendassignment.api.dto.LoginDto;
import com.wantedbackendassignment.api.dto.SignUpDto;
import com.wantedbackendassignment.api.user.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

public class UserUtils {

    public static User createDummyUser(String dummyEmail, String dummyPassword) {
        return User.builder()
                .email(dummyEmail)
                .password(dummyPassword)
                .build();
    }

    public static SignUpDto createSignUpDto(String dummyEmail, String dummyPassword) {
        return new SignUpDto(dummyEmail, dummyPassword);
    }

    public static LoginDto createLoginDto(String dummyEmail, String dummyPassword) {
        return new LoginDto(dummyEmail, dummyPassword);
    }

    public static Authentication createAuthentication(Object principal, String dummyPassword) {
        return new UsernamePasswordAuthenticationToken(principal, dummyPassword);
    }
}
