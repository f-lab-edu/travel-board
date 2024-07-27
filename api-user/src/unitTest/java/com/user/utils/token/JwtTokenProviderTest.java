package com.user.utils.token;

import com.user.enums.ErrorType;
import com.user.utils.error.CommonException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static com.user.enums.TokenType.ACCESS;
import static com.user.enums.TokenType.REFRESH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(
                "localTestAccessTokenSecretQWERTYUIOP12345678900", Duration.ofMinutes(30L),
                "localTestRefreshTokenSecretQWERTYUIOP1234567890", Duration.ofDays(7L)
        );
    }

    @Test
    @DisplayName("Access 토큰을 생성하고 유저 아이디를 추출할 수 있다")
    void generateAccessToken() {
        // given
        Long userId = 1L;
        Date now = new Date();

        // when
        String token = jwtTokenProvider.generateToken(ACCESS, userId, now);
        Long result = jwtTokenProvider.getUserId(ACCESS, token);

        // then
        assertThat(result).isEqualTo(userId);
    }

    @Test
    @DisplayName("Refresh 토큰을 생성하고 유저 아이디를 추출할 수 있다")
    void generateRefreshToken() {
        // given
        Long userId = 1L;
        Date now = new Date();

        // when
        String token = jwtTokenProvider.generateToken(REFRESH, userId, now);
        Long result = jwtTokenProvider.getUserId(REFRESH, token);

        // then
        assertThat(result).isEqualTo(userId);
    }

    /**
     * expired token will throw ExpiredJwtException
     * it should be caught and rethrown as CommonException(ErrorType.UNAUTHORIZED_TOKEN)
     */
    @Test
    @DisplayName("만료된 토큰은 UNAUTHORIZED_TOKEN 에러를 발생시킨다")
    void expiredToken() {
        // given
        Long userId = 1L;
        Date now = new Date(1721396135444L);
        String token = jwtTokenProvider.generateToken(ACCESS, userId, now);

        // when & then
        assertThatThrownBy(() -> jwtTokenProvider.getUserId(ACCESS, token))
                .isInstanceOf(CommonException.class)
                .hasMessage(ErrorType.UNAUTHORIZED_TOKEN.getMessage());
    }

    /**
     * invalid token(header, payload) format will throw MalformedJwtException
     * invalid token(signature) will throw SignatureException
     * unsupported jwt will throw UnsupportedJwtException
     * it should be caught and rethrown as CommonException(ErrorType.UNAUTHORIZED_TOKEN)
     */
    @TestFactory
    Stream<DynamicTest> invalidTokenFormatThrowsInvalidTokenError() {
        // given
        List<String> tokens = List.of("UNAUTHORIZED_TOKEN", "invalid.token.string", "eyJhbGciOiJIUzI1NiJ9.invalid.payload", "eyJhbGciOiJIUzI1NiJ9..payload");

        // when & then
        return tokens.stream()
                .map(token -> dynamicTest("형식이 잘못된 토큰이면 UNAUTHORIZED_TOKEN 에러를 발생시킨다",
                        () -> assertThatThrownBy(() -> jwtTokenProvider.getUserId(ACCESS, token))
                                .isInstanceOf(CommonException.class)
                                .hasMessage(ErrorType.UNAUTHORIZED_TOKEN.getMessage())));
    }

    /**
     * empty string token will throw IllegalArgumentException
     * it should be caught and rethrown as CommonException(ErrorType.UNAUTHORIZED_TOKEN)
     */
    @TestFactory
    Stream<DynamicTest> emptyStringTokenThrowsInvalidTokenError() {
        // given
        List<String> tokens = List.of("", " ");

        // when & then
        return tokens.stream()
                .map(token -> dynamicTest("토큰이 empty 문자열이면 UNAUTHORIZED_TOKEN 에러를 발생시킨다",
                        () -> assertThatThrownBy(() -> jwtTokenProvider.getUserId(ACCESS, token))
                                .isInstanceOf(CommonException.class)
                                .hasMessage(ErrorType.UNAUTHORIZED_TOKEN.getMessage())));
    }

    /**
     * null token will throw IllegalArgumentException
     * it should be caught and rethrown as CommonException(ErrorType.UNAUTHORIZED_TOKEN)
     */
    @Test
    @DisplayName("null 토큰은 UNAUTHORIZED_TOKEN 에러를 발생시킨다")
    void nullStringTokenThrowsInvalidTokenError() {
        // given
        String token = null;

        // when & then
        assertThatThrownBy(() -> jwtTokenProvider.getUserId(ACCESS, token))
                .isInstanceOf(CommonException.class)
                .hasMessage(ErrorType.UNAUTHORIZED_TOKEN.getMessage());
    }

    @Test
    @DisplayName("현재 알 수 없는 Token 에러는 UNAUTHORIZED_TOKEN 에러를 발생시킨다")
    void JwtTokenProviderThrowsUnauthorizedTokenError() {
        // JwtTokenProvider.getPayload(access, token) will throw CommonException(ErrorType.UNAUTHORIZED_TOKEN)
    }

    @Test
    @DisplayName("토큰의 subject가 TokenType과 일치하지 않으면 UNAUTHORIZED_TOKEN 에러를 발생시킨다")
    void tokenContainsIncorrectSubject() {
        // given
        Long userId = 1L;
        Date now = new Date();
        byte[] secretBytes = Base64.getDecoder().decode("localTestAccessTokenSecretQWERTYUIOP12345678900");
        SecretKey accessSecretKey = Keys.hmacShaKeyFor(secretBytes);
        long accessValidityMilliseconds = Duration.ofMinutes(30L).toMillis();

        String nullSubject = null;
        String token = Jwts.builder()
                .subject(nullSubject)
                .signWith(accessSecretKey)
                .claim("userId", userId)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessValidityMilliseconds))
                .compact();

        // when & then
        assertThatThrownBy(() -> jwtTokenProvider.getUserId(ACCESS, token))
                .isInstanceOf(CommonException.class)
                .hasMessage(ErrorType.UNAUTHORIZED_TOKEN.getMessage());
    }

    @Test
    @DisplayName("토큰의 subject가 null이면 UNAUTHORIZED_TOKEN 에러를 발생시킨다")
    void tokenContainsNullSubject() {
        // given
        Long userId = 1L;
        Date now = new Date();
        byte[] secretBytes = Base64.getDecoder().decode("localTestAccessTokenSecretQWERTYUIOP12345678900");
        SecretKey accessSecretKey = Keys.hmacShaKeyFor(secretBytes);
        long accessValidityMilliseconds = Duration.ofMinutes(30L).toMillis();

        String invalidSubject = "INVALID_SUBJECT";
        String token = Jwts.builder()
                .subject(invalidSubject)
                .signWith(accessSecretKey)
                .claim("userId", userId)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessValidityMilliseconds))
                .compact();

        // when & then
        assertThatThrownBy(() -> jwtTokenProvider.getUserId(ACCESS, token))
                .isInstanceOf(CommonException.class)
                .hasMessage(ErrorType.UNAUTHORIZED_TOKEN.getMessage());
    }

    @Test
    @DisplayName("미래에 생성되는 토큰은 유효하다")
    void tokenWithFutureGeneratedIsValid() {
        // given
        Long userId = 1L;
        Date futureDate = new Date(System.currentTimeMillis() + 3600000);
        String token = jwtTokenProvider.generateToken(ACCESS, userId, futureDate);

        // when
        Long result = jwtTokenProvider.getUserId(ACCESS, token);

        // then
        assertThat(result).isEqualTo(userId);
    }
}
