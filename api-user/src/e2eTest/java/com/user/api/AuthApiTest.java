package com.user.api;

import com.storage.entity.Account;
import com.storage.repository.AccountRepository;
import com.storage.repository.UserRepository;
import com.user.E2eTestSupport;
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

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        accountRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("회원가입이 성공하면 201 Created가 반환되어야 한다")
    void signupSuccess() {
        String requestBody = signupRequestBody();

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post("/auth/signup")
        .then()
                .statusCode(201);
    }

    @Test
    @DisplayName("회원가입시 이메일이 중복되면 409 Conflict가 반환되면서 실패한다")
    void signupWithDuplicatedEmail() {
        // given
        Account account = Account.builder().email("email@gmail.com").password("password").build();
        accountRepository.save(account);

        String requestBody = signupRequestBody();

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post("/auth/signup")
        .then()
                .statusCode(409)
                .body("code", equalTo(409))
                .body("message", equalTo("Email is already in use"))
                .body("validations", equalTo(Map.of()));
    }

    private String signupRequestBody() {
        return """
                {
                    "email": "email@gmail.com",
                    "password": "password",
                    "nickname": "nickname",
                    "profileImageUrl": "https://profileImageUrl.png",
                    "bio": "bio"
                }
                """;
    }

    @ParameterizedTest
    @MethodSource("provideInvalidSignupRequestBody")
    @DisplayName("회원가입시 유효성 검증에 걸리면 400 Bad Request 반환되면서 실패한다")
    void signupWithInvalidRequest(String requestBody, String validationField) {
        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post("/auth/signup")
        .then()
                .statusCode(400)
                .body("code", equalTo(400))
                .body("message", equalTo("Request validation failed"))
                .body("validations", hasKey(validationField));
    }

    private static Stream<Arguments> provideInvalidSignupRequestBody() {
        return Stream.of(
                // Empty email
                Arguments.of("{\"email\": \"\", \"password\": \"password\", \"nickname\": \"nickname\", \"profileImageUrl\": \"https://profileImageUrl.png\", \"bio\": \"bio\"}", "email"),
                // Invalid email format
                Arguments.of("{\"email\": \"invalidemail\", \"password\": \"password\", \"nickname\": \"nickname\", \"profileImageUrl\": \"https://profileImageUrl.png\", \"bio\": \"bio\"}", "email"),
                // Password too short
                Arguments.of("{\"email\": \"valid@gmail.com\", \"password\": \"short\", \"nickname\": \"nickname\", \"profileImageUrl\": \"https://profileImageUrl.png\", \"bio\": \"bio\"}", "password"),
                // Password too long
                Arguments.of("{\"email\": \"valid@gmail.com\", \"password\": \"longlonglonglonglonglong\", \"nickname\": \"nickname\", \"profileImageUrl\": \"https://profileImageUrl.png\", \"bio\": \"bio\"}", "password"),
                // Invalid Password format
                Arguments.of("{\"email\": \"valid@gmail.com\", \"password\": \"password)(**&&\", \"nickname\": \"nickname\", \"profileImageUrl\": \"https://profileImageUrl.png\", \"bio\": \"bio\"}", "password"),
                // Empty nickname
                Arguments.of("{\"email\": \"valid@gmail.com\", \"password\": \"password\", \"nickname\": \"\", \"profileImageUrl\": \"https://profileImageUrl.png\", \"bio\": \"bio\"}", "nickname"),
                // Invalid nickname format
                Arguments.of("{\"email\": \"valid@gmail.com\", \"password\": \"password\", \"nickname\": \"nick name\", \"profileImageUrl\": \"https://profileImageUrl.png\", \"bio\": \"bio\"}", "nickname"),
                // Invalid URL format
                Arguments.of("{\"email\": \"valid@gmail.com\", \"password\": \"password\", \"nickname\": \"nickname\", \"profileImageUrl\": \"invalid_url\", \"bio\": \"bio\"}", "profileImageUrl"),
                // Bio too long
                Arguments.of("{\"email\": \"valid@gmail.com\", \"password\": \"password!\", \"nickname\": \"nickname\", \"profileImageUrl\": \"https://profileImageUrl.png\", \"bio\": \"" + "가".repeat(101) + "\"}", "bio")
        );
    }
}
