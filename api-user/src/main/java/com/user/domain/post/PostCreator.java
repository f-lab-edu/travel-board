package com.user.domain.post;

import com.storage.entity.Post;
import com.storage.entity.User;

public class PostCreator {

    public static Post create(User user, String location, String title, String content, Boolean needPremium) {
        return Post.builder()
                .author(user)
                .location(location)
                .title(title)
                .content(content)
                .views(0)
                .needPremium(needPremium)
                .build();
    }
}
