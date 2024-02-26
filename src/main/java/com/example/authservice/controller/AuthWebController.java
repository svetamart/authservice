package com.example.authservice.controller;

import com.example.authservice.model.AuthResponse;
import com.example.authservice.model.User;
import com.example.authservice.model.UserRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;


@Controller
public class AuthWebController {

    private final RestTemplate restTemplate;

    @Autowired
    public AuthWebController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("userRequest", new UserRequest());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute UserRequest user, Model model, HttpServletRequest request) {
        String apiUrl = "http://localhost:8080/api/auth/login";

        ResponseEntity<AuthResponse> responseEntity = restTemplate.postForEntity(apiUrl, new HttpEntity<>(user), AuthResponse.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            AuthResponse authResponse = responseEntity.getBody();

            String token = authResponse.getToken();
            Long userId = authResponse.getUserId();

            request.getSession().setAttribute("Authorization", "Bearer " + token);
            request.getSession().setAttribute("userId", userId);

            return "redirect:/dashboard";

        } else if (responseEntity.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            model.addAttribute("error", "Invalid credentials. Please register.");
            return "register";

        } else {
            model.addAttribute("error", "Error during login. Please try again.");
            return "login";
        }
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("userRequest", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute UserRequest user, Model model) {
        String apiUrl = "http://localhost:8080/api/auth/register";

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiUrl, new HttpEntity<>(user), String.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return "redirect:/login";
        } else {
            model.addAttribute("error", "Error during registration. Please try again.");
            return "register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("Authorization");
        if (token != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", token);
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            String apiUrl = "http://localhost:8080/api/auth/dashboard";

            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);
            model.addAttribute("dashboardData", response.getBody());
            return "dashboard";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");

        if (userId != null) {
            String logoutApiUrl = "http://localhost:8080/api/auth/logout?userId=" + userId;
            restTemplate.getForEntity(logoutApiUrl, String.class);

            request.getSession().removeAttribute("Authorization");
            request.getSession().removeAttribute("userId");
            request.getSession().invalidate();
        }

        return "redirect:/login";
    }
}
