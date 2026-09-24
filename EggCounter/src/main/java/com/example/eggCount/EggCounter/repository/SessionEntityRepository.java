package com.example.eggCount.EggCounter.repository;

import com.example.eggCount.EggCounter.entity.SessionEntity;
import com.example.eggCount.EggCounter.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SessionEntityRepository extends JpaRepository<SessionEntity, Long> {
    List<SessionEntity> findByUser(UserEntity user);

    Optional<SessionEntity> findByRefreshToken(String refreshToken);

    void deleteByRefreshToken(String refreshToken);
}