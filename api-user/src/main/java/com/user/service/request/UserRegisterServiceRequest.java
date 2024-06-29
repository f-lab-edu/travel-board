package com.user.service.request;

import com.storage.entity.Account;
import com.storage.entity.User;
import lombok.Builder;

@Builder
public record UserRegisterServiceRequest(
        String email,
        String password,
        String nickname,
        String profileImageUrl,
        String bio
) {

    public User toUser(Account account) {
        return User.createNew(account, nickname, profileImageUrl, bio);
    }
}
