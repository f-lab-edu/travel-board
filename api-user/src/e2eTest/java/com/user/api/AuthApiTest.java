package com.user.api;

import com.storage.entity.Account;
import com.storage.repository.AccountRepository;
import com.storage.repository.UserRepository;
import com.user.E2eTestSupport;
import com.user.controller.request.UserRegisterRequest;
import com.user.utils.error.ErrorType;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;

public class AuthApiTest extends E2eTestSupport {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    private static final UserRegisterRequest VALID_SIGNUP_REQUEST = new UserRegisterRequest("valid@gmail.com",
            "password", "nickname", "https://profileImageUrl.png", "bio");

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        accountRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("회원가입이 성공하면 201 Created가 반환되어야 한다")
    void signupSuccess() {
        given()
                .contentType(ContentType.JSON)
                .body(VALID_SIGNUP_REQUEST)
        .when()
                .post("/auth/signup")
        .then()
                .statusCode(201);
    }

    @Test
    @DisplayName("회원가입시 이메일이 중복되면 409 Conflict가 반환되면서 실패한다")
    void signupWithDuplicatedEmail() {
        // given
        Account account = Account.builder().email(VALID_SIGNUP_REQUEST.email()).build();
        accountRepository.save(account);

        given()
                .contentType(ContentType.JSON)
                .body(VALID_SIGNUP_REQUEST)
        .when()
                .post("/auth/signup")
        .then()
                .statusCode(409)
                .body("code", equalTo(409))
                .body("message", equalTo(ErrorType.DUPLICATED_EMAIL.getMessage()))
                .body("validations", equalTo(Map.of()));
    }

    @ParameterizedTest(name = "{1} 유효성 검증 실패")
    @MethodSource("provideInvalidSignupRequestBody")
    @DisplayName("회원가입시 유효하지 않은 요청이면 400 Bad Request가 반환되어야 한다")
    void signupWithInvalidRequest(UserRegisterRequest request, String validationField) {
        given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post("/auth/signup")
        .then()
                .statusCode(400)
                .body("code", equalTo(400))
                .body("message", equalTo(ErrorType.INVALID_REQUEST.getMessage()))
                .body("validations", hasKey(validationField));
    }

    private static Stream<Arguments> provideInvalidSignupRequestBody() {
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
