package com.example.eggCount.EggCounter.controller;

import com.example.eggCount.EggCounter.dto.LoginAndSignupResponse;
import com.example.eggCount.EggCounter.dto.LoginRequest;
import com.example.eggCount.EggCounter.dto.RefreshTokenRequest;
import com.example.eggCount.EggCounter.dto.SignupRequest;
import com.example.eggCount.EggCounter.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.Cookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<LoginAndSignupResponse> signup(@RequestBody SignupRequest signupRequest, HttpServletRequest request, HttpServletResponse response) {

        LoginAndSignupResponse signupResponse = authService.signup(signupRequest);

        Cookie cookie = new Cookie("refreshToken", signupResponse.getRefreshToken());
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        return ResponseEntity.ok(signupResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginAndSignupResponse> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {

        LoginAndSignupResponse loginResponse = authService.login(loginRequest);

        Cookie cookie = new Cookie("refreshToken", loginResponse.getRefreshToken());
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginAndSignupResponse> refresh(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok("Logged out successfully");
    }
}
