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
import lombok.NoArgsConstructor;

import java.util.Date;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class JwtTokenProvider {

    public static String generateToken(TokenType tokenType, TokenPayload tokenPayload, Date now) {
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

    public static TokenPayload getPayload(TokenType tokenType, String token) {
        Claims claims = getClaims(tokenType, token);
        return TokenPayload.from(claims);
    }

    private static Claims getClaims(TokenType tokenType, String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(tokenType.getTokenProperty().getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            String subject = claims.getSubject();
            checkSubject(tokenType, subject);
            return claims;
        } catch (MalformedJwtException | SignatureException | UnsupportedJwtException | IllegalArgumentException e) {
            throw new CommonException(ErrorType.INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new CommonException(ErrorType.TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new CommonException(ErrorType.UNAUTHORIZED_TOKEN);
        }
    }

    private static void checkSubject(TokenType tokenType, String subject) {
        if (subject == null || !subject.equals(tokenType.name())) {
            throw new CommonException(ErrorType.INVALID_TOKEN);
        }
    }
}
