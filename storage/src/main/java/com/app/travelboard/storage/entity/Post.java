package com.app.travelboard.storage.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_post_user_id"))
    private User author;

    @Column(length = 50)
    private String location;

    private String title;

    @Lob
    @Column(columnDefinition = "MEDIUMTEXT")
    private String content;

    private int views;

    private boolean needPremium;
}
