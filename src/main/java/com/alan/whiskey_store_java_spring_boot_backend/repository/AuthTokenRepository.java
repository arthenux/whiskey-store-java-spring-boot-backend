package com.alan.whiskey_store_java_spring_boot_backend.repository;

import com.alan.whiskey_store_java_spring_boot_backend.entity.AuthToken;
import com.alan.whiskey_store_java_spring_boot_backend.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {

    Optional<AuthToken> findByTokenAndRevokedFalse(String token);

    List<AuthToken> findByUserAndRevokedFalse(UserAccount user);
}
