package com.wantedbackendassignment.api.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wantedbackendassignment.api.dto.ResponseDto;
import com.wantedbackendassignment.api.dto.ValidationResult;
import com.wantedbackendassignment.api.exception.LoginInvalidException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class CustomExceptionHandlerFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (LoginInvalidException e) {
            ValidationResult errors = ValidationResult.of(e.getErrors());

            setErrorResponse(response,
                    ResponseDto.failure(errors, HttpServletResponse.SC_BAD_REQUEST));
        } catch (RuntimeException e) {
            setErrorResponse(response,
                    ResponseDto.failure("mapping error", HttpServletResponse.SC_BAD_REQUEST));
        }
    }

    private void setErrorResponse(HttpServletResponse response, ResponseDto body) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(body.getResponseCode());

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
