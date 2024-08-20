package com.user.domain.user;

import com.storage.entity.Post;
import com.storage.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostSaver {

    private final PostRepository postRepository;

    public Post save(Post post) {
        return postRepository.save(post);
    }
}
