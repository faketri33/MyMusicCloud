package com.mmc.auth.api.controller;

import com.mmc.auth.api.dto.request.AuthRequestDto;
import com.mmc.auth.config.WebSecurityConfiguration;
import com.mmc.auth.domain.entity.JwtState;
import com.mmc.auth.domain.exceptions.InvalidUserCredentials;
import com.mmc.auth.infrastructure.security.CustomUserDetailsServiceImpl;
import com.mmc.auth.service.AuthService;
import com.mmc.auth.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
@Import(WebSecurityConfiguration.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsServiceImpl customUserDetailsService;

    private AuthRequestDto validRequest;
    private JwtState jwtState;

    @Value("${cors.allowedOrigins}")
    private String allowedOrigins;

    @BeforeEach
    void setUp() {
        validRequest = new AuthRequestDto("user@test.com", "password123");
        jwtState = new JwtState("access.token.here", "refresh.token.here");
    }

    @Test
    void login() throws Exception {
        when(authService.login(any(AuthRequestDto.class))).thenReturn(jwtState);

        mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest))
                        .header("Origin", allowedOrigins))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access").value(jwtState.access()))
                .andExpect(jsonPath("$.refresh").value(jwtState.refresh()));
    }

    @Test
    void login_invalidCredentials_returns401() throws Exception {
        when(authService.login(any(AuthRequestDto.class)))
                .thenThrow(new InvalidUserCredentials("Invalid login or password"));

        mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest))
                        .header("Origin", allowedOrigins))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_emptyBody_returns400() throws Exception {
        mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .header("Origin", allowedOrigins))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_serviceThrowsUnexpectedException_returns500() throws Exception {
        when(authService.login(any(AuthRequestDto.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest))
                        .header("Origin", allowedOrigins))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void login_callsAuthServiceWithCorrectArgs() throws Exception {
        when(authService.login(any(AuthRequestDto.class))).thenReturn(jwtState);

        mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest))
                        .header("Origin", allowedOrigins))
                .andExpect(status().isOk());

        verify(authService, times(1)).login(
                argThat(req ->
                        req.login().equals(validRequest.login()) &&
                                req.password().equals(validRequest.password())
                )
        );
    }
}