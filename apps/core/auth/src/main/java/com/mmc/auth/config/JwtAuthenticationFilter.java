package com.mmc.auth.config;

import com.mmc.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String HEADER_NAME = "Authorization";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String header = request.getHeader(HEADER_NAME);

        if (header == null || ! header.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = header.substring(BEARER_PREFIX.length());

        if(Boolean.FALSE.equals(jwtService.validate(token))){
            filterChain.doFilter(request, response);
            return;
        }

        final UUID userID = jwtService.extractUserId(token);
        final List<String> roles = jwtService.extractUserRole(token);

        if (userID != null || SecurityContextHolder.getContext().getAuthentication() == null){
            List<GrantedAuthority> authorities = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

            var principal = new UsernamePasswordAuthenticationToken(userID, null, authorities);
            principal.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContext sc = SecurityContextHolder.createEmptyContext();
            sc.setAuthentication(principal);
            SecurityContextHolder.setContext(sc);
        }

        filterChain.doFilter(request, response);
    }
}
