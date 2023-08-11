package com.wantedbackendassignment.api.auth.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wantedbackendassignment.api.dto.LoginDto;
import com.wantedbackendassignment.api.dto.SignUpDto;
import com.wantedbackendassignment.api.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static com.wantedbackendassignment.api.UserUtils.createLoginDto;
import static com.wantedbackendassignment.api.UserUtils.createSignUpDto;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
@AutoConfigureMockMvc
class LoginFilterTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    private WebApplicationContext ctx;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    String existsEmail;
    String existsPassword;

    @BeforeEach
    public void init() {
        mvc = MockMvcBuilders.webAppContextSetup(ctx)
                .apply(springSecurity())
                .alwaysDo(print())
                .build();

        existsEmail = "test@wanted.com";
        existsPassword = "12345678";

        SignUpDto signUpDto = createSignUpDto(existsEmail, existsPassword);
        userService.signUp(signUpDto);
    }

    @Test
    @DisplayName("login() 성공 : 회원 로그인 요청")
    void attemptAuthentication_success() throws Exception {
        LoginDto loginDto = createLoginDto(existsEmail, existsPassword);

        mvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(loginDto)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("access_token"))
                .andExpect(cookie().httpOnly("access_token", true))
                .andExpect(cookie().secure("access_token", true))
                .andExpect(jsonPath("responseCode", is(200)))
                .andExpect(jsonPath("$.data", is("login success")))
                .andReturn();
    }

    @Test
    @DisplayName("login() 실패 : 비회원 로그인 요청")
    void attemptAuthentication_failure() throws Exception {
        String nonExistsEmail = "non@wanted.com";
        String nonExistsPassword = "non12345678";

        LoginDto loginDto = createLoginDto(nonExistsEmail, nonExistsPassword);

        mvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(loginDto)))
                .andExpect(status().is4xxClientError())
                .andExpect(cookie().doesNotExist("access_token"))
                .andExpect(jsonPath("responseCode", is(401)))
                .andExpect(jsonPath("$.error", is("Invalid email or password")))
                .andReturn();
    }
}
