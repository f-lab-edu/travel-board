package com.user.config;

import com.user.utils.token.TokenPayload;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class UserPrincipal extends User {

    private final Long accountId;
    private final Long userId;
    private static final String ROLE_USER = "ROLE_USER";

    public UserPrincipal(com.storage.entity.User user) {
        super(user.getAccount().getEmail(), user.getAccount().getPassword(), List.of(new SimpleGrantedAuthority(ROLE_USER)));
        this.accountId = user.getAccount().getId();
        this.userId = user.getId();
    }

    public UserPrincipal(TokenPayload tokenPayload) {
        super(tokenPayload.email(), "encoded", List.of(new SimpleGrantedAuthority(ROLE_USER)));
        this.accountId = tokenPayload.accountId();
        this.userId = tokenPayload.userId();
    }
}
