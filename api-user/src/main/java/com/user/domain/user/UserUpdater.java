package com.user.domain.user;

import com.storage.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserUpdater {

    public void updateRefreshToken(User user, String refreshToken) {
        user.setRefreshToken(refreshToken);
    }
}
