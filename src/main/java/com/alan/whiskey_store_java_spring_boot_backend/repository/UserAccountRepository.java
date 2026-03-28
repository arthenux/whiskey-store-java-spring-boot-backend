package com.alan.whiskey_store_java_spring_boot_backend.repository;

import com.alan.whiskey_store_java_spring_boot_backend.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByEmailIgnoreCase(String email);
}
