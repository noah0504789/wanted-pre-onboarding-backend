package com.wantedbackendassignment.api.user;


import com.wantedbackendassignment.api.dto.SignUpDto;

public interface IUserService {

    User signUp(SignUpDto signUpDto);

    boolean isEqualPassword(String rawPassword, String hashPassword);
}
