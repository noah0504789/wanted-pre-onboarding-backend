package com.wantedbackendassignment.api.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.wantedbackendassignment.api.UserUtils.createDummyUser;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @Test
    @DisplayName("signUp() : 신규 회원 DB 저장 성공")
    void signUp_success() {
        String dummyEmail = "test@wanted.com";
        String dummyPassword = "12345678";
        User dummyUser = createDummyUser(dummyEmail, dummyPassword);

        String encodedDummyPassword = "encoded_12345678";
        when(passwordEncoder.encode(anyString())).thenReturn(encodedDummyPassword);

        dummyUser.setPassword(encodedDummyPassword);
        when(userRepository.save(any(User.class))).thenReturn(dummyUser);

        User savedUser = userService.signUp(createDummyUser(dummyEmail, dummyPassword));

        verify(passwordEncoder, times(1)).encode(anyString());
        verify(userRepository, times(1)).save(any(User.class));

        assertEquals(dummyUser.getEmail(), savedUser.getEmail());
        assertEquals(dummyUser.getPassword(), savedUser.getPassword());
    }

    @Test
    @DisplayName("loadUserByUsername() : 기존 회원 DB 조회 성공")
    void loadUserByUsername_success() {
        String dummyEmail = "test@wanted.com";
        String dummyPassword = "12345678";
        User dummyUser = createDummyUser(dummyEmail, dummyPassword);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(dummyUser));

        UserDetails savedUser = userService.loadUserByUsername(dummyEmail);

        verify(userRepository, times(1)).findByEmail(anyString());

        assertEquals(dummyUser.getEmail(), savedUser.getUsername());
    }

    @Test
    @DisplayName("loadUserByUsername() : 존재하지 않는 회원 DB 조회 실패")
    void loadUserByUsername_failure_non_exists_email() {
        String nonExistsEmail = "noAtMark";

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
           userService.loadUserByUsername(nonExistsEmail);
        });

        verify(userRepository, times(1)).findByEmail(anyString());
    }

    @Test
    @DisplayName("isEqualPassword() : 저장된 암호 패스워드의 원시 패스워드 여부 확인 성공")
    void isEqualPassword_success() {
        String dummyPassword = "12345678";
        String savedPassword = "encoded password";

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        assertTrue(userService.isEqualPassword(dummyPassword, savedPassword));
    }

    @Test
    @DisplayName("isEqualPassword() : 저장된 암호 패스워드의 원시 패스워드 여부 확인 실패")
    void isEqualPassword_failure_mismatched_password() {
        String mismatchedPassword = "wrong_password";
        String savedPassword = "encoded password";

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertFalse(userService.isEqualPassword(mismatchedPassword, savedPassword));
    }
}
