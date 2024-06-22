package com.app.travelboard.storage.repository;

import com.app.travelboard.storage.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
}
