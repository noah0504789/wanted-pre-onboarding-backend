package com.wantedbackendassignment.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wantedbackendassignment.api.dto.SignUpDto;
import com.wantedbackendassignment.api.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static com.wantedbackendassignment.api.user.UserUtils.createSignUpDto;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

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
}
