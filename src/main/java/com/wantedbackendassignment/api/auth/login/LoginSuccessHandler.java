package com.wantedbackendassignment.api.auth.login;

import com.wantedbackendassignment.api.auth.jwt.JwtProvider;
import com.wantedbackendassignment.api.auth.jwt.JwtVo;
import com.wantedbackendassignment.api.properties.JwtProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        JwtVo jwtInfo = JwtVo.builder()
                .email(principal.getUsername())
                .roles(authentication.getAuthorities())
                .build();

        String accessToken = jwtProvider.generateAccessToken(jwtInfo);

        setResponseWithAccessToken(response, getCookieOfAccessToken(accessToken));
    }

    private Cookie getCookieOfAccessToken(String accessTokenValue) {
        Cookie accessToken = new Cookie("access_token", accessTokenValue);
        accessToken.setHttpOnly(true);
        accessToken.setSecure(true);
        accessToken.setMaxAge((int) (60 * 60 * jwtProperties.getValidityPeriod()));

        return accessToken;
    }

    private void setResponseWithAccessToken(HttpServletResponse response, Cookie accessToken) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.addCookie(accessToken);
    }
}
