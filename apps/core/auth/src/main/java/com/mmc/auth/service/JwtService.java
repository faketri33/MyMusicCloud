package com.mmc.auth.service;

import com.mmc.auth.domain.entity.JwtState;
import com.mmc.auth.infrastructure.security.CustomUserPrincipal;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface JwtService {

    JwtState generateJwt(CustomUserPrincipal userPrincipal);

    Boolean validate(String token);

    UUID extractUserId(String token);
    List<String> extractUserRole(String token);

    Map<String, Object> publicKeys();
}
