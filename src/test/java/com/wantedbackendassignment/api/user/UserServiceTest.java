package com.wantedbackendassignment.api.user;

import com.wantedbackendassignment.api.dto.SignUpDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.wantedbackendassignment.api.user.UserUtils.createDummyUser;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserMapper userMapper;

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

        when(userMapper.toUser(any(SignUpDto.class))).thenReturn(dummyUser);

        String encodedDummyPassword = "encoded_12345678";
        when(passwordEncoder.encode(anyString())).thenReturn(encodedDummyPassword);

        dummyUser.setPassword(encodedDummyPassword);
        when(userRepository.save(any(User.class))).thenReturn(dummyUser);

        User savedUser = userService.signUp(new SignUpDto(dummyEmail, dummyPassword));

        verify(userMapper, times(1)).toUser(any(SignUpDto.class));
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(userRepository, times(1)).save(any(User.class));

        assertEquals(dummyUser.getEmail(), savedUser.getEmail());
        assertEquals(dummyUser.getPassword(), savedUser.getPassword());
    }
}
