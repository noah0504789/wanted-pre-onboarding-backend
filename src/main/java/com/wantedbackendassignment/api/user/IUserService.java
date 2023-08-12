package com.wantedbackendassignment.api.user;


import com.wantedbackendassignment.api.dto.SignUpDto;

public interface IUserService {

    User signUp(User newUser);

    boolean isEqualPassword(String rawPassword, String hashPassword);
}
