package com.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.config.security.SecurityConfig;
import com.user.dto.request.AccessTokenReissueRequest;
import com.user.dto.request.LoginRequest;
import com.user.dto.request.UserRegisterRequest;
import com.user.dto.response.TokenResponse;
import com.user.enums.ErrorType;
import com.user.service.AuthService;
import com.user.support.fixture.dto.request.AccessTokenReissueRequestFixtureFactory;
import com.user.support.fixture.dto.request.LoginRequestFixtureFactory;
import com.user.support.fixture.dto.request.UserRegisterRequestFixtureFactory;
import com.user.utils.error.CommonException;
import com.user.utils.token.JwtTokenProvider;
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

import static com.user.enums.ErrorType.DEFAULT_ERROR;
import static com.user.enums.ErrorType.DUPLICATED_EMAIL;
import static com.user.enums.ErrorType.INVALID_REQUEST;
import static com.user.enums.ErrorType.UNAUTHORIZED_TOKEN;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

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

    @Test
    @DisplayName("로그인 성공 시 200 OK 응답이 반환되어야 한다")
    void loginSuccess() throws Exception {
        // given
        LoginRequest request = LoginRequestFixtureFactory.create();
        String json = objectMapper.writeValueAsString(request);
        TokenResponse expectedResponse = new TokenResponse("accessToken", "refreshToken");
        given(authService.login(request)).willReturn(expectedResponse);

        // when && then
        mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"));
    }

    @Test
    @DisplayName("로그인 실패 시 401 Unauthorized 응답이 반환되어야 한다")
    void loginFailure() throws Exception {
        // given
        LoginRequest request = LoginRequestFixtureFactory.create();
        String json = objectMapper.writeValueAsString(request);
        doThrow(new CommonException(ErrorType.LOGIN_FAIL)).when(authService).login(request);

        // when && then
        mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value(ErrorType.LOGIN_FAIL.getMessage()));
    }

    @TestFactory
    @DisplayName("로그인 시 이메일 유효성 검증 실패하면 400 Bad Request 응답이 반환되어야 한다")
    Stream<DynamicTest> loginEmailValidationFailure() {
        return LoginRequestFixtureFactory.getInvalidEmailRequests().stream()
                .map(request -> dynamicTest(
                                String.format("이메일에 %s를 입력하면 유효성 검증이 실패한다", request.email()),
                                () -> mockMvc.perform(post("/auth/login")
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
    @DisplayName("로그인 시 패스워드 유효성 검증 실패하면 400 Bad Request 응답이 반환되어야 한다")
    Stream<DynamicTest> loginPasswordValidationFailure() {
        return LoginRequestFixtureFactory.getInvalidPasswordRequests().stream()
                .map(request -> dynamicTest(
                                String.format("패스워드에 %s를 입력하면 유효성 검증이 실패한다", request.password()),
                                () -> mockMvc.perform(post("/auth/login")
                                                .contentType(APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isBadRequest())
                                        .andExpect(jsonPath("$.code").value(400))
                                        .andExpect(jsonPath("$.message").value(INVALID_REQUEST.getMessage()))
                                        .andExpect(jsonPath("$.validations.password").isNotEmpty())
                        )
                );
    }

    @Test
    @DisplayName("로그인 시 예상할 수 없는 예외가 발생하면 500 Internal Server Error 응답이 반환되어야 한다")
    void loginFailureWhenUserServiceThrowException() throws Exception {
        // given
        LoginRequest request = LoginRequestFixtureFactory.create();
        String json = objectMapper.writeValueAsString(request);
        doThrow(new RuntimeException()).when(authService).login(request);

        // when & then
        mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(DEFAULT_ERROR.getMessage()));
    }

    @Test
    @DisplayName("엑세스 토큰 재발급 성공 시 200 OK 응답이 반환되어야 한다")
    void reissueAccessTokenSuccess() throws Exception {
        // given
        AccessTokenReissueRequest request = AccessTokenReissueRequestFixtureFactory.createMockRefreshToken();
        String json = objectMapper.writeValueAsString(request);
        String expectedAccessToken = "newAccessToken";
        given(authService.reissueAccessToken(request.refreshToken())).willReturn(expectedAccessToken);

        // when && then
        mockMvc.perform(patch("/auth/access-token")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(expectedAccessToken));
    }

    @Test
    @DisplayName("엑세스 토큰 재발급 실패 시 401 Unauthorized 응답이 반환되어야 한다")
    void reissueAccessTokenFailure() throws Exception {
        // given
        AccessTokenReissueRequest request = AccessTokenReissueRequestFixtureFactory.createMockRefreshToken();
        String json = objectMapper.writeValueAsString(request);
        doThrow(new CommonException(UNAUTHORIZED_TOKEN)).when(authService).reissueAccessToken(request.refreshToken());

        // when && then
        mockMvc.perform(patch("/auth/access-token")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value(UNAUTHORIZED_TOKEN.getMessage()));
    }

    @TestFactory
    @DisplayName("엑세스 토큰 재발급 시 리프레시 토큰 유효성 검증 실패하면 400 Bad Request 응답이 반환되어야 한다")
    Stream<DynamicTest> reissueAccessTokenValidationFailure() {
        return AccessTokenReissueRequestFixtureFactory.getInvalidRefreshTokenRequests().stream()
                .map(request -> dynamicTest(
                                String.format("리프레시 토큰에 %s를 입력하면 유효성 검증이 실패한다", request.refreshToken()),
                                () -> mockMvc.perform(patch("/auth/access-token")
                                                .contentType(APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isBadRequest())
                                        .andExpect(jsonPath("$.code").value(400))
                                        .andExpect(jsonPath("$.message").value(INVALID_REQUEST.getMessage()))
                                        .andExpect(jsonPath("$.validations.refreshToken").isNotEmpty())
                        )
                );
    }

    @Test
    @DisplayName("엑세스 토큰 재발급 시 예상할 수 없는 예외가 발생하면 500 Internal Server Error 응답이 반환되어야 한다")
    void reissueAccessTokenFailureWhenUserServiceThrowException() throws Exception {
        // given
        AccessTokenReissueRequest request = AccessTokenReissueRequestFixtureFactory.createMockRefreshToken();
        String json = objectMapper.writeValueAsString(request);
        doThrow(new RuntimeException()).when(authService).reissueAccessToken(request.refreshToken());

        // when & then
        mockMvc.perform(patch("/auth/access-token")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(DEFAULT_ERROR.getMessage()));
    }
}
