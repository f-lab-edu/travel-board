package com.storage.repository;

import com.storage.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
            SELECT u
            FROM User u
                JOIN FETCH u.account a
            WHERE a.email = :email
            """)
    Optional<User> findByAccountEmail(String email);
}
