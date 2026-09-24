package com.example.eggCount.EggCounter.repository;

import com.example.eggCount.EggCounter.entity.EggEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EggEntityRepository extends JpaRepository<EggEntity, Long> {

    List<EggEntity> findAllByOrderByCreatedAtDesc();

    Optional<EggEntity> findByUserEntityId(Long userId);
}
