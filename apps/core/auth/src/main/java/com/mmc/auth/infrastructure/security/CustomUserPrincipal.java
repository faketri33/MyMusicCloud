package com.mmc.auth.infrastructure.security;

import com.mmc.auth.domain.entity.UserDomain;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

public class CustomUserPrincipal implements UserDetails {
    private final UserDomain userDomain;

    public CustomUserPrincipal(UserDomain userDomain) {
        this.userDomain = userDomain;
    }

    public UUID getId(){
        return userDomain.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userDomain.getRoles()
                .stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
                .toList();
    }

    @Override
    public @Nullable String getPassword() {
        return userDomain.getPassword();
    }

    @Override
    public String getUsername() {
        return userDomain.getUsername();
    }
}
