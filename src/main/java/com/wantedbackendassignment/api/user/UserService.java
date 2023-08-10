package com.wantedbackendassignment.api.user;

import com.wantedbackendassignment.api.dto.SignUpDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public User signUp(SignUpDto signUpDto) {
        User newUser = userMapper.toUser(signUpDto);

        String encodedPassword = passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(encodedPassword);

        return userRepository.save(newUser);
    }
}
