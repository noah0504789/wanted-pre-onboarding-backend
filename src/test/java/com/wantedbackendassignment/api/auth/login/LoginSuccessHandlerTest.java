package com.wantedbackendassignment.api.auth.login;

import com.wantedbackendassignment.api.auth.jwt.JwtProvider;
import com.wantedbackendassignment.api.auth.jwt.JwtVo;
import com.wantedbackendassignment.api.dto.ResponseDto;
import com.wantedbackendassignment.api.properties.JwtProperties;
import com.wantedbackendassignment.api.user.User;
import com.wantedbackendassignment.api.utils.HttpUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

import java.io.IOException;

import static com.wantedbackendassignment.api.UserUtils.createAuthentication;
import static com.wantedbackendassignment.api.UserUtils.createUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginSuccessHandlerTest {

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private HttpUtils httpUtils;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private LoginSuccessHandler loginSuccessHandler;

    @Test
    @DisplayName("onAuthenticationSuccess() : 인증 성공 및 응답 제어 수행 테스트")
    void onAuthenticationSuccess() throws IOException {
        String dummyEmail = "test@wanted.com";
        String dummyPassword = "12345678";
        User newUser = createUser(dummyEmail, dummyPassword);
        Authentication authentication = createAuthentication(newUser, dummyPassword);

        String accessToken = "access_token_value";
        when(jwtProvider.generate(any(JwtVo.class))).thenReturn(accessToken);

        long validityPeriod = 2L;
        when(jwtProperties.getValidityPeriod()).thenReturn(validityPeriod);

        ResponseDto<String> expectedResponseDto = ResponseDto.success("login success", HttpStatus.OK.value());
        when(httpUtils.createSuccessResponse(anyString(), anyInt())).thenReturn(expectedResponseDto);

        Cookie expectedCookie = new Cookie("access_token", accessToken);
        when(httpUtils.getCookieOfAccessToken(accessToken, validityPeriod)).thenReturn(expectedCookie);

        loginSuccessHandler.onAuthenticationSuccess(null, response, authentication);

        verify(jwtProvider, times(1)).generate(any(JwtVo.class));
        verify(jwtProperties, times(1)).getValidityPeriod();
        verify(httpUtils, times(1)).setResponseWithCookie(
                any(HttpServletResponse.class),
                eq(expectedResponseDto),
                eq(expectedCookie)
        );
    }
}
