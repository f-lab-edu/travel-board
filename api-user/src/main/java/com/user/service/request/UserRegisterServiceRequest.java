package com.user.service.request;

import com.storage.entity.Account;
import com.storage.entity.User;
import com.user.service.factory.UserFactory;
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
        return UserFactory.create(account, nickname, profileImageUrl, bio);
    }
}
