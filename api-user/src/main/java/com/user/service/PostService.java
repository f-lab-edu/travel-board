package com.user.service;

import com.storage.entity.User;
import com.user.domain.post.PostCreator;
import com.user.dto.request.PostRegisterRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostCreator postCreator;

    @Transactional
    public void register(User user, PostRegisterRequest request) {
        postCreator.create(user, request.location(), request.title(), request.content(), request.needPremium());
    }
}
