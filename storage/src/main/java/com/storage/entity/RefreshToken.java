package com.storage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID tokenValue;

    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Column
    private LocalDateTime expiredAt;

    @Builder
    private RefreshToken(Account account, LocalDateTime expiredAt) {
        this.account = account;
        this.expiredAt = expiredAt;
    }
}
