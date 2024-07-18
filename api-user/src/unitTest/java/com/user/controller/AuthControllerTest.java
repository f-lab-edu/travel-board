package com.user.controller;

import com.user.ControllerTestSupport;
import com.user.controller.request.UserRegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;

import java.util.stream.Stream;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("회원가입이 성공하면 201 Created 응답이 반환되어야 한다")
    void signupSuccess() throws Exception {
        // given
        UserRegisterRequest request = getUserRegisterRequest();

        // when & then
        mockMvc.perform(post("/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidUserRegisterRequests")
    @DisplayName("유효하지 않은 요청으로 회원가입이 실패하면 400 Bad Request 응답이 반환되어야 한다")
    void signupValidationFailure(UserRegisterRequest request) throws Exception {
        // when & then
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Request validation failed"));
    }

    private UserRegisterRequest getUserRegisterRequest() {
        return new UserRegisterRequest(
                "email@gmail.com",
                "password",
                "nickname",
                "https://profileImageUrl.png",
                "bio"
        );
    }

    private static Stream<Arguments> provideInvalidUserRegisterRequests() {
        return Stream.of(
                // Empty email
                Arguments.of(new UserRegisterRequest("", "password", "nickname", "https://profileImageUrl.png", "bio")),
                // Invalid email format
                Arguments.of(new UserRegisterRequest("invalidemail", "password", "nickname", "https://profileImageUrl.png", "bio")),
                // Password too short
                Arguments.of(new UserRegisterRequest("valid@gmail.com", "short", "nickname", "https://profileImageUrl.png", "bio")),
                // Password too long
                Arguments.of(new UserRegisterRequest("valid@gmail.com", "longlonglonglonglonglong", "nickname", "https://profileImageUrl.png", "bio")),
                // Invalid Password format
                Arguments.of(new UserRegisterRequest("valid@gmail.com", "password)(**&&", "nickname", "https://profileImageUrl.png", "bio")),
                // Empty nickname
                Arguments.of(new UserRegisterRequest("valid@gmail.com", "password", "", "https://profileImageUrl.png", "bio")),
                // Invalid nickname format
                Arguments.of(new UserRegisterRequest("valid@gmail.com", "password", "nick name", "https://profileImageUrl.png", "bio")),
                // Invalid URL format
                Arguments.of(new UserRegisterRequest("valid@gmail.com", "password", "nickname", "invalid_url", "bio")),
                // Bio too long
                Arguments.of(new UserRegisterRequest("valid@gmail.com", "password!", "nickname", "https://profileImageUrl.png", "가".repeat(101)))
        );
    }
}
