package com.user.controller;

import com.storage.entity.User;
import com.user.config.security.CurrentUser;
import com.user.dto.request.AccessTokenReissueRequest;
import com.user.dto.request.UserRegisterRequest;
import com.user.dto.response.TokenResponse;
import com.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    @PatchMapping("/access-token")
    public ResponseEntity<TokenResponse> reissueAccessToken(@RequestBody @Valid AccessTokenReissueRequest request) {
        String accessToken = authService.reissueAccessToken(request.refreshToken());
        return ResponseEntity.ok(TokenResponse.of(accessToken));
    }

    @GetMapping("/ping")
    public ResponseEntity<Long> ping(@CurrentUser User user) {
        return ResponseEntity.ok(user.getId());
    }
}
