package com.wantedbackendassignment.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wantedbackendassignment.api.auth.jwt.JwtAuthenticationFilter;
import com.wantedbackendassignment.api.auth.jwt.JwtProvider;
import com.wantedbackendassignment.api.auth.login.LoginFailureHandler;
import com.wantedbackendassignment.api.auth.login.LoginFilter;
import com.wantedbackendassignment.api.auth.login.LoginProvider;
import com.wantedbackendassignment.api.auth.login.LoginSuccessHandler;
import com.wantedbackendassignment.api.config.SecurityConfig;
import com.wantedbackendassignment.api.dto.SignUpDto;
import com.wantedbackendassignment.api.user.User;
import com.wantedbackendassignment.api.user.UserMapper;
import com.wantedbackendassignment.api.user.UserService;
import com.wantedbackendassignment.api.utils.HttpUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.stream.Stream;

import static com.wantedbackendassignment.api.UserUtils.createDummyUser;
import static com.wantedbackendassignment.api.UserUtils.createSignUpDto;
import static org.hamcrest.Matchers.either;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    value = AuthController.class,
    includeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {HttpUtils.class, SecurityConfig.class, UserMapper.class})
    }
)
class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext ctx;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private LoginProvider loginProvider;

    @MockBean
    private LoginSuccessHandler loginSuccessHandler;

    @MockBean
    private LoginFailureHandler loginFailureHandler;

    @MockBean
    private JwtProvider jwtProvider;

    @BeforeEach
    void init() {
        mvc = MockMvcBuilders.webAppContextSetup(ctx)
                .apply(springSecurity())
                .alwaysDo(print())
                .build();
    }

    String signUpUrl = "/api/auth/sign-up";

    @Test
    @DisplayName("signUp() : 유효한 값으로 회원가입 요청")
    void signUp_success() throws Exception {
        String dummyEmail = "test@wanted.com";
        String dummyPassword = "12345678";
        SignUpDto signUpDto = createSignUpDto(dummyEmail, dummyPassword);

        mvc.perform(post(signUpUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(signUpDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("responseCode", is(201)))
                .andExpect(jsonPath("data", is("sign-up success")))
                .andReturn();
    }

    @ParameterizedTest(name = "#{index} - {displayName} = Test with Argument={0}, {1}, {2}")
    @MethodSource("invalidInputForSignUp")
    @DisplayName("signUp() : 유효하지 않은 값으로 회원가입 요청")
    void signUp_failure_input_invalid(String invalidEmail, String invalidPassword) throws Exception {
        SignUpDto signUpDto = createSignUpDto(invalidEmail, invalidPassword);

        when(userService.signUp(any(User.class)))
                .thenReturn(createDummyUser(invalidEmail, "encoded" + invalidPassword));

        mvc.perform(post(signUpUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(signUpDto)))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andExpect(jsonPath("responseCode", is(400)))
                .andExpect(jsonPath("$.error.errors[0].objectName", is("signUpDto")))
                .andExpect(jsonPath("$.error.errors[0].field", either(is("email")).or(is("password"))))
                .andReturn();
    }

    static Stream<Arguments> invalidInputForSignUp() {
        return Stream.of(
                arguments("noAtMark", "12345678"),
                arguments("test@wanted.com", "1234567"),
                arguments("noAtMark", "1234567")
        );
    }
}
