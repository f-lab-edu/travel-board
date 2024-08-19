package com.user.support.fixture.entity;

import com.storage.entity.Product;
import com.storage.entity.ProductLevel;
import com.storage.entity.User;

import java.time.LocalDateTime;

public class ProductFixtureFactory {

    public static Product createWith(User user, ProductLevel productLevel) {
        return Product.builder()
                .user(user)
                .level(productLevel)
                .startAt(LocalDateTime.of(2024, 1, 1, 0, 0))
                .endAt(LocalDateTime.of(2024, 12, 31, 23, 59))
                .build();
    }

    public static Product createWith(User user, ProductLevel productLevel, LocalDateTime endAt) {
        return Product.builder()
                .user(user)
                .level(productLevel)
                .startAt(LocalDateTime.of(2024, 1, 1, 0, 0))
                .endAt(endAt)
                .build();
    }
}
