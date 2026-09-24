package com.example.eggCount.EggCounter.controller;

import com.example.eggCount.EggCounter.dto.AddEgg;
import com.example.eggCount.EggCounter.dto.EggsInformation;
import com.example.eggCount.EggCounter.entity.UserEntity;
import com.example.eggCount.EggCounter.service.EggService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/eggs")
@RequiredArgsConstructor
public class EggController {

    private final EggService eggService;

    @PostMapping("/addEggs")
    public ResponseEntity<String> addEggs(@RequestBody AddEgg addEgg, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        return ResponseEntity.ok(eggService.addNewEggs(addEgg, user));
    }

    @GetMapping
    public ResponseEntity<List<EggsInformation>> eggsInformation() {
        return ResponseEntity.ok(eggService.getAllEggs());
    }

    @PutMapping("/updateEggs/{userId}")
    public ResponseEntity<EggsInformation> updateEggs(@PathVariable Long userId, @RequestBody AddEgg addEgg, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        return ResponseEntity.ok(eggService.updateEggs(userId, addEgg, user));
    }
}
