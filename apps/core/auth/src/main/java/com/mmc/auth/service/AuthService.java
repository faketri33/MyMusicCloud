package com.mmc.auth.service;

import com.mmc.auth.api.dto.request.AuthRequestDto;
import com.mmc.auth.domain.entity.JwtState;

import java.util.Map;

public interface AuthService {

    JwtState login(AuthRequestDto ard);
    JwtState register(AuthRequestDto ard);

    Map<String, Object> publicKeys();
}
