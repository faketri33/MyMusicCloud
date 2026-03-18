package com.mmc.auth.service.impl;

import com.mmc.auth.api.dto.request.AuthRequestDto;
import com.mmc.auth.domain.entity.JwtState;
import com.mmc.auth.domain.entity.UserDomain;
import com.mmc.auth.domain.exceptions.InvalidUserCredentials;
import com.mmc.auth.domain.exceptions.UsernameAlreadyExists;
import com.mmc.auth.domain.repository.UserRepository;
import com.mmc.auth.infrastructure.persistence.entity.ERoles;
import com.mmc.auth.infrastructure.security.CustomUserPrincipal;
import com.mmc.auth.service.AuthService;
import com.mmc.auth.service.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public JwtState login(AuthRequestDto ard) {
        Object principal;
        try {
            principal = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(ard.login(), ard.password())
            ).getPrincipal();
        } catch (BadCredentialsException ex) {
            throw new InvalidUserCredentials("Invalid password or login");
        }

        if (!(principal instanceof CustomUserPrincipal up)) {
            assert principal != null;
            throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
        }

        return jwtService.generateJwt(up);
    }

    @Override
    public JwtState register(AuthRequestDto ard) {

        if (Boolean.TRUE.equals(userRepository.usernameAlreadyExist(ard.login())))
            throw new UsernameAlreadyExists("This username already used, please choose another one");

        UserDomain userDomain = new UserDomain();

        userDomain.activate();
        userDomain.setUsername(ard.login());
        userDomain.setPassword(passwordEncoder.encode(ard.password()));

        userDomain.addRole(ERoles.DEFAULT);

        userDomain = userRepository.save(userDomain);

        return jwtService.generateJwt(new CustomUserPrincipal(userDomain));
    }

    @Override
    public Map<String, Object> publicKeys() {
        return jwtService.publicKeys();
    }
}
