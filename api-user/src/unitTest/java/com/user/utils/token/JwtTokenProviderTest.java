package com.user.utils.token;

import com.user.utils.error.CommonException;
import com.user.utils.error.ErrorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.Mockito.when;

class JwtTokenProviderTest {

    @Mock
    private TokenType access;

    @Mock
    private TokenType refresh;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(access.getTokenProperty()).thenReturn(new JwtTokenProperties.TokenProperty(
                "unitaTestAccessTokenSecretQWERTYUIOP12345678900",
                Duration.ofMinutes(30)));
        when(access.name()).thenReturn("ACCESS");
        when(refresh.getTokenProperty()).thenReturn(new JwtTokenProperties.TokenProperty(
                "unitrTestAccessTokenSecretQWERTYUIOP12345678900",
                Duration.ofDays(7)));
        when(refresh.name()).thenReturn("REFRESH");
    }

    @Test
    @DisplayName("Access 토큰을 생성하고 유저 아이디를 추출할 수 있다")
    void generateAccessToken() {
        // given
        TokenPayload tokenPayload = new TokenPayload("email@gmail.com", 1L, 1L);
        Date now = new Date();

        // when
        String token = JwtTokenProvider.generateToken(access, tokenPayload, now);
        TokenPayload result = JwtTokenProvider.getPayload(access, token);

        // then
        assertThat(result).isEqualTo(tokenPayload);
    }

    @Test
    @DisplayName("Refresh 토큰을 생성하고 유저 아이디를 추출할 수 있다")
    void generateRefreshToken() {
        // given
        TokenPayload tokenPayload = new TokenPayload("email@gmail.com", 1L, 1L);
        Date now = new Date();

        // when
        String token = JwtTokenProvider.generateToken(refresh, tokenPayload, now);
        TokenPayload result = JwtTokenProvider.getPayload(refresh, token);

        // then
        assertThat(result).isEqualTo(tokenPayload);
    }

    /**
     * expired token will throw ExpiredJwtException
     * it should be caught and rethrown as CommonException(ErrorType.TOKEN_EXPIRED)
     */
    @Test
    @DisplayName("만료된 토큰은 TOKEN_EXPIRED 에러를 발생시킨다")
    void expiredToken() {
        // given
        TokenPayload tokenPayload = new TokenPayload("email@gmail.com", 1L, 1L);
        Date now = new Date(1721396135444L);
        String token = JwtTokenProvider.generateToken(access, tokenPayload, now);

        // when & then
        assertThatThrownBy(() -> JwtTokenProvider.getPayload(access, token))
                .isInstanceOf(CommonException.class)
                .hasMessage(ErrorType.TOKEN_EXPIRED.getMessage());
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
                        () -> assertThatThrownBy(() -> JwtTokenProvider.getPayload(access, token))
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
                        () -> assertThatThrownBy(() -> JwtTokenProvider.getPayload(access, token))
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
        assertThatThrownBy(() -> JwtTokenProvider.getPayload(access, token))
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
        TokenPayload tokenPayload = new TokenPayload("email@gmail.com", 1L, 1L);
        Date now = new Date();
        String token = JwtTokenProvider.generateToken(access, tokenPayload, now);

        when(access.name()).thenReturn("INCORRECT_SUBJECT");

        // when & then
        assertThatThrownBy(() -> JwtTokenProvider.getPayload(access, token))
                .isInstanceOf(CommonException.class)
                .hasMessage(ErrorType.UNAUTHORIZED_TOKEN.getMessage());
    }

    @Test
    @DisplayName("토큰의 subject가 null이면 UNAUTHORIZED_TOKEN 에러를 발생시킨다")
    void tokenContainsNullSubject() {
        // given
        TokenPayload tokenPayload = new TokenPayload("email@gmail.com", 1L, 1L);
        Date now = new Date();
        String token = JwtTokenProvider.generateToken(access, tokenPayload, now);

        when(access.name()).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> JwtTokenProvider.getPayload(access, token))
                .isInstanceOf(CommonException.class)
                .hasMessage(ErrorType.UNAUTHORIZED_TOKEN.getMessage());
    }

    @Test
    @DisplayName("미래에 생성되는 토큰은 유효하다")
    void tokenWithFutureGeneratedIsValid() {
        // given
        TokenPayload tokenPayload = new TokenPayload("email@gmail.com", 1L, 1L);
        Date futureDate = new Date(System.currentTimeMillis() + 3600000);
        String token = JwtTokenProvider.generateToken(access, tokenPayload, futureDate);

        // when
        TokenPayload result = JwtTokenProvider.getPayload(access, token);

        // then
        assertThat(result).isEqualTo(tokenPayload);
    }
}
