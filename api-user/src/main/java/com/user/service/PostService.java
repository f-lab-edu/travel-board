package com.user.service;

import com.storage.entity.Post;
import com.storage.entity.Product;
import com.storage.entity.User;
import com.storage.repository.PostRepository;
import com.storage.repository.ProductRepository;
import com.user.domain.post.PostCreator;
import com.user.domain.user.ProductValidator;
import com.user.dto.request.PostRegisterRequest;
import com.user.enums.ErrorType;
import com.user.utils.error.CommonException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.user.enums.ErrorType.PRODUCT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PostService {

    private final ProductValidator productValidator;
    private final ProductRepository productRepository;
    private final PostRepository postRepository;

    @Transactional
    public void register(User user, PostRegisterRequest request) {
        if (request.needPremium()) {
            Product product = productRepository.findByUser(user)
                    .orElseThrow(() -> new CommonException(PRODUCT_NOT_FOUND));
            boolean isPremium = productValidator.isPremiumProduct(product, LocalDateTime.now());
            if (!isPremium) {
                throw new CommonException(ErrorType.PRODUCT_PREMIUM_REQUIRED);
            }
        }
        Post post = PostCreator.create(
                user, request.location(), request.title(), request.content(), request.needPremium());
        postRepository.save(post);
    }
}
