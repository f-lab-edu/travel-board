package com.user.service.token;

import com.storage.entity.Account;
import com.storage.entity.RefreshToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class RefreshTokenProvider {

    private final long validTimeInDays;

    public RefreshTokenProvider(@Value("${token.refresh.valid-time-in-days}") long validTimeInDays) {
        this.validTimeInDays = validTimeInDays;
    }

    public RefreshToken createToken(Account account) {
        String uuid = UUID.randomUUID().toString();
        LocalDateTime expiredAt = LocalDateTime.now().plusDays(validTimeInDays);

        return RefreshToken.builder()
                .tokenValue(uuid)
                .account(account)
                .expiredAt(expiredAt)
                .build();
    }
}
