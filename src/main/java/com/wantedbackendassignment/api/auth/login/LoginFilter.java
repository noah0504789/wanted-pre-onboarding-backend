package com.wantedbackendassignment.api.auth.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wantedbackendassignment.api.dto.LoginDto;
import com.wantedbackendassignment.api.exception.LoginInvalidException;
import com.wantedbackendassignment.api.utils.HttpUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;

import java.io.IOException;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;
    private final Validator validator;

    public LoginFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper, Validator validator) {
        super(authenticationManager);
        this.objectMapper = objectMapper;
        this.validator = validator;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        LoginDto loginDto = parseLogin(request);
        validateLogin(loginDto);

        return authenticate(request, loginDto);
    }

    private LoginDto parseLogin(HttpServletRequest request) {
        try {
            return objectMapper.readValue(request.getInputStream(), LoginDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void validateLogin(LoginDto loginDto) throws LoginInvalidException {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(loginDto, "loginDto");
        validator.validate(loginDto, errors);
        if (errors.hasErrors()) {
            throw new LoginInvalidException(errors);
        }
    }

    private Authentication authenticate(HttpServletRequest request, LoginDto loginDto) {
        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password());
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
