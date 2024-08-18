package com.user.config.security;

import com.storage.entity.User;
import com.user.support.fixture.entity.AccountFixtureFactory;
import com.user.support.fixture.entity.UserFixtureFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockUserPrincipalSecurityContextFactory implements WithSecurityContextFactory<WithMockUserPrincipal> {

    @Override
    public SecurityContext createSecurityContext(WithMockUserPrincipal customUser) {

        User user = UserFixtureFactory.create(AccountFixtureFactory.create());
        UserPrincipal principal = UserPrincipal.of(user);
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                principal, principal.getPassword(), principal.getAuthorities()
        );
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authentication);
        return context;
    }
}
