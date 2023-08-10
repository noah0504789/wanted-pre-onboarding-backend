package com.wantedbackendassignment.api.auth.login;

import com.wantedbackendassignment.api.auth.jwt.JwtProvider;
import com.wantedbackendassignment.api.auth.jwt.JwtVo;
import com.wantedbackendassignment.api.properties.JwtProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.wantedbackendassignment.api.utils.HttpUtils.createSuccessResponse;
import static com.wantedbackendassignment.api.utils.HttpUtils.getCookieOfAccessToken;
import static com.wantedbackendassignment.api.utils.HttpUtils.setResponseWithCookie;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        JwtVo jwtInfo = JwtVo.builder()
                .email(principal.getUsername())
                .roles(authentication.getAuthorities())
                .build();

        String accessToken = jwtProvider.generate(jwtInfo);
        long validityPeriod = jwtProperties.getValidityPeriod();

        setResponseWithCookie(
                response,
                createSuccessResponse("login success", HttpStatus.OK.value()),
                getCookieOfAccessToken(accessToken, validityPeriod)
        );
    }
}
