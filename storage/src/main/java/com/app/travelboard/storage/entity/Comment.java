package com.app.travelboard.storage.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

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
