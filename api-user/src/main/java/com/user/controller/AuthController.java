package com.user.controller;

import com.user.controller.request.UserRegisterRequest;
import com.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public void signup(@RequestBody @Valid UserRegisterRequest request) {
        request.validate();
        userService.register(request.toServiceRequest());
    }

}
