package com.user.api;

import com.storage.entity.Account;
import com.storage.repository.AccountRepository;
import com.storage.repository.UserRepository;
import com.user.E2eTestSupport;
import com.user.controller.request.UserRegisterRequest;
import com.user.support.fixture.dto.request.UserRegisterRequestFixtureFactory;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.stream.Stream;

import static com.user.utils.error.ErrorType.DUPLICATED_EMAIL;
import static com.user.utils.error.ErrorType.INVALID_REQUEST;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class AuthApiTest extends E2eTestSupport {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        accountRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("회원가입이 성공한다")
    void signupSuccess() {
        given()
                .contentType(ContentType.JSON)
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
                .contentType(ContentType.JSON)
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
                                    .contentType(ContentType.JSON)
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
    @DisplayName("회원가입 시 이메일 유효성 검증에 실패한다")
    Stream<DynamicTest> passwordValidationFailure() {
        return UserRegisterRequestFixtureFactory.getInvalidPasswordRequests().stream()
                .map(request -> dynamicTest(
                        String.format("비밀번호에 %s를 입력하면 유효성 검증이 실패한다", request.password()),
                        () -> given()
                                    .contentType(ContentType.JSON)
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
                                    .contentType(ContentType.JSON)
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
                                    .contentType(ContentType.JSON)
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
                                    .contentType(ContentType.JSON)
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
}
