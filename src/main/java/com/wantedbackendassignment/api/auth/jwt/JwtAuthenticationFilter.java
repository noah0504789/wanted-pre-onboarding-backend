package com.wantedbackendassignment.api.auth.jwt;

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

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final HttpUtils httpUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, AuthenticationException, JwtException {
        String accessToken = httpUtils.resolveToken(request);

        jwtProvider.verify(accessToken);
        jwtProvider.setAuthenticationToContext(accessToken);

        filterChain.doFilter(request, response);
    }
}
