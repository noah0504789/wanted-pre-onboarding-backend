package com.wantedbackendassignment.api.auth.login;

import com.wantedbackendassignment.api.utils.HttpUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private final HttpUtils httpUtils;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        httpUtils.setResponseWithRedirect(
                response,
                httpUtils.createFailureResponse("Invalid email or password", HttpStatus.UNAUTHORIZED.value()),
                "/api/login"
        );
    }
}
