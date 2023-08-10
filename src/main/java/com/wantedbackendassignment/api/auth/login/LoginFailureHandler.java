package com.wantedbackendassignment.api.auth.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wantedbackendassignment.api.dto.ResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;

@Component
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        setResponseWithLocation(
                response,
                createFailureResponse("Invalid email or password", HttpStatus.UNAUTHORIZED),
                getRedirectUri(request, "/api/login")
        );
    }

    private void setResponseWithLocation(HttpServletResponse response, ResponseDto body, String redirectPath) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setHeader("Location", redirectPath);
        response.setStatus(body.getResponseCode());

        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(body));
        writer.flush();
        writer.close();
    }

    private <T> ResponseDto<T> createFailureResponse(T error, HttpStatus status) {
        return ResponseDto.failure(error, status.value());
    }

    private String getRedirectUri(HttpServletRequest request, String redirectPath) {
        URI currentUri = URI.create(request.getRequestURI());

        return UriComponentsBuilder.fromUri(currentUri)
                .path(redirectPath)
                .build().toString();
    }
}
