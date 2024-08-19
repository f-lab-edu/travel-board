package com.user.api;

import com.storage.entity.Account;
import com.storage.entity.Product;
import com.storage.entity.ProductLevel;
import com.storage.entity.User;
import com.storage.repository.AccountRepository;
import com.storage.repository.PostRepository;
import com.storage.repository.ProductRepository;
import com.storage.repository.UserRepository;
import com.user.E2eTestSupport;
import com.user.dto.request.PostRegisterRequest;
import com.user.support.fixture.dto.request.PostRegisterRequestFixtureFactory;
import com.user.support.fixture.entity.AccountFixtureFactory;
import com.user.support.fixture.entity.ProductFixtureFactory;
import com.user.support.fixture.entity.UserFixtureFactory;
import com.user.utils.token.JwtTokenProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.stream.Stream;

import static com.user.enums.ErrorType.INVALID_REQUEST;
import static com.user.enums.ErrorType.LOGIN_REQUIRED;
import static com.user.enums.ErrorType.PRODUCT_NOT_FOUND;
import static com.user.enums.ErrorType.PRODUCT_PREMIUM_REQUIRED;
import static com.user.enums.TokenType.ACCESS;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class PostApiTest extends E2eTestSupport {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PostRepository postRepository;


    @AfterEach
    void tearDown() {
        postRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        accountRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("일반 게시물 등록이 성공한다")
    void registerNotPremiumPostSuccess() {
        // given
        String accessToken = createAndSaveUserWithAccessToken();

        PostRegisterRequest request = PostRegisterRequestFixtureFactory.createWithNeedPremium(false);

        given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .contentType(JSON)
                .body(request)
        .when()
                .post("/posts")
        .then()
                .statusCode(201);
    }

    @Test
    @DisplayName("프리미엄 게시물 등록이 성공한다")
    void registerPremiumPostSuccess() {
        // given
        Account account = AccountFixtureFactory.create();
        User user = UserFixtureFactory.create(account);
        accountRepository.save(account);
        userRepository.save(user);
        LocalDateTime endAt = LocalDateTime.now().plusDays(1L);
        Product product = ProductFixtureFactory.createWith(user, ProductLevel.PREMIUM, endAt);
        productRepository.save(product);
        String accessToken = jwtTokenProvider.generateToken(ACCESS, user.getId(), new Date());

        PostRegisterRequest request = PostRegisterRequestFixtureFactory.createWithNeedPremium(true);

        given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .contentType(JSON)
                .body(request)
        .when()
                .post("/posts")
        .then()
                .statusCode(201);
    }

    @Test
    @DisplayName("게시물 등록시 access token이 없으면 게시물 등록에 실패한다")
    void registerPostWithoutAccessToken() {
        // given
        PostRegisterRequest request = PostRegisterRequestFixtureFactory.createWithNeedPremium(false);

        given()
                .contentType(JSON)
                .body(request)
        .when()
                .post("/posts")
        .then()
                .statusCode(401)
                .body("code", equalTo(401))
                .body("message", equalTo(LOGIN_REQUIRED.getMessage()))
                .body("validations", equalTo(Map.of()));
    }

    @Test
    @DisplayName("프리미엄 게시물 등록시 프리미엄 회원이 아니면 게시물 등록에 실패한다")
    void registerPremiumPostWhenNotPremiumProduct() {
        // given
        Account account = AccountFixtureFactory.create();
        User user = UserFixtureFactory.create(account);
        accountRepository.save(account);
        userRepository.save(user);
        Product product = ProductFixtureFactory.createWith(user, ProductLevel.BASIC);
        productRepository.save(product);
        String accessToken = jwtTokenProvider.generateToken(ACCESS, user.getId(), new Date());

        PostRegisterRequest request = PostRegisterRequestFixtureFactory.createWithNeedPremium(true);

        given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .contentType(JSON)
                .body(request)
        .when()
                .post("/posts")
        .then()
                .statusCode(402)
                .body("code", equalTo(402))
                .body("message", equalTo(PRODUCT_PREMIUM_REQUIRED.getMessage()))
                .body("validations", equalTo(Map.of()));
    }

    @Test
    @DisplayName("프리미엄 게시물 등록시 프리미엄 회원 기간이 만료되면 게시물 등록에 실패한다")
    void registerPremiumPostWhenProductExpired() {
        // given
        Account account = AccountFixtureFactory.create();
        User user = UserFixtureFactory.create(account);
        accountRepository.save(account);
        userRepository.save(user);
        LocalDateTime endAt = LocalDateTime.now().minusDays(1L);
        Product product = ProductFixtureFactory.createWith(user, ProductLevel.PREMIUM, endAt);
        productRepository.save(product);
        String accessToken = jwtTokenProvider.generateToken(ACCESS, user.getId(), new Date());

        PostRegisterRequest request = PostRegisterRequestFixtureFactory.createWithNeedPremium(true);

        given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .contentType(JSON)
                .body(request)
        .when()
                .post("/posts")
        .then()
                .statusCode(402)
                .body("code", equalTo(402))
                .body("message", equalTo(PRODUCT_PREMIUM_REQUIRED.getMessage()))
                .body("validations", equalTo(Map.of()));
    }

    @Test
    @DisplayName("프리미엄 게시물 등록시 상품이 등록되지 않았으면 게시물 등록에 실패한다")
    void registerPremiumPostWhenProductNotFound() {
        // given
        String accessToken = createAndSaveUserWithAccessToken();

        PostRegisterRequest request = PostRegisterRequestFixtureFactory.createWithNeedPremium(true);

        given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .contentType(JSON)
                .body(request)
        .when()
                .post("/posts")
        .then()
                .statusCode(404)
                .body("code", equalTo(404))
                .body("message", equalTo(PRODUCT_NOT_FOUND.getMessage()))
                .body("validations", equalTo(Map.of()));
    }

    @TestFactory
    @DisplayName("게시물 등록시 장소 유효성 검사에 실패하면 게시물 등록에 실패한다")
    Stream<DynamicTest> registerPostFailureWhenLocationInvalid() {
        // given
        String accessToken = createAndSaveUserWithAccessToken();

        return PostRegisterRequestFixtureFactory.getInvalidLocationRequests().stream()
                .map(request -> dynamicTest(
                                String.format("장소에 %s를 입력하면 유효성 검사에 실패한다", request.location()),
                                () -> given()
                                        .header(AUTHORIZATION, "Bearer " + accessToken)
                                        .contentType(JSON)
                                        .body(request)
                                .when()
                                        .post("/posts")
                                .then()
                                        .statusCode(400)
                                        .body("code", equalTo(400))
                                        .body("message", equalTo(INVALID_REQUEST.getMessage()))
                                        .body("validations", hasKey("location"))
                            )
                );
    }

    @TestFactory
    @DisplayName("게시물 등록시 제목 유효성 검사에 실패하면 게시물 등록에 실패한다")
    Stream<DynamicTest> registerPostFailureWhenTitleInvalid() {
        // given
        String accessToken = createAndSaveUserWithAccessToken();

        return PostRegisterRequestFixtureFactory.getInvalidTitleRequests().stream()
                .map(request -> dynamicTest(
                                String.format("제목에 %s를 입력하면 유효성 검사에 실패한다", request.title()),
                                () -> given()
                                        .header(AUTHORIZATION, "Bearer " + accessToken)
                                        .contentType(JSON)
                                        .body(request)
                                .when()
                                        .post("/posts")
                                .then()
                                        .statusCode(400)
                                        .body("code", equalTo(400))
                                        .body("message", equalTo(INVALID_REQUEST.getMessage()))
                                        .body("validations", hasKey("title"))
                            )
                );
    }

    @TestFactory
    @DisplayName("게시물 등록시 내용 유효성 검사에 실패하면 게시물 등록에 실패한다")
    Stream<DynamicTest> registerPostFailureWhenContentInvalid() {
        // given
        String accessToken = createAndSaveUserWithAccessToken();

        return PostRegisterRequestFixtureFactory.getInvalidContentRequests().stream()
                .map(request -> dynamicTest(
                                String.format("내용에 %s를 입력하면 유효성 검사에 실패한다", request.content()),
                                () -> given()
                                        .header(AUTHORIZATION, "Bearer " + accessToken)
                                        .contentType(JSON)
                                        .body(request)
                                .when()
                                        .post("/posts")
                                .then()
                                        .statusCode(400)
                                        .body("code", equalTo(400))
                                        .body("message", equalTo(INVALID_REQUEST.getMessage()))
                                        .body("validations", hasKey("content"))
                            )
                );
    }

    @TestFactory
    @DisplayName("게시물 등록시 needPremium 유효성 검사에 실패하면 게시물 등록에 실패한다")
    Stream<DynamicTest> registerPostFailureWhenNeedPremiumInvalid() {
        // given
        String accessToken = createAndSaveUserWithAccessToken();

        return PostRegisterRequestFixtureFactory.getInvalidNeedPremiumRequests().stream()
                .map(request -> dynamicTest(
                                String.format("needPremium에 %s를 입력하면 유효성 검사에 실패한다", request.needPremium()),
                                () -> given()
                                        .header(AUTHORIZATION, "Bearer " + accessToken)
                                        .contentType(JSON)
                                        .body(request)
                                .when()
                                        .post("/posts")
                                .then()
                                        .statusCode(400)
                                        .body("code", equalTo(400))
                                        .body("message", equalTo(INVALID_REQUEST.getMessage()))
                                        .body("validations", hasKey("needPremium"))
                            )
                );
    }

    private String createAndSaveUserWithAccessToken() {
        Account account = AccountFixtureFactory.create();
        User user = UserFixtureFactory.create(account);
        accountRepository.save(account);
        userRepository.save(user);
        return jwtTokenProvider.generateToken(ACCESS, user.getId(), new Date());
    }
}
