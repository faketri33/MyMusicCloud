package com.mmc.auth.api.controller;


import com.mmc.auth.api.dto.request.AuthRequestDto;
import com.mmc.auth.domain.entity.JwtState;
import com.mmc.auth.domain.exceptions.InvalidUserCredentials;
import com.mmc.auth.domain.exceptions.UsernameAlreadyExists;
import com.mmc.auth.domain.repository.UserRepository;
import com.mmc.auth.service.AuthService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


@Transactional
class authServiceContainerIT extends IntegrationTestBase {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void register_newUser_savesToDatabaseAndReturnsJwt() {
        AuthRequestDto request = new AuthRequestDto("newuser", "password123");

        JwtState result = authService.register(request);

        assertThat(result.access()).isNotBlank();
        assertThat(result.refresh()).isNotBlank();

        assertThat(jwtService.validate(result.access())).isTrue();

        assertThat(userRepository.loadUserByUsername("newuser")).isPresent();
    }

    @Test
    void register_duplicateUsername_throwsException() {
        AuthRequestDto request = new AuthRequestDto("existinguser", "password123");
        authService.register(request);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(UsernameAlreadyExists.class);
    }

    @Test
    void register_passwordIsHashed_notStoredAsPlainText() {
        AuthRequestDto request = new AuthRequestDto("hashuser", "plainpassword");
        authService.register(request);

        var savedUser = userRepository.loadUserByUsername("hashuser");

        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getPassword()).isNotEqualTo("plainpassword");
        assertThat(savedUser.get().getPassword()).startsWith("$2a$");
    }

    @Test
    void register_newUser_hasDefaultRole() {
        AuthRequestDto request = new AuthRequestDto("roleuser", "password123");
        authService.register(request);

        var savedUser = userRepository.loadUserByUsername("roleuser");

        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getRoles()).isNotNull();
    }

    // ─── LOGIN ────────────────────────────────────────────────────────────────

    @Test
    void login_existingUser_returnsValidJwt() {
        AuthRequestDto request = new AuthRequestDto("loginuser", "password123");
        authService.register(request);

        JwtState result = authService.login(request);

        assertThat(result.access()).isNotBlank();
        assertThat(result.refresh()).isNotBlank();
        assertThat(jwtService.validate(result.access())).isTrue();
    }

    @Test
    void login_wrongPassword_throwsException() {
        authService.register(new AuthRequestDto("user", "correctpassword"));

        assertThatThrownBy(() ->
                authService.login(new AuthRequestDto("user", "wrongpassword"))
        ).isInstanceOf(InvalidUserCredentials.class);
    }

    @Test
    void login_nonExistentUser_throwsException() {
        assertThatThrownBy(() ->
                authService.login(new AuthRequestDto("ghost", "password"))
        ).isInstanceOf(InvalidUserCredentials.class);
    }

    @Test
    void login_returnsJwtWithCorrectSubject() {
        AuthRequestDto request = new AuthRequestDto("subjectuser", "password123");
        authService.register(request);

        JwtState result = authService.login(request);

        UUID subject = jwtService.extractUserId(result.access());
        var savedUser = userRepository.loadUserByUsername("subjectuser");

        assertThat(savedUser).isPresent();
        assertThat(subject).isEqualTo(savedUser.get().getId());
    }

    // ─── JWT ──────────────────────────────────────────────────────────────────

    @Test
    void register_accessAndRefreshTokensAreDifferent() {
        AuthRequestDto request = new AuthRequestDto("tokenuser", "password123");

        JwtState result = authService.register(request);

        assertThat(result.access()).isNotEqualTo(result.refresh());
    }

    @Test
    void register_twoUsers_receivesDifferentTokens() {
        JwtState first = authService.register(new AuthRequestDto("user1", "password123"));
        JwtState second = authService.register(new AuthRequestDto("user2", "password123"));

        assertThat(first.access()).isNotEqualTo(second.access());
        assertThat(first.refresh()).isNotEqualTo(second.refresh());
    }
}
