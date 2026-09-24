package com.example.eggCount.EggCounter.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginAndSignupResponse {

    private Long id;
    private String refreshToken;
    private String name;
    private String accessToken;

}
