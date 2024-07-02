package com.user.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        Long currentAccountId = getCurrentAccountId(authentication);
        return Optional.of(currentAccountId);
    }

    private Long getCurrentAccountId(Authentication authentication) {
        /*
         TODO login feature 진행시 principal에서 accountId를 가져오도록 수정
              현재 principal이 anonymousUser로 들어오고 있음
         */
        return -1L;
    }
}
