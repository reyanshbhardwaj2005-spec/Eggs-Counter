package com.example.eggCount.EggCounter.service;

import com.example.eggCount.EggCounter.dto.LoginAndSignupResponse;
import com.example.eggCount.EggCounter.dto.LoginRequest;
import com.example.eggCount.EggCounter.dto.SignupRequest;
import com.example.eggCount.EggCounter.entity.UserEntity;
import com.example.eggCount.EggCounter.repository.SessionEntityRepository;
import com.example.eggCount.EggCounter.repository.UserEntityRepository;
import com.example.eggCount.EggCounter.exception.DuplicateUserException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserEntityRepository userEntityRepository;
    private final SessionEntityRepository sessionEntityRepository;
    private final JwtService jwtService;
    private final SessionService service;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public LoginAndSignupResponse signup(SignupRequest signupRequest) {
        Optional<UserEntity> user = userEntityRepository.findByUsername(signupRequest.getUsername());

        if(user.isPresent()) {
            throw new DuplicateUserException("User with this username is already present.");
        } else {

            UserEntity userToBeSaved = modelMapper.map(signupRequest, UserEntity.class);
            userToBeSaved.setPassword(passwordEncoder.encode(userToBeSaved.getPassword()));

            UserEntity savedUser = userEntityRepository.save(userToBeSaved);

            String accessToken = jwtService.generateAccessToken(savedUser);
            String refreshToken =jwtService.generateRefreshToken(savedUser);

            service.generateNewSession(savedUser, refreshToken);
            return new LoginAndSignupResponse(savedUser.getId(), accessToken, refreshToken);
        }
    }

    public LoginAndSignupResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        UserEntity user = (UserEntity) authentication.getPrincipal();
        String access_token = jwtService.generateAccessToken(user);
        String refresh_token = jwtService.generateRefreshToken(user);

        service.generateNewSession(user, refresh_token);
        return new LoginAndSignupResponse(user.getId(), access_token, refresh_token);
    }

    public @Nullable LoginAndSignupResponse refreshToken(String refreshToken) {
        service.validateSession(refreshToken);
        String tokenType = jwtService.getTokenType(refreshToken);

        if (!"refresh".equals(tokenType)) {
            throw new RuntimeException("Invalid refresh token");
        }

        Long userId = jwtService.getIdFromToken(refreshToken);
        UserEntity userEntity = userService.getUserById(userId);
        String accessToken = jwtService.generateAccessToken(userEntity);
        return new LoginAndSignupResponse(userEntity.getId(), accessToken, refreshToken);
    }

    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }
        sessionEntityRepository.deleteByRefreshToken(refreshToken);
    }
}
