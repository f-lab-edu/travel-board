package com.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.config.SecurityConfig;
import com.user.controller.request.UserRegisterRequest;
import com.user.service.AuthService;
import com.user.support.fixture.dto.request.UserRegisterRequestFixtureFactory;
import com.user.utils.error.CommonException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static com.user.utils.error.ErrorType.DEFAULT_ERROR;
import static com.user.utils.error.ErrorType.DUPLICATED_EMAIL;
import static com.user.utils.error.ErrorType.INVALID_REQUEST;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("회원가입이 성공하면 201 Created 응답이 반환되어야 한다")
    void signupSuccess() throws Exception {
        // given
        UserRegisterRequest request = UserRegisterRequestFixtureFactory.create();
        String json = objectMapper.writeValueAsString(request);
        // UserService.register method is called, but it just returns without actually doing anything.

        // when & then
        mockMvc.perform(post("/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("userService에서 예상할 수 없는 예외가 발생하면 500 Internal Server Error 응답이 반환되어야 한다")
    void signupFailureWhenUserServiceThrowException() throws Exception {
        // given
        UserRegisterRequest request = UserRegisterRequestFixtureFactory.create();
        String json = objectMapper.writeValueAsString(request);
        doThrow(new RuntimeException()).when(authService).register(request);

        // when & then
        mockMvc.perform(post("/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(DEFAULT_ERROR.getMessage()));
    }

    @Test
    @DisplayName("userService.register()에서 Duplicated Email 예외가 발생하면 409 Conflict 응답이 반환되어야 한다")
    void signupFailureWhenUserServiceThrowCommonException() throws Exception {
        // given
        UserRegisterRequest request = UserRegisterRequestFixtureFactory.create();
        String json = objectMapper.writeValueAsString(request);
        doThrow(new CommonException(DUPLICATED_EMAIL)).when(authService).register(request);

        // when & then
        mockMvc.perform(post("/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.message").value(DUPLICATED_EMAIL.getMessage()));
    }

    @TestFactory
    @DisplayName("회원가입 시 이메일 유효성 검증 실패하면 400 Bad Request 응답이 반환되어야 한다")
    Stream<DynamicTest> emailValidationFailure() {
        return UserRegisterRequestFixtureFactory.getInvalidEmailRequests().stream()
                .map(request -> dynamicTest(
                                String.format("이메일에 %s를 입력하면 유효성 검증이 실패한다", request.email()),
                                () -> mockMvc.perform(post("/auth/signup")
                                                .contentType(APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isBadRequest())
                                        .andExpect(jsonPath("$.code").value(400))
                                        .andExpect(jsonPath("$.message").value(INVALID_REQUEST.getMessage()))
                                        .andExpect(jsonPath("$.validations.email").isNotEmpty())
                        )
                );
    }

    @TestFactory
    @DisplayName("회원가입 시 비밀번호 유효성 검증 실패하면 400 Bad Request 응답이 반환되어야 한다")
    Stream<DynamicTest> passwordValidationFailure() {
        return UserRegisterRequestFixtureFactory.getInvalidPasswordRequests().stream()
                .map(request -> dynamicTest(
                                String.format("비밀번호에 %s를 입력하면 유효성 검증이 실패한다", request.password()),
                                () -> mockMvc.perform(post("/auth/signup")
                                                .contentType(APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isBadRequest())
                                        .andExpect(jsonPath("$.code").value(400))
                                        .andExpect(jsonPath("$.message").value(INVALID_REQUEST.getMessage()))
                                        .andExpect(jsonPath("$.validations.password").isNotEmpty())
                        )
                );
    }

    @TestFactory
    @DisplayName("회원가입 시 닉네임 유효성 검증 실패하면 400 Bad Request 응답이 반환되어야 한다")
    Stream<DynamicTest> nicknameValidationFailure() {
        return UserRegisterRequestFixtureFactory.getInvalidNicknameRequests().stream()
                .map(request -> dynamicTest(
                                String.format("닉네임에 %s를 입력하면 유효성 검증이 실패한다", request.nickname()),
                                () -> mockMvc.perform(post("/auth/signup")
                                                .contentType(APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isBadRequest())
                                        .andExpect(jsonPath("$.code").value(400))
                                        .andExpect(jsonPath("$.message").value(INVALID_REQUEST.getMessage()))
                                        .andExpect(jsonPath("$.validations.nickname").isNotEmpty())
                        )
                );
    }

    @TestFactory
    @DisplayName("회원가입 시 프로필 이미지 URL 유효성 검증 실패하면 400 Bad Request 응답이 반환되어야 한다")
    Stream<DynamicTest> profileImageUrlValidationFailure() {
        return UserRegisterRequestFixtureFactory.getInvalidProfileImageUrlRequests().stream()
                .map(request -> dynamicTest(
                                String.format("프로필 이미지 URL에 %s를 입력하면 유효성 검증이 실패한다", request.profileImageUrl()),
                                () -> mockMvc.perform(post("/auth/signup")
                                                .contentType(APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isBadRequest())
                                        .andExpect(jsonPath("$.code").value(400))
                                        .andExpect(jsonPath("$.message").value(INVALID_REQUEST.getMessage()))
                                        .andExpect(jsonPath("$.validations.profileImageUrl").isNotEmpty())
                        )
                );
    }

    @TestFactory
    @DisplayName("회원가입 시 소개글 유효성 검증 실패하면 400 Bad Request 응답이 반환되어야 한다")
    Stream<DynamicTest> bioValidationFailure() {
        return UserRegisterRequestFixtureFactory.getInvalidBioRequests().stream()
                .map(request -> dynamicTest(
                                String.format("소개에 %s를 입력하면 유효성 검증이 실패한다", request.bio()),
                                () -> mockMvc.perform(post("/auth/signup")
                                                .contentType(APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isBadRequest())
                                        .andExpect(jsonPath("$.code").value(400))
                                        .andExpect(jsonPath("$.message").value(INVALID_REQUEST.getMessage()))
                                        .andExpect(jsonPath("$.validations.bio").isNotEmpty())
                        )
                );
    }
}
