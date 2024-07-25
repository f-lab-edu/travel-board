package com.user.service;

import com.storage.entity.Account;
import com.storage.entity.User;
import com.storage.repository.AccountRepository;
import com.storage.repository.UserRepository;
import com.user.controller.request.UserRegisterRequest;
import com.user.domain.account.AccountManager;
import com.user.domain.user.UserManager;
import com.user.utils.error.CommonException;
import com.user.utils.token.JwtTokenProvider;
import com.user.utils.token.TokenPayload;
import com.user.utils.token.TokenResponse;
import com.user.utils.token.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static com.user.utils.error.ErrorType.DUPLICATED_EMAIL;
import static com.user.utils.error.ErrorType.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void register(UserRegisterRequest request) {
        accountRepository.findByEmail(request.email()).ifPresent(account -> {
            throw new CommonException(DUPLICATED_EMAIL);
        });

        Account account = AccountManager.create(request.email(), request.password(), passwordEncoder);
        User user = UserManager.create(account, request.nickname(), request.profileImageUrl(), request.bio());

        accountRepository.save(account);
        userRepository.save(user);
    }

    @Transactional
    public TokenResponse createTokens(TokenPayload tokenPayload) {
        Date now = new Date();
        String accessToken = jwtTokenProvider.generateToken(TokenType.ACCESS, tokenPayload, now);
        String refreshToken = jwtTokenProvider.generateToken(TokenType.REFRESH, tokenPayload, now);

        User user = userRepository.findById(tokenPayload.userId())
                .orElseThrow(() -> new CommonException(USER_NOT_FOUND));
        UserManager.updateRefreshToken(user, refreshToken);

        return TokenResponse.of(accessToken, refreshToken);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CommonException(USER_NOT_FOUND));
    }
}
