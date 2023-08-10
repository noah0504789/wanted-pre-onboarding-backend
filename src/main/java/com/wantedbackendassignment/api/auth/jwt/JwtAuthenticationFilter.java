package com.wantedbackendassignment.api.auth.jwt;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String BEARER_PREFIX = "Bearer ";
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, AuthenticationException, JwtException {
        String accessToken = resolveToken(request);

        jwtProvider.verify(accessToken);
        jwtProvider.setAuthenticationToContext(accessToken);

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) throws AuthenticationException {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken == null) {
            throw new InsufficientAuthenticationException("not found auth header");
        }

        if (!bearerToken.startsWith(BEARER_PREFIX)) {
            throw new InsufficientAuthenticationException("not a bearer type of auth header");
        }

        return bearerToken.substring(BEARER_PREFIX.length());
    }
}
