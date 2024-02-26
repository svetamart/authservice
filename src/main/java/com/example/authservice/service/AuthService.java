package com.example.authservice.service;

import com.example.authservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserService userService;
    private final SessionService sessionService;

    @Autowired
    public AuthService(UserService userService, SessionService sessionService) {
        this.userService = userService;
        this.sessionService = sessionService;
    }

    public String login(String username, String password) {
        User user = userService.findByUsername(username);
        if (user != null && userService.isPasswordCorrect(password, user.getPassword())) {
            return sessionService.createSession(user.getId());
        }
        return null;
    }

    public void register(User user) {
        userService.register(user);
    }

    public void logout(Long userId) {
        sessionService.deleteSessionByUserId(userId);
    }

    public Long getUserIdByUsername(String username) {
        User user = userService.findByUsername(username);
        return (user != null) ? user.getId() : null;
    }
}
