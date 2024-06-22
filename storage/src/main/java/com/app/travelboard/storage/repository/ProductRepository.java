package com.app.travelboard.storage.repository;

import com.app.travelboard.storage.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
