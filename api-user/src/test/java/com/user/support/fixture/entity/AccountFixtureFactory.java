package com.user.support.fixture.entity;

import com.storage.entity.Account;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AccountFixtureFactory {

    public static Account create() {
        return Account.builder()
                .email("valid@email.com")
                .password("encodedPassword")
                .build();
    }

    public static Account create(PasswordEncoder passwordEncoder, String password) {
        return Account.builder()
                .email("valid@email.com")
                .password(passwordEncoder.encode(password))
                .build();
    }
}
