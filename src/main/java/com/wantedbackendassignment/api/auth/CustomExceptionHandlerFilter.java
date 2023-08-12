package com.wantedbackendassignment.api.auth;

import com.wantedbackendassignment.api.dto.ValidationResult;
import com.wantedbackendassignment.api.exception.LoginInvalidException;
import com.wantedbackendassignment.api.utils.HttpUtils;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class CustomExceptionHandlerFilter extends OncePerRequestFilter {

    private final HttpUtils httpUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (LoginInvalidException e) {
            ValidationResult errors = ValidationResult.of(e.getErrors());

            httpUtils.setResponse(
                    response,
                    httpUtils.createFailureResponse(errors, HttpServletResponse.SC_BAD_REQUEST)
            );
        } catch (AuthenticationException | JwtException e) {
            httpUtils.setResponseWithRedirect(
                    response,
                    httpUtils.createFailureResponse(e.getMessage(), HttpServletResponse.SC_UNAUTHORIZED),
                    "/api/login"
            );
        } catch (RuntimeException e) {
            httpUtils.setResponse(
                    response,
                    httpUtils.createFailureResponse("mapping error", HttpServletResponse.SC_BAD_REQUEST)
            );
        }
    }
}
