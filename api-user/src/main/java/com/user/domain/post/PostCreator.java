package com.user.domain.post;

import com.storage.entity.Post;
import com.storage.entity.User;
import com.storage.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostCreator {

    private final PostRepository postRepository;

    public Post create(User user, String location, String title, String content, Boolean needPremium) {
        Post post = Post.builder()
                .author(user)
                .location(location)
                .title(title)
                .content(content)
                .views(0)
                .needPremium(needPremium)
                .build();
        return postRepository.save(post);
    }
}
