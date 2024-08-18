package com.storage.repository;

import com.storage.entity.Product;
import com.storage.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByUser(User user);
}
