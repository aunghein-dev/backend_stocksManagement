package com.aunghein.SpringTemplate.repository;

import com.aunghein.SpringTemplate.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepo extends JpaRepository<Session, Long> {
    Optional<Session> findByUserId(Long userId);
    Optional<Session> findBySessionIdAndUserId(Long sessionId, Long userId);
}
