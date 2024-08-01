package com.user.controller;

import com.storage.entity.User;
import com.user.config.security.UserPrincipal;
import com.user.dto.request.AccessTokenReissueRequest;
import com.user.dto.request.LoginRequest;
import com.user.dto.request.UserRegisterRequest;
import com.user.dto.response.AccessTokenResponse;
import com.user.dto.response.TokenResponse;
import com.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody @Valid UserRegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        authService.login(request);
        User user = getPrincipalUser();
        TokenResponse tokenResponse = authService.createTokens(user);
        authService.registerRefreshToken(user, tokenResponse.refreshToken());
        return ResponseEntity.ok(tokenResponse);
    }

    @PatchMapping("/access-token")
    public ResponseEntity<AccessTokenResponse> reissueAccessToken(@RequestBody @Valid AccessTokenReissueRequest request) {
        String accessToken = authService.reissueAccessToken(request.refreshToken());
        return ResponseEntity.ok(AccessTokenResponse.of(accessToken));
    }

    private User getPrincipalUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return principal.getUser();
    }
}
