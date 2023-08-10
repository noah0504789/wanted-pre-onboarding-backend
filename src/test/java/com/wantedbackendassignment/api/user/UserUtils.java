package com.wantedbackendassignment.api.user;

public class UserUtils {

    public static User createDummyUser(String dummyEmail, String dummyPassword) {
        return User.builder()
                .email(dummyEmail)
                .password(dummyPassword)
                .build();
    }

}
