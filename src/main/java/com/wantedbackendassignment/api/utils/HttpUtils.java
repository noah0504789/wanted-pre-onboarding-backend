package com.wantedbackendassignment.api.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wantedbackendassignment.api.dto.ResponseDto;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;

@Component
@RequiredArgsConstructor
public class HttpUtils {

    public static final String BEARER_PREFIX = "Bearer ";

    private final ObjectMapper objectMapper;

    public String resolveToken(HttpServletRequest request) throws AuthenticationException {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken == null) {
            throw new InsufficientAuthenticationException("not found auth header");
        }

        if (!bearerToken.startsWith(BEARER_PREFIX)) {
            throw new InsufficientAuthenticationException("not a bearer type of auth header");
        }

        return bearerToken.substring(BEARER_PREFIX.length());
    }

    public Cookie getCookieOfAccessToken(String accessTokenValue, long validityPeriod) {
        Cookie accessToken = new Cookie("access_token", accessTokenValue);
        accessToken.setHttpOnly(true);
        accessToken.setSecure(true);
        accessToken.setMaxAge((int) (60 * 60 * validityPeriod));

        return accessToken;
    }

    public <T> ResponseDto<T> createSuccessResponse(T data, int responseCode) {
        return ResponseDto.success(data, responseCode);
    }

    public <T> ResponseDto<T> createFailureResponse(T error, int responseCode) {
        return ResponseDto.failure(error, responseCode);
    }

    public void setResponse(HttpServletResponse response, ResponseDto body) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(body.getResponseCode());

        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(body));
        writer.flush();
        writer.close();
    }

    public void setResponseWithCookie(HttpServletResponse response, ResponseDto body, Cookie accessToken) throws IOException {
        setResponse(response, body);
        response.addCookie(accessToken);
    }

    public void setResponseWithRedirect(HttpServletResponse response, ResponseDto body, String redirectPath) throws IOException {
        setResponse(response, body);
        response.setHeader("Location", redirectPath);
    }
}
