package com.wantedbackendassignment.api.user;

import com.wantedbackendassignment.api.dto.LoginDto;
import com.wantedbackendassignment.api.dto.SignUpDto;

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
}
