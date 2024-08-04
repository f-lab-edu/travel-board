package com.user.support.fixture.entity;

import com.storage.entity.User;

public class UserFixtureFactory {

    public static User create() {
        return User.builder()
                .account(AccountFixtureFactory.create())
                .nickname("nickname")
                .profileImageUrl("https://travel/profileImageUrl.png")
                .bio("introduce myself")
                .refreshToken("refreshToken")
                .build();
    }
}
