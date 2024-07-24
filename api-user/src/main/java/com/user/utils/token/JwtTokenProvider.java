package com.user.utils.token;

import com.user.utils.error.CommonException;
import com.user.utils.error.ErrorType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    public String generateToken(TokenType tokenType, TokenPayload tokenPayload, Date now) {
        Date expiration = new Date(now.getTime() + tokenType.getTokenProperty().getValidityInMillisSeconds());
        return Jwts.builder()
                .subject(tokenType.name())
                .signWith(tokenType.getTokenProperty().getSecretKey())
                .claim("email", tokenPayload.email())
                .claim("userId", tokenPayload.userId())
                .claim("accountId", tokenPayload.accountId())
                .issuedAt(now)
                .expiration(expiration)
                .compact();
    }

    public TokenPayload getUserId(TokenType tokenType, String token) {
        Claims tokenPayload = getClaims(tokenType, token);
        String email = tokenPayload.get("email", String.class);
        Long userId = tokenPayload.get("userId", Long.class);
        Long accountId = tokenPayload.get("accountId", Long.class);
        return TokenPayload.of(email, userId, accountId);
    }

    private Claims getClaims(TokenType tokenType, String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(tokenType.getTokenProperty().getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            if (!claims.getSubject().equals(tokenType.name())) {
                throw new CommonException(ErrorType.INVALID_TOKEN);
            }
            return claims;
        } catch (MalformedJwtException | SignatureException | UnsupportedJwtException | IllegalArgumentException e) {
            throw new CommonException(ErrorType.INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new CommonException(ErrorType.TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new RuntimeException(e);
        }
    }

}
