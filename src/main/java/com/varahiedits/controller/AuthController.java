package com.varahiedits.controller;

import com.varahiedits.dto.ApiResponse;
import com.varahiedits.dto.LoginRequest;
import com.varahiedits.model.AdminUser;
import com.varahiedits.repository.AdminUserRepository;
import com.varahiedits.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * POST /api/auth/login
     * Admin login – returns JWT token
     */
    @PostMapping("/login")
    public ApiResponse<Map<String, String>> login(@Valid @RequestBody LoginRequest request) {
        AdminUser admin = adminUserRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(admin.getUsername());
        log.info("Admin login: {}", admin.getUsername());

        return ApiResponse.success("Login successful", Map.of(
                "token", token,
                "username", admin.getUsername(),
                "role", admin.getRole()
        ));
    }
}
