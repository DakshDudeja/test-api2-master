package com.klipvr.backend.repository;

import com.klipvr.backend.models.RefreshToken;
import com.klipvr.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUserId(Long id);
    Optional<RefreshToken> findByToken(String token);
    int deleteByUser(User user);
}

