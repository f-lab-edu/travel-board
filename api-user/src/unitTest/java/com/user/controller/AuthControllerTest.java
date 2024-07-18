package com.user.controller;

import com.user.ControllerTestSupport;
import com.user.controller.request.UserRegisterRequest;
import com.user.utils.error.ErrorType;
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

    private static final UserRegisterRequest VALID_SIGNUP_REQUEST = new UserRegisterRequest("valid@gmail.com",
            "password", "nickname", "https://profileImageUrl.png", "bio");

    @Test
    @DisplayName("회원가입이 성공하면 201 Created 응답이 반환되어야 한다")
    void signupSuccess() throws Exception {
        // given
        String json = objectMapper.writeValueAsString(VALID_SIGNUP_REQUEST);

        // when & then
        mockMvc.perform(post("/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @ParameterizedTest(name = "{1} 유효성 검증 실패")
    @MethodSource("provideInvalidUserRegisterRequests")
    @DisplayName("유효하지 않은 요청으로 회원가입이 실패하면 400 Bad Request 응답이 반환되어야 한다")
    void signupValidationFailure(UserRegisterRequest request, String validationField) throws Exception {
        // when & then
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value(ErrorType.INVALID_REQUEST.getMessage()))
                .andExpect(jsonPath(String.format("$.validations.%s", validationField)).isNotEmpty());
    }

    private static Stream<Arguments> provideInvalidUserRegisterRequests() {
        return Stream.of(
                Arguments.of(new UserRegisterRequest("", VALID_SIGNUP_REQUEST.password(), VALID_SIGNUP_REQUEST.nickname(), VALID_SIGNUP_REQUEST.profileImageUrl(), VALID_SIGNUP_REQUEST.bio()), "email"),
                Arguments.of(new UserRegisterRequest("invalid_email", VALID_SIGNUP_REQUEST.password(), VALID_SIGNUP_REQUEST.nickname(), VALID_SIGNUP_REQUEST.profileImageUrl(), VALID_SIGNUP_REQUEST.bio()), "email"),
                Arguments.of(new UserRegisterRequest(VALID_SIGNUP_REQUEST.email(), "", VALID_SIGNUP_REQUEST.nickname(), VALID_SIGNUP_REQUEST.profileImageUrl(), VALID_SIGNUP_REQUEST.bio()), "password"),
                Arguments.of(new UserRegisterRequest(VALID_SIGNUP_REQUEST.email(), "short", VALID_SIGNUP_REQUEST.nickname(), VALID_SIGNUP_REQUEST.profileImageUrl(), VALID_SIGNUP_REQUEST.bio()), "password"),
                Arguments.of(new UserRegisterRequest(VALID_SIGNUP_REQUEST.email(), "longlonglonglonglonglong", VALID_SIGNUP_REQUEST.nickname(), VALID_SIGNUP_REQUEST.profileImageUrl(), VALID_SIGNUP_REQUEST.bio()), "password"),
                Arguments.of(new UserRegisterRequest(VALID_SIGNUP_REQUEST.email(), "password)(**&&", VALID_SIGNUP_REQUEST.nickname(), VALID_SIGNUP_REQUEST.profileImageUrl(), VALID_SIGNUP_REQUEST.bio()), "password"),
                Arguments.of(new UserRegisterRequest(VALID_SIGNUP_REQUEST.email(), VALID_SIGNUP_REQUEST.password(), "", VALID_SIGNUP_REQUEST.profileImageUrl(), VALID_SIGNUP_REQUEST.bio()), "nickname"),
                Arguments.of(new UserRegisterRequest(VALID_SIGNUP_REQUEST.email(), VALID_SIGNUP_REQUEST.password(), "nick name", VALID_SIGNUP_REQUEST.profileImageUrl(), VALID_SIGNUP_REQUEST.bio()), "nickname"),
                Arguments.of(new UserRegisterRequest(VALID_SIGNUP_REQUEST.email(), VALID_SIGNUP_REQUEST.password(), VALID_SIGNUP_REQUEST.nickname(), "invalid_url", VALID_SIGNUP_REQUEST.bio()), "profileImageUrl"),
                Arguments.of(new UserRegisterRequest(VALID_SIGNUP_REQUEST.email(), VALID_SIGNUP_REQUEST.password(), VALID_SIGNUP_REQUEST.nickname(), VALID_SIGNUP_REQUEST.profileImageUrl(), "가".repeat(101)), "bio")
        );
    }
}
