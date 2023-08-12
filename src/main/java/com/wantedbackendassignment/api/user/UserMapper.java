package com.wantedbackendassignment.api.user;

import com.wantedbackendassignment.api.AuthController;
import com.wantedbackendassignment.api.dto.SignUpDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = AuthController.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {
    User toUser(SignUpDto signUpDto);
}
