package com.storage.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_product_user_id"))
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(30)")
    private ProductLevel level = ProductLevel.BASIC;

    @Column
    private LocalDateTime startAt;

    @Column
    private LocalDateTime endAt;

    @Builder
    private Product(User user, ProductLevel level, LocalDateTime startAt, LocalDateTime endAt) {
        this.user = user;
        this.level = level;
        this.startAt = startAt;
        this.endAt = endAt;
    }
}
