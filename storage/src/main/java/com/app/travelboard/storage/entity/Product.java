package com.app.travelboard.storage.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_product_user_id"))
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(30)")
    private ProductLevel level = ProductLevel.BASIC;

    @Column
    private LocalDateTime startAt;

    @Column
    private LocalDateTime endAt;
}
