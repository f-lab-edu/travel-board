package com.user.service;

import com.storage.entity.Account;
import com.storage.entity.User;
import com.storage.repository.AccountRepository;
import com.storage.repository.UserRepository;
import com.user.config.security.UserPrincipal;
import com.user.domain.account.AccountCreator;
import com.user.domain.user.UserCreator;
import com.user.domain.user.UserUpdater;
import com.user.dto.request.LoginRequest;
import com.user.dto.request.UserRegisterRequest;
import com.user.dto.response.TokenResponse;
import com.user.utils.error.CommonException;
import com.user.utils.token.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static com.user.enums.ErrorType.DUPLICATED_EMAIL;
import static com.user.enums.ErrorType.LOGIN_FAIL;
import static com.user.enums.ErrorType.USER_NOT_FOUND;
import static com.user.enums.TokenType.ACCESS;
import static com.user.enums.TokenType.REFRESH;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserUpdater userUpdater;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void register(UserRegisterRequest request) {
        accountRepository.findByEmail(request.email()).ifPresent(account -> {
            throw new CommonException(DUPLICATED_EMAIL);
        });

        Account account = AccountCreator.create(request.email(), request.password(), passwordEncoder);
        User user = UserCreator.create(account, request.nickname(), request.profileImageUrl(), request.bio());

        accountRepository.save(account);
        userRepository.save(user);
    }

    @Transactional
    public TokenResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByAccountEmail(loginRequest.email())
                .orElseThrow(() -> new CommonException(LOGIN_FAIL));

        if (!passwordEncoder.matches(loginRequest.password(), user.getAccount().getPassword())) {
            throw new CommonException(LOGIN_FAIL);
        }

        TokenResponse tokens = createTokens(user.getId(), new Date());
        userUpdater.updateRefreshToken(user, tokens.refreshToken());
        return tokens;
    }

    public UserPrincipal getUserPrincipal(Long userId) {
        User user = userRepository.findByIdWithAccount(userId)
                .orElseThrow(() -> new CommonException(USER_NOT_FOUND));
        return UserPrincipal.of(user);
    }

    public String reissueAccessToken(String refreshToken) {
        Long userId = jwtTokenProvider.getUserId(REFRESH, refreshToken);
        User user = userRepository.findByIdAndRefreshToken(userId, refreshToken)
                .orElseThrow(() -> new CommonException(USER_NOT_FOUND));
        return jwtTokenProvider.generateToken(ACCESS, user.getId(), new Date());
    }

    private TokenResponse createTokens(Long userId, Date now) {
        String accessToken = jwtTokenProvider.generateToken(ACCESS, userId, now);
        String refreshToken = jwtTokenProvider.generateToken(REFRESH, userId, now);
        return TokenResponse.of(accessToken, refreshToken);
    }
}
