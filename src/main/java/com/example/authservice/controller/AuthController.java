package com.example.authservice.controller;
import com.example.authservice.model.AuthResponse;
import com.example.authservice.model.User;
import com.example.authservice.model.UserRequest;
import com.example.authservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserRequest user) {
        String jwtToken = authService.login(user.getUsername(), user.getPassword());

        if (jwtToken != null) {
            Long userId = authService.getUserIdByUsername(user.getUsername());
            AuthResponse authResponse = new AuthResponse(jwtToken, userId);
            return new ResponseEntity<>(authResponse, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse("Invalid credentials", null));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRequest user) {
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(user.getPassword());
        authService.register(newUser);
        return ResponseEntity.ok("User registered successfully");
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam Long userId) {
        authService.logout(userId);
        return ResponseEntity.ok("User logged out");
    }

    @GetMapping("/dashboard")
    public ResponseEntity<String> dashboard(String username) {
        return ResponseEntity.ok("Hello " + username +". Welcome to the dashboard");
    }
}
