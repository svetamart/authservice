package com.example.authservice.service;

import com.example.authservice.model.SessionEntity;
import com.example.authservice.repository.SessionRepository;
import com.example.authservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final JwtService jwtService;
    private final UserService userService;

    @Autowired
    public SessionService(SessionRepository sessionRepository, JwtService jwtService, UserService userService) {
        this.sessionRepository = sessionRepository;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    public String createSession(Long userId) {
        String jwtToken = jwtService.generateJwtToken(userId);
        User user = userService.findById(userId);
        SessionEntity session = new SessionEntity(jwtToken, new Date(), user);
        sessionRepository.save(session);
        return jwtToken;
    }

    public void deleteSessionByUserId(Long userId) {
        sessionRepository.deleteSessionByUserId(userId);
    }
}