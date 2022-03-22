package com.klipvr.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.klipvr.backend.models.UserInfo;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserinfoRepository extends JpaRepository<UserInfo,Long>
{
    Optional<UserInfo> findByUsername(String username);

    List<UserInfo> findTop10ByOrderByIdDesc();
}
