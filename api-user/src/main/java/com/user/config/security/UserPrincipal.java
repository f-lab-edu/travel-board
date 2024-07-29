package com.user.config.security;

import com.storage.entity.User;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Getter
public class UserPrincipal extends org.springframework.security.core.userdetails.User {

    private final User user;
    private static final String ROLE_USER = "ROLE_USER";

    public UserPrincipal(User user) {
        super(user.getAccount().getEmail(), user.getAccount().getPassword(), List.of(new SimpleGrantedAuthority(ROLE_USER)));
        this.user = user;
    }
}
