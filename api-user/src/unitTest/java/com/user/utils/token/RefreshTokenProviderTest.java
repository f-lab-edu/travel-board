package com.user.utils.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class RefreshTokenProviderTest {

    private JwtTokenProvider refreshTokenProvider;
    private String secret;
    private long validityInDays;

    @BeforeEach
    void setUp() {
        this.secret = "testRefreshTokenSecrettestRefreshTokenSecrettestRefreshTokenSecret";
        this.validityInDays = 1;
        this.refreshTokenProvider = new JwtTokenProvider(secret, validityInDays);
    }

    @Test
    @DisplayName("리프래쉬 토큰은 정상적으로 생성되어야 한다")
    void generateTokenSuccess() {
        String token = refreshTokenProvider.generateToken(new Date());
        assertThat(token).isNotBlank();
    }

    @Test
    @DisplayName("생성된 리프래쉬 토큰은 JWT 형식을 따라야 한다")
    void generateTokenFormat() {
        Date now = new Date();
        String token = refreshTokenProvider.generateToken(now);

        String[] tokenParts = token.split("\\.");
        Claims claim = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret)))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(tokenParts).hasSize(3);
        assertThat(claim.getSubject()).isEqualTo("REFRESH");
        // The library is rounding down the last three digits of Unix time(ex. 1721396135444 -> 1721396135000)
        assertThat(claim.getIssuedAt().getTime()).isCloseTo(now.getTime(), within(1000L));
        long expectedExpirationTime = now.getTime() + validityInDays * 24L * 3600L * 1000L;
        assertThat(claim.getExpiration().getTime()).isCloseTo(expectedExpirationTime, within(1000L));
    }

}
