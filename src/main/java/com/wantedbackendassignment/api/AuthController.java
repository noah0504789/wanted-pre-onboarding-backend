package com.wantedbackendassignment.api;

import com.wantedbackendassignment.api.dto.SignUpDto;
import com.wantedbackendassignment.api.user.IUserService;
import com.wantedbackendassignment.api.utils.HttpUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IUserService userService;
    private final HttpUtils httpUtils;

    @PostMapping("/sign-up")
    public ResponseEntity signUp(final @Valid @RequestBody SignUpDto signUpDto) {
        HttpStatus created = HttpStatus.CREATED;

        userService.signUp(signUpDto);

        return new ResponseEntity<>(
                httpUtils.createSuccessResponse("sign-up success", created.value()),
                created
        );
    }
}
