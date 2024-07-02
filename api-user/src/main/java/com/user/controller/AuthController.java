package com.user.controller;

import com.user.controller.request.UserRegisterRequest;
import com.user.controller.response.UserRegisterResponse;
import com.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final CookieProvider cookieProvider;
    private static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH";

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody @Valid UserRegisterRequest request) {
        UserRegisterResponse response = userService.register(request);
        ResponseCookie cookie = cookieProvider.createCookie(
                REFRESH_TOKEN_COOKIE_NAME,
                response.refreshTokenValue(),
                response.getMaxAgeForCookie()
        );
        URI location = URI.create(String.format("/users/%d", response.userId()));
        return ResponseEntity.created(location)
                .header(SET_COOKIE, cookie.toString())
                .build();
    }

}
