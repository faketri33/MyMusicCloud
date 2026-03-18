package com.mmc.auth.api.controller;

import com.mmc.auth.service.AuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/v1/internal")
public class InternalController {

    private final AuthService authService;

    public InternalController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/.well-know")
    public Map<String, Object> publicKeys(){
        return authService.publicKeys();
    }
}
