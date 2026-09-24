package com.example.eggCount.EggCounter.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EggsInformation {

    private Long userId;
    private String name;
    private Long eggCount;
    private LocalDateTime updatedAt;
}
