package com.mmc.auth.infrastructure.security;

import com.mmc.auth.domain.entity.UserDomain;
import com.mmc.auth.domain.repository.UserRepository;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsServiceImpl( UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @NullMarked
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDomain ue = userRepository.loadUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with login %s not found", username)));

        return new CustomUserPrincipal(ue);
    }
}
