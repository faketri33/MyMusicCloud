package com.mmc.auth.api.controller;


import com.mmc.auth.api.dto.request.AuthRequestDto;
import com.mmc.auth.domain.entity.JwtState;
import com.mmc.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtState> login(@Valid @RequestBody AuthRequestDto ard) {
        return ResponseEntity.ok(authService.login(ard));
    }

    @PostMapping("/register")
    public ResponseEntity<JwtState> register(@Valid @RequestBody AuthRequestDto ard) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.register(ard));
    }
}
