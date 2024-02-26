package com.example.authservice.repository;

import com.example.authservice.model.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<SessionEntity, Long> {
    void deleteSessionByUserId(Long userId);
}
