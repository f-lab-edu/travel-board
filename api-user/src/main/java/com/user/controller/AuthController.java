package com.user.controller;

import com.user.config.UserPrincipal;
import com.user.controller.request.UserRegisterRequest;
import com.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody @Valid UserRegisterRequest request) {
        userService.register(request);
        return ResponseEntity.status(CREATED).build();
    }

    @GetMapping("/ping")
    public ResponseEntity<Long> ping(@AuthenticationPrincipal UserPrincipal principal) {
        Long userId = principal.getUserId();
        return ResponseEntity.ok(userId);
    }
}
