package com.user.domain.user;

import com.storage.entity.Account;
import com.storage.entity.User;

import java.util.Optional;

public class UserCreator {

    public static User create(Account account, String nickname, String profileImageUrl, String bio) {
        return User.builder()
                .account(account)
                .nickname(nickname)
                .profileImageUrl(Optional.ofNullable(profileImageUrl).orElse(""))
                .bio(Optional.ofNullable(bio).orElse(""))
                .build();
    }
}
