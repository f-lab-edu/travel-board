package com.app.travelboard.storage.repository;

import com.app.travelboard.storage.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
