package com.user.service;

import com.storage.entity.Post;
import com.storage.entity.User;
import com.storage.repository.PostRepository;
import com.user.domain.post.PostCreator;
import com.user.dto.request.PostRegisterRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    @Transactional
    public void register(User user, PostRegisterRequest request) {
        Post post = PostCreator.create(
                user, request.location(), request.title(), request.content(), request.needPremium());
        postRepository.save(post);
    }
}
