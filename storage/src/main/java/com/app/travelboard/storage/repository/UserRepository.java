package com.app.travelboard.storage.repository;

import com.app.travelboard.storage.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
