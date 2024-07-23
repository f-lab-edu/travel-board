package com.user.utils.token;

import com.user.utils.error.CommonException;
import com.user.utils.error.ErrorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.mockito.InjectMocks;
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

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private TokenType access;

    @Mock
    private TokenType refresh;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(access.getTokenProperty()).thenReturn(new TokenProperties.TokenProperty(
                "unitaTestAccessTokenSecretQWERTYUIOP12345678900",
                Duration.ofMinutes(30)));
        when(refresh.getTokenProperty()).thenReturn(new TokenProperties.TokenProperty(
                "unitrTestAccessTokenSecretQWERTYUIOP12345678900",
                Duration.ofDays(7)));
    }

    @Test
    @DisplayName("Access 토큰을 생성하고 유저 아이디를 추출할 수 있다")
    void generateAccessToken() {
        // given
        Long userId = 1L;
        Date now = new Date();

        // when
        String token = jwtTokenProvider.generateToken(access, userId, now);
        Long result = jwtTokenProvider.getUserId(access, token);

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
        String token = jwtTokenProvider.generateToken(refresh, userId, now);
        Long result = jwtTokenProvider.getUserId(refresh, token);

        // then
        assertThat(result).isEqualTo(userId);
    }

    @Test
    @DisplayName("만료된 토큰은 TOKEN_EXPIRED 에러를 발생시킨다")
    void expiredToken() {
        // given
        Long userId = 1L;
        Date now = new Date(1721396135444L);
        String token = jwtTokenProvider.generateToken(access, userId, now);

        // when & then
        assertThatThrownBy(() -> jwtTokenProvider.getUserId(access, token))
                .isInstanceOf(CommonException.class)
                .hasMessage(ErrorType.TOKEN_EXPIRED.getMessage());
    }

    @Test
    @DisplayName("형식이 잘못된 토큰은 INVALID_TOKEN 에러를 발생시킨다")
    void invalidToken() {
        // given
        String token = "invalid.token.string";

        // when & then
        assertThatThrownBy(() -> jwtTokenProvider.getUserId(access, token))
                .isInstanceOf(CommonException.class)
                .hasMessage(ErrorType.INVALID_TOKEN.getMessage());
    }

    @TestFactory
    Stream<DynamicTest> invalidTokenFormatThrowsInvalidTokenError() {
        // given
        List<String> tokens = List.of("invalid_token", "invalid.token.string", "eyJhbGciOiJIUzI1NiJ9.invalid.payload", "eyJhbGciOiJIUzI1NiJ9..payload");

        // when & then
        return tokens.stream()
                .map(token -> dynamicTest("형식이 잘못된 토큰이면 INVALID_TOKEN 에러를 발생시킨다",
                        () -> assertThatThrownBy(() -> jwtTokenProvider.getUserId(access, token))
                                .isInstanceOf(CommonException.class)
                                .hasMessage(ErrorType.INVALID_TOKEN.getMessage())));
    }

    @TestFactory
    Stream<DynamicTest> blankStringTokenThrowsInvalidTokenError() {
        // given
        List<String> tokens = List.of("", " ");

        // when & then
        return tokens.stream()
                .map(token -> dynamicTest("토큰이 Blank 문자열이면 INVALID_TOKEN 에러를 발생시킨다",
                        () -> assertThatThrownBy(() -> jwtTokenProvider.getUserId(access, token))
                                .isInstanceOf(CommonException.class)
                                .hasMessage(ErrorType.INVALID_TOKEN.getMessage())));
    }

    @Test
    @DisplayName("미래에 만료되는 토큰은 유효하다")
    void tokenWithFutureExpirationIsValid() {
        // given
        Long userId = 1L;
        Date futureDate = new Date(System.currentTimeMillis() + 3600000);
        String token = jwtTokenProvider.generateToken(access, userId, futureDate);

        // when
        Long result = jwtTokenProvider.getUserId(access, token);

        // then
        assertThat(result).isEqualTo(userId);
    }
}