package com.user.controller;

import com.storage.entity.User;
import com.user.config.security.CurrentUser;
import com.user.dto.request.PostRegisterRequest;
import com.user.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<Void> registerPost(@CurrentUser User user,
                                             @RequestBody @Valid PostRegisterRequest request) {
        postService.register(user, request);
        return ResponseEntity.status(CREATED).build();
    }
}
