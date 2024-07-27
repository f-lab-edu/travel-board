package com.user.domain.account;

import com.storage.entity.Account;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AccountCreator {

    public static Account create(String email, String password, PasswordEncoder passwordEncoder) {
        return Account.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();
    }
}
