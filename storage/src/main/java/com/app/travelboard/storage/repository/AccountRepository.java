package com.app.travelboard.storage.repository;

import com.app.travelboard.storage.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
