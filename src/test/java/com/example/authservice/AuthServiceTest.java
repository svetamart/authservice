package com.example.authservice;


import com.example.authservice.model.SessionEntity;
import com.example.authservice.model.User;
import com.example.authservice.repository.SessionRepository;
import com.example.authservice.repository.UserRepository;
import com.example.authservice.service.JwtService;
import com.example.authservice.service.SessionService;
import com.example.authservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AuthServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private SessionService sessionService;
    @Mock
    private JwtService jwtService;
    @Mock
    BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUserRegistration() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("password");

        when(userRepository.findByUsername("testUser")).thenReturn(null);
        when(passwordEncoder.encode("password")).thenReturn("hashedPassword");

        userService.register(user);
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @Test
    public void testLogout() {
        Long id = 10L;
        sessionService.deleteSessionByUserId(id);
        Mockito.verify(sessionRepository, Mockito.times(1)).deleteSessionByUserId(id);
    }

    @Test
    public void testLoginCreatesNewSession() {
        String username = "testUser";
        String password = "password";
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername(username);
        user.setPassword(password);

        when(userService.findByUsername(username)).thenReturn(user);
        when(userService.isPasswordCorrect(password, user.getPassword())).thenReturn(true);
        when(jwtService.generateJwtToken(userId)).thenReturn("someToken");

        SessionEntity sessionEntity = new SessionEntity("someToken", new Date(), user);

        sessionService.createSession(userId);

        Mockito.verify(sessionRepository, Mockito.times(1)).save(sessionEntity);
    }

}
