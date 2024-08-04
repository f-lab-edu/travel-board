package com.user.support.fixture.entity;

import com.storage.entity.Account;

public class AccountFixtureFactory {

    public static Account create() {
        return Account.builder()
                .email("valid@email.com")
                .password("encodedPassword")
                .build();
    }
}
