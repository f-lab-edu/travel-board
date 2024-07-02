package com.storage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    private String tokenValue;

    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    @Builder
    private RefreshToken(String tokenValue, Account account, LocalDateTime expiredAt) {
        this.tokenValue = tokenValue;
        this.account = account;
        this.expiredAt = expiredAt;
    }
}
