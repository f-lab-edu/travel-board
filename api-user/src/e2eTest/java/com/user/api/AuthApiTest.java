package com.user.api;

import com.storage.entity.Account;
import com.storage.entity.User;
import com.storage.repository.AccountRepository;
import com.storage.repository.UserRepository;
import com.user.E2eTestSupport;
import com.user.dto.request.AccessTokenReissueRequest;
import com.user.dto.request.LoginRequest;
import com.user.dto.request.UserRegisterRequest;
import com.user.support.fixture.dto.request.AccessTokenReissueRequestFixtureFactory;
import com.user.support.fixture.dto.request.LoginRequestFixtureFactory;
import com.user.support.fixture.dto.request.UserRegisterRequestFixtureFactory;
import com.user.support.fixture.entity.AccountFixtureFactory;
import com.user.support.fixture.entity.UserFixtureFactory;
import com.user.utils.token.JwtTokenProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Map;
import java.util.stream.Stream;

import static com.user.enums.ErrorType.DUPLICATED_EMAIL;
import static com.user.enums.ErrorType.INVALID_REQUEST;
import static com.user.enums.ErrorType.LOGIN_FAIL;
import static com.user.enums.ErrorType.UNAUTHORIZED_TOKEN;
import static com.user.enums.ErrorType.USER_NOT_FOUND;
import static com.user.enums.TokenType.REFRESH;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class AuthApiTest extends E2eTestSupport {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        accountRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("회원가입이 성공한다")
    void signupSuccess() {
        given()
                .contentType(JSON)
                .body(UserRegisterRequestFixtureFactory.create())
        .when()
                .post("/auth/signup")
        .then()
                .statusCode(201);
    }

    @Test
    @DisplayName("회원가입시 이메일이 중복되면 실패한다")
    void signupWithDuplicatedEmail() {
        // given
        UserRegisterRequest request = UserRegisterRequestFixtureFactory.create();
        Account account = Account.builder().email(request.email()).build();
        accountRepository.save(account);

        given()
                .contentType(JSON)
                .body(request)
        .when()
                .post("/auth/signup")
        .then()
                .statusCode(409)
                .body("code", equalTo(409))
                .body("message", equalTo(DUPLICATED_EMAIL.getMessage()))
                .body("validations", equalTo(Map.of()));
    }

    @TestFactory
    @DisplayName("회원가입 시 이메일 유효성 검증에 실패한다")
    Stream<DynamicTest> emailValidationFailure() {
        return UserRegisterRequestFixtureFactory.getInvalidEmailRequests().stream()
                .map(request -> dynamicTest(
                        String.format("이메일에 %s를 입력하면 유효성 검증이 실패한다", request.email()),
                        () -> given()
                                    .contentType(JSON)
                                    .body(request)
                                .when()
                                    .post("/auth/signup")
                                .then()
                                    .statusCode(400)
                                    .body("code", equalTo(400))
                                    .body("message", equalTo(INVALID_REQUEST.getMessage()))
                                    .body("validations", hasKey("email"))
                ));
    }

    @TestFactory
    @DisplayName("회원가입 시 패스워드 유효성 검증에 실패한다")
    Stream<DynamicTest> passwordValidationFailure() {
        return UserRegisterRequestFixtureFactory.getInvalidPasswordRequests().stream()
                .map(request -> dynamicTest(
                        String.format("비밀번호에 %s를 입력하면 유효성 검증이 실패한다", request.password()),
                        () -> given()
                                    .contentType(JSON)
                                    .body(request)
                                .when()
                                    .post("/auth/signup")
                                .then()
                                    .statusCode(400)
                                    .body("code", equalTo(400))
                                    .body("message", equalTo(INVALID_REQUEST.getMessage()))
                                    .body("validations", hasKey("password"))
                ));
    }

    @TestFactory
    @DisplayName("회원가입 시 닉네임 유효성 검증에 실패한다")
    Stream<DynamicTest> nicknameValidationFailure() {
        return UserRegisterRequestFixtureFactory.getInvalidNicknameRequests().stream()
                .map(request -> dynamicTest(
                        String.format("닉네임에 %s를 입력하면 유효성 검증이 실패한다", request.nickname()),
                        () -> given()
                                    .contentType(JSON)
                                    .body(request)
                                .when()
                                    .post("/auth/signup")
                                .then()
                                    .statusCode(400)
                                    .body("code", equalTo(400))
                                    .body("message", equalTo(INVALID_REQUEST.getMessage()))
                                    .body("validations", hasKey("nickname"))
                ));
    }

    @TestFactory
    @DisplayName("회원가입 시 프로필 이미지 URL 유효성 검증에 실패한다")
    Stream<DynamicTest> profileImageUrlValidationFailure() {
        return UserRegisterRequestFixtureFactory.getInvalidProfileImageUrlRequests().stream()
                .map(request -> dynamicTest(
                        String.format("프로필 이미지 URL에 %s를 입력하면 유효성 검증이 실패한다", request.profileImageUrl()),
                        () -> given()
                                    .contentType(JSON)
                                    .body(request)
                                .when()
                                    .post("/auth/signup")
                                .then()
                                    .statusCode(400)
                                    .body("code", equalTo(400))
                                    .body("message", equalTo(INVALID_REQUEST.getMessage()))
                                    .body("validations", hasKey("profileImageUrl"))
                ));
    }

    @TestFactory
    @DisplayName("회원가입 시 소개글 유효성 검증에 실패한다")
    Stream<DynamicTest> bioValidationFailure() {
        return UserRegisterRequestFixtureFactory.getInvalidBioRequests().stream()
                .map(request -> dynamicTest(
                        String.format("소개에 %s를 입력하면 유효성 검증이 실패한다", request.bio()),
                        () -> given()
                                    .contentType(JSON)
                                    .body(request)
                                .when()
                                    .post("/auth/signup")
                                .then()
                                    .statusCode(400)
                                    .body("code", equalTo(400))
                                    .body("message", equalTo(INVALID_REQUEST.getMessage()))
                                    .body("validations", hasKey("bio"))
                ));
    }

    @Test
    @DisplayName("로그인이 성공한다")
    void loginSuccess() {
        // given
        Account account = AccountFixtureFactory.create(passwordEncoder, "password");
        User user = UserFixtureFactory.create(account);
        accountRepository.save(account);
        userRepository.save(user);

        LoginRequest request = new LoginRequest(account.getEmail(), "password");

        // when
        given()
                .contentType(JSON)
                .body(request)
        .when()
                .post("/auth/login")
        .then()
                .statusCode(200)
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @TestFactory
    @DisplayName("로그인 시 이메일 유효성 검증에 실패한다")
    Stream<DynamicTest> loginEmailValidationFailure() {
        return LoginRequestFixtureFactory.getInvalidEmailRequests().stream()
                .map(request -> dynamicTest(
                        String.format("이메일에 %s를 입력하면 유효성 검증이 실패한다", request.email()),
                        () -> given()
                                    .contentType(JSON)
                                    .body(request)
                                .when()
                                    .post("/auth/login")
                                .then()
                                    .statusCode(400)
                                    .body("code", equalTo(400))
                                    .body("message", equalTo(INVALID_REQUEST.getMessage()))
                                    .body("validations", hasKey("email"))
                ));
    }

    @TestFactory
    @DisplayName("로그인 시 비밀번호 유효성 검증에 실패한다")
    Stream<DynamicTest> loginPasswordValidationFailure() {
        return LoginRequestFixtureFactory.getInvalidPasswordRequests().stream()
                .map(request -> dynamicTest(
                        String.format("비밀번호에 %s를 입력하면 유효성 검증이 실패한다", request.password()),
                        () -> given()
                                    .contentType(JSON)
                                    .body(request)
                                .when()
                                    .post("/auth/login")
                                .then()
                                    .statusCode(400)
                                    .body("code", equalTo(400))
                                    .body("message", equalTo(INVALID_REQUEST.getMessage()))
                                    .body("validations", hasKey("password"))
                ));
    }

    @Test
    @DisplayName("로그인 시 가입하지 않은 이메일로 로그인하면 실패한다")
    void loginWithNotRegisteredEmail() {
        // given
        LoginRequest request = LoginRequestFixtureFactory.create();

        // when
        given()
                .contentType(JSON)
                .body(request)
        .when()
                .post("/auth/login")
        .then()
                .statusCode(401)
                .body("code", equalTo(401))
                .body("message", equalTo(LOGIN_FAIL.getMessage()));
    }

    @Test
    @DisplayName("로그인 시 패스워드가 일치하지 않으면 실패한다")
    void loginWithNotMatchedPassword() {
        // given
        Account account = AccountFixtureFactory.create(passwordEncoder, "password");
        User user = UserFixtureFactory.create(account);
        accountRepository.save(account);
        userRepository.save(user);

        LoginRequest request = new LoginRequest(account.getEmail(), "invalidPassword");

        // when
        given()
                .contentType(JSON)
                .body(request)
        .when()
                .post("/auth/login")
        .then()
                .statusCode(401)
                .body("code", equalTo(401))
                .body("message", equalTo(LOGIN_FAIL.getMessage()));
    }

    @Test
    @DisplayName("액세스 토큰 재발급이 성공한다")
    void reissueAccessTokenSuccess() {
        // given
        Account account = AccountFixtureFactory.create(passwordEncoder, "password");
        User user = UserFixtureFactory.create(account);
        accountRepository.save(account);
        userRepository.save(user);

        String refreshToken = jwtTokenProvider.generateToken(REFRESH, user.getId(), new Date());
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
        AccessTokenReissueRequest request = new AccessTokenReissueRequest(refreshToken);

        // when
        given()
                .contentType(JSON)
                .body(request)
        .when()
                .patch("/auth/access-token")
        .then()
                .statusCode(200)
                .body("accessToken", notNullValue());
    }

    @TestFactory
    @DisplayName("액세스 토큰 재발급 시 리프레시 토큰 유효성 검증에 실패한다")
    Stream<DynamicTest> reissueAccessTokenValidationFailure() {
        return AccessTokenReissueRequestFixtureFactory.getInvalidRefreshTokenRequests().stream()
                .map(request -> dynamicTest(
                        String.format("리프레시 토큰에 %s를 입력하면 유효성 검증이 실패한다", request.refreshToken()),
                        () -> given()
                                    .contentType(JSON)
                                    .body(request)
                                .when()
                                    .patch("/auth/access-token")
                                .then()
                                    .statusCode(400)
                                    .body("code", equalTo(400))
                                    .body("message", equalTo(INVALID_REQUEST.getMessage()))
                                    .body("validations", hasKey("refreshToken"))
                ));
    }

    @Test
    @DisplayName("엑세스 토큰 재발급 시 리프래시 토큰이 유효하지 않으면 실패한다.")
    void reissueAccessTokenWithInvalidRefreshToken() {
        // given
        AccessTokenReissueRequest request = AccessTokenReissueRequestFixtureFactory.createMockRefreshToken();

        // when
        given()
                .contentType(JSON)
                .body(request)
        .when()
                .patch("/auth/access-token")
        .then()
                .statusCode(401)
                .body("code", equalTo(401))
                .body("message", equalTo(UNAUTHORIZED_TOKEN.getMessage()));
    }

    @Test
    @DisplayName("엑세스 토큰 재발급 시 리프레시 토큰이 유효하더라도 사용자가 가지지 않으면 실패한다. 예상 상황 이중 로그인 방지")
    void reissueAccessTokenWithValidRefreshTokenButUserNotFound() {
        // given
        Account account = AccountFixtureFactory.create(passwordEncoder, "password");
        User user = UserFixtureFactory.create(account);
        accountRepository.save(account);
        userRepository.save(user);

        String refreshToken = jwtTokenProvider.generateToken(REFRESH, user.getId(), new Date());
        AccessTokenReissueRequest request = new AccessTokenReissueRequest(refreshToken);

        // when
        given()
                .contentType(JSON)
                .body(request)
        .when()
                .patch("/auth/access-token")
        .then()
                .statusCode(404)
                .body("code", equalTo(404))
                .body("message", equalTo(USER_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("엑세스 토큰 재발급 시 만료된 리프레시 토큰으로 시도하면 실패한다")
    void reissueAccessTokenWithExpiredRefreshToken() {
        // given
        // The expiration time of the refresh token registered in the property is 7 days
        // Therefore, try using the refresh token created 8 days ago
        Date date8DaysAgo = new Date(System.currentTimeMillis() - 8 * 24 * 60 * 60 * 1000);
        String refreshToken = jwtTokenProvider.generateToken(REFRESH, 1L, date8DaysAgo);
        AccessTokenReissueRequest request = new AccessTokenReissueRequest(refreshToken);

        // when
        given()
                .contentType(JSON)
                .body(request)
        .when()
                .patch("/auth/access-token")
        .then()
                .statusCode(401)
                .body("code", equalTo(401))
                .body("message", equalTo(UNAUTHORIZED_TOKEN.getMessage()));
    }
}
