package com.user.service;

import com.storage.entity.Post;
import com.storage.entity.Product;
import com.storage.entity.User;
import com.user.domain.post.PostCreator;
import com.user.domain.user.PostSaver;
import com.user.domain.user.ProductFinder;
import com.user.domain.user.ProductValidator;
import com.user.dto.request.PostRegisterRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostService {

    private final ProductFinder productFinder;
    private final ProductValidator productValidator;
    private final PostSaver postSaver;

    @Transactional
    public void register(User user, PostRegisterRequest request) {
        if (request.needPremium()) {
            Product product = productFinder.find(user);
            productValidator.validatePremium(product, LocalDateTime.now());
        }
        Post post = PostCreator.create(
                user, request.location(), request.title(), request.content(), request.needPremium());
        postSaver.save(post);
    }
}
