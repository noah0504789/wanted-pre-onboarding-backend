package com.wantedbackendassignment.api.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements IUserService, UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException(String.format("User with email %s not found", email)));
    }

    @Override
    public User signUp(User newUser) {
        String encodedPassword = passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(encodedPassword);

        return userRepository.save(newUser);
    }

    @Override
    public boolean isEqualPassword(String rawPassword, String hashPassword) {
        return passwordEncoder.matches(rawPassword, hashPassword);
    }
}
