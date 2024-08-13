package com.storage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_post_user_id"))
    private User author;

    @Column(length = 50)
    private String location;

    @Column
    private String title;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String content;

    @Column
    private int views;

    @Column
    private boolean needPremium;

    @Builder
    public Post(User author, String location, String title, String content, int views, boolean needPremium) {
        this.author = author;
        this.location = location;
        this.title = title;
        this.content = content;
        this.views = views;
        this.needPremium = needPremium;
    }
}
