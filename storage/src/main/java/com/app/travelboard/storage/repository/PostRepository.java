package com.app.travelboard.storage.repository;

import com.app.travelboard.storage.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
