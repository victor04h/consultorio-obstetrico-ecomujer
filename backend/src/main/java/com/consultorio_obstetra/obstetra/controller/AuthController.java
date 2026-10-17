package com.consultorio_obstetra.obstetra.controller;

import com.consultorio_obstetra.obstetra.dto.auth.LoginRequest;
import com.consultorio_obstetra.obstetra.dto.auth.LoginResponse;
import com.consultorio_obstetra.obstetra.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
