package com.klipvr.backend.repository;

import java.util.Optional;

import com.klipvr.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
  Optional<User> findByUsername(String username);
User findByEmail(String email);
  Boolean existsByUsername(String username);
  //Optional<User> findByResetToken(String resetToken);
  Boolean existsByEmail(String email);

 // String getEmail();
}
