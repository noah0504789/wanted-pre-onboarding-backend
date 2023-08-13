package com.wantedbackendassignment.api.auth.login;

import com.wantedbackendassignment.api.user.User;
import com.wantedbackendassignment.api.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static com.wantedbackendassignment.api.UserUtils.createAuthentication;
import static com.wantedbackendassignment.api.UserUtils.createUser;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginProviderTest {

    @Mock
    UserService userService;

    @InjectMocks
    LoginProvider loginProvider;

    @Test
    @DisplayName("authenticate() : 로그인 인증 성공")
    void authenticate_success() {
        String dummyEmail = "test@wanted.com";
        String dummyPassword = "12345678";
        User dummyUser = createUser(dummyEmail, dummyPassword);

        when(userService.loadUserByUsername(dummyEmail)).thenReturn(dummyUser);
        when(userService.isEqualPassword(dummyPassword, dummyUser.getPassword())).thenReturn(true);

        Authentication auth = createAuthentication(dummyEmail, dummyPassword);
        Authentication result = loginProvider.authenticate(auth);

        assertNotNull(result);
        assertEquals(dummyEmail, result.getName());
    }

    @Test
    @DisplayName("authenticate() : 로그인 인증 실패 - 존재하지 않는 이메일")
    void authenticate_failure_non_exists_email() {
        String nonExistsEmail = "noAtMark";
        when(userService.loadUserByUsername(nonExistsEmail)).thenThrow(UsernameNotFoundException.class);

        String dummyPassword = "12345678";
        Authentication auth = createAuthentication(nonExistsEmail, dummyPassword);

        assertThrows(UsernameNotFoundException.class, () -> loginProvider.authenticate(auth));
    }

    @Test
    @DisplayName("authenticate() : 로그인 인증 실패 - 일치하지 않는 패스워드")
    void authenticate_failure_mismatched_password() {
        String dummyEmail = "test@wanted.com";
        String dummyPassword = "12345678";
        User dummyUser = createUser(dummyEmail, dummyPassword);
        when(userService.loadUserByUsername(dummyEmail)).thenReturn(dummyUser);

        String mismatchedPassword = "123456789";
        when(userService.isEqualPassword(mismatchedPassword, dummyUser.getPassword())).thenReturn(false);

        Authentication auth = createAuthentication(dummyEmail, mismatchedPassword);

        assertThrows(BadCredentialsException.class, () -> loginProvider.authenticate(auth));
    }
}
