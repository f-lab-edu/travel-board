package com.storage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_comment_user_id"))
    private User author;

    @ManyToOne
    @JoinColumn(name = "post_id", foreignKey = @ForeignKey(name = "FK_comment_post_id"))
    private Post post;

    @ManyToOne
    @JoinColumn(name = "parent_comment_id", foreignKey = @ForeignKey(name = "FK_comment_parent_comment_id"))
    private Comment parentComment;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;
}
