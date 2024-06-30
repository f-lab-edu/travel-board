package com.user.service.factory;

import com.storage.entity.Account;

public class AccountFactory {

    public static Account create(String email, String password) {
        return Account.builder()
                .email(email)
                .password(password)
                .build();
    }
}
