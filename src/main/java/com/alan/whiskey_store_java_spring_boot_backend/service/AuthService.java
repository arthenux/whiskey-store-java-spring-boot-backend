package com.alan.whiskey_store_java_spring_boot_backend.service;

import com.alan.whiskey_store_java_spring_boot_backend.dto.AuthResponse;
import com.alan.whiskey_store_java_spring_boot_backend.dto.LoginRequest;
import com.alan.whiskey_store_java_spring_boot_backend.dto.RegisterRequest;
import com.alan.whiskey_store_java_spring_boot_backend.dto.UserResponse;
import com.alan.whiskey_store_java_spring_boot_backend.entity.AuthToken;
import com.alan.whiskey_store_java_spring_boot_backend.entity.UserAccount;
import com.alan.whiskey_store_java_spring_boot_backend.entity.enums.Role;
import com.alan.whiskey_store_java_spring_boot_backend.repository.AuthTokenRepository;
import com.alan.whiskey_store_java_spring_boot_backend.repository.UserAccountRepository;
import com.alan.whiskey_store_java_spring_boot_backend.security.AuthenticatedUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
public class AuthService {

    private static final Duration TOKEN_LIFETIME = Duration.ofDays(30);

    private final UserAccountRepository userAccountRepository;
    private final AuthTokenRepository authTokenRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserAccountRepository userAccountRepository,
            AuthTokenRepository authTokenRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userAccountRepository = userAccountRepository;
        this.authTokenRepository = authTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (userAccountRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new ResponseStatusException(BAD_REQUEST, "An account with this email already exists.");
        }
        UserAccount user = new UserAccount();
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        userAccountRepository.save(user);
        return issueToken(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        UserAccount user = userAccountRepository.findByEmailIgnoreCase(request.email().trim())
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Invalid email or password."));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(UNAUTHORIZED, "Invalid email or password.");
        }
        return issueToken(user);
    }

    public UserResponse me(AuthenticatedUser authenticatedUser) {
        UserAccount user = userAccountRepository.findById(authenticatedUser.id())
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "User account not found."));
        return mapUser(user);
    }

    @Transactional
    public void logout(AuthenticatedUser authenticatedUser) {
        UserAccount user = userAccountRepository.findById(authenticatedUser.id())
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "User account not found."));
        revokeTokens(user);
    }

    @Transactional
    public AuthResponse issueToken(UserAccount user) {
        revokeTokens(user);
        AuthToken token = new AuthToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID() + "-" + UUID.randomUUID());
        token.setExpiresAt(Instant.now().plus(TOKEN_LIFETIME));
        token.setRevoked(false);
        authTokenRepository.save(token);
        return new AuthResponse(token.getToken(), token.getExpiresAt(), mapUser(user));
    }

    private void revokeTokens(UserAccount user) {
        List<AuthToken> activeTokens = authTokenRepository.findByUserAndRevokedFalse(user);
        for (AuthToken activeToken : activeTokens) {
            activeToken.setRevoked(true);
        }
        authTokenRepository.saveAll(activeTokens);
    }

    public UserResponse mapUser(UserAccount user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
