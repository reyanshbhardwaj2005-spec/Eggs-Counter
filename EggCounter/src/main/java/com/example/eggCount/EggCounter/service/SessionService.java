package com.example.eggCount.EggCounter.service;

import com.example.eggCount.EggCounter.entity.SessionEntity;
import com.example.eggCount.EggCounter.entity.UserEntity;
import com.example.eggCount.EggCounter.repository.SessionEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionEntityRepository sessionEntityRepository;
    private final int SESSION_LIMIT = 1;

    public void generateNewSession(UserEntity user, String refresh_token) {

        List<SessionEntity> userSession = sessionEntityRepository.findByUser(user);

        if (userSession.size() == SESSION_LIMIT) {
            userSession.sort(Comparator.comparing(SessionEntity::getLastUsed));

            SessionEntity leastResentlyUsedSession = userSession.getFirst();
            sessionEntityRepository.delete(leastResentlyUsedSession);
        }

        SessionEntity newSession = SessionEntity.builder()
                .user(user)
                .refreshToken(refresh_token)
                .build();
        sessionEntityRepository.save(newSession);
    }

    public void validateSession(String refresh_token) {
        SessionEntity session = (SessionEntity) sessionEntityRepository.findByRefreshToken(refresh_token)
                .orElseThrow(() -> new SessionAuthenticationException("No session found with this refresh token"+refresh_token));
        session.setLastUsed(LocalDateTime.now());
        sessionEntityRepository.save(session);
    }
}
