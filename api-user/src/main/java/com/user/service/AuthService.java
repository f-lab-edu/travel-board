package com.user.service;

import com.storage.entity.User;
import com.storage.repository.UserRepository;
import com.user.domain.user.UserManager;
import com.user.utils.error.CommonException;
import com.user.utils.token.JwtTokenProvider;
import com.user.utils.token.TokenResponse;
import com.user.utils.token.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static com.user.utils.error.ErrorType.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Transactional
    public TokenResponse createTokens(Long userId) {
        Date now = new Date();
        String accessToken = jwtTokenProvider.generateToken(TokenType.ACCESS, userId, now);
        String refreshToken = jwtTokenProvider.generateToken(TokenType.REFRESH, userId, now);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(USER_NOT_FOUND));
        UserManager.updateRefreshToken(user, refreshToken);

        return TokenResponse.of(accessToken, refreshToken);
    }
}
