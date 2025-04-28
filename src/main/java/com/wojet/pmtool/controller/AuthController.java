package com.wojet.pmtool.controller;

import com.wojet.pmtool.model.User;
import com.wojet.pmtool.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        User user = userService.registerUser(email, password);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully!");
        response.put("userId", user.getId());
        return response;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        return userService.loginUser(email, password)
            .map(user -> {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Login successful!");
                response.put("token", "dummy-token-" + user.getId()); // Temporary token
                return response;
            })
            .orElseGet(() -> {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Invalid credentials");
                return error;
            });
    }
}
