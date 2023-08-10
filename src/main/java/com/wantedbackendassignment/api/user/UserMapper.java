package com.wantedbackendassignment.api.user;

import com.wantedbackendassignment.api.dto.SignUpDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = UserService.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {
    User toUser(SignUpDto signUpDto);
}
