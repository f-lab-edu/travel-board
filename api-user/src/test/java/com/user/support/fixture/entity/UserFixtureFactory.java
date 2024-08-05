package com.user.support.fixture.entity;

import com.storage.entity.Account;
import com.storage.entity.User;

public class UserFixtureFactory {

    public static User create(Account account) {
        return User.builder()
                .account(account)
                .nickname("nickname")
                .profileImageUrl("https://travel/profileImageUrl.png")
                .bio("introduce myself")
                .refreshToken("refreshToken")
                .build();
    }
}
