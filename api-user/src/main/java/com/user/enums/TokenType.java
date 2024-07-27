package com.user.enums;

import lombok.Getter;

import javax.crypto.SecretKey;

@Getter
public enum TokenType {

    ACCESS, REFRESH;

    private SecretKey secretKey;
    private long validityMilliseconds;

    public void setSecretKey(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    public void setValidityMilliseconds(long validityMilliseconds) {
        this.validityMilliseconds = validityMilliseconds;
    }
}
