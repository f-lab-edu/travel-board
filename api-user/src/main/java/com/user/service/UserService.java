package com.user.service;

import com.storage.entity.Account;
import com.storage.entity.RefreshToken;
import com.storage.entity.User;
import com.storage.repository.AccountRepository;
import com.storage.repository.RefreshTokenRepository;
import com.storage.repository.UserRepository;
import com.user.controller.request.UserRegisterRequest;
import com.user.controller.response.UserRegisterResponse;
import com.user.service.token.RefreshTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenProvider refreshTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserRegisterResponse register(UserRegisterRequest request) {
        accountRepository.findByEmail(request.email()).ifPresent(account -> {
            throw new IllegalStateException("Email is already in use");
        });

        String encodedPassword = passwordEncoder.encode(request.password());
        Account account = createAccount(request.email(), encodedPassword);
        RefreshToken refreshToken = refreshTokenProvider.createToken(account);
        User user = createUser(account, request.nickname(), request.profileImageUrl(), request.bio());

        accountRepository.save(account);
        refreshTokenRepository.save(refreshToken);
        userRepository.save(user);
        return UserRegisterResponse.of(user.getId(), refreshToken.getTokenValue(), refreshToken.getExpiredAt());
    }

    private Account createAccount(String email, String password) {
        return Account.builder()
                .email(email)
                .password(password)
                .build();
    }

    private User createUser(Account account, String nickname, String profileImageUrl, String bio) {
        return User.builder()
                .account(account)
                .nickname(nickname)
                .profileImageUrl(Optional.ofNullable(profileImageUrl).orElse(""))
                .bio(Optional.ofNullable(bio).orElse(""))
                .build();
    }
}
