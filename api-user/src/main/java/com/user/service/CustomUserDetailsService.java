package com.user.service;

import com.storage.entity.User;
import com.storage.repository.UserRepository;
import com.user.config.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.user.enums.ErrorType.LOGIN_FAIL;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByAccountEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(LOGIN_FAIL.getMessage()));
        return UserPrincipal.of(user);
    }
}
