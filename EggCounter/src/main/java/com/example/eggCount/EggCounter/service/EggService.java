package com.example.eggCount.EggCounter.service;

import com.example.eggCount.EggCounter.dto.AddEgg;
import com.example.eggCount.EggCounter.dto.EggsInformation;
import com.example.eggCount.EggCounter.entity.EggEntity;
import com.example.eggCount.EggCounter.entity.UserEntity;
import com.example.eggCount.EggCounter.repository.EggEntityRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EggService {

    private final EggEntityRepository eggEntityRepository;

    public @Nullable String addNewEggs(AddEgg addEgg, UserEntity user) {
        EggEntity egg = new EggEntity();
        egg.setNumberOfEggs(addEgg.getNumberOfEggs());
        egg.setCreatedAt(LocalDateTime.now());
        egg.setUserEntity(user);
        EggEntity savedEggs = eggEntityRepository.save(egg);
        return "Your eggs have been saved and updated successfully";
    }

    public @Nullable List<EggsInformation> getAllEggs() {
        return eggEntityRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public EggsInformation updateEggs(Long userId, AddEgg addEgg, UserEntity user) {

        if (!user.getId().equals(userId)) {
            throw new RuntimeException("You are not allowed to update another user's eggs");
        }

        EggEntity egg = eggEntityRepository.findByUserEntityId(userId)
                .orElseThrow(() -> new RuntimeException("Egg record not found for this user"));

        egg.setNumberOfEggs(addEgg.getNumberOfEggs());
        egg.setCreatedAt(LocalDateTime.now());
        EggEntity updatedEgg = eggEntityRepository.save(egg);
        return convertToDTO(updatedEgg);
    }

    private EggsInformation convertToDTO(EggEntity egg) {

        return new EggsInformation(
                egg.getId(),
                egg.getUserEntity().getName(),
                egg.getNumberOfEggs(),
                egg.getCreatedAt());

    }
}
