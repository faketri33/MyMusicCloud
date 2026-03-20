package com.mmc.auth.api.controller;


import com.mmc.auth.api.dto.request.AuthRequestDto;
import com.mmc.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody AuthRequestDto ard, HttpServletResponse response) {
        var tokens = authService.login(ard);

        setCookie(tokens.refresh(), response);

        return ResponseEntity.ok(tokens.access());
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody AuthRequestDto ard, HttpServletResponse response) {
        var tokens = authService.register(ard);

        setCookie(tokens.refresh(), response);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(tokens.access());
    }

    private void setCookie(String token, HttpServletResponse response){
        var refreshCookie = ResponseCookie.from("refresh_token", token)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/v1/auth")
                .maxAge(Duration.ofDays(7))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }
}
