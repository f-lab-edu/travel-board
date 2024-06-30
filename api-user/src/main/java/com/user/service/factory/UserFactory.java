package com.user.service.factory;

import com.storage.entity.Account;
import com.storage.entity.User;

public class UserFactory {

    public static User create(Account account, String nickname, String profileImageUrl, String bio) {
        return User.builder()
                .account(account)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .bio(bio)
                .build();
    }
}
