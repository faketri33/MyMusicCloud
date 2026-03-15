package com.mmc.auth.infrastructure.persistence.repository;

import com.mmc.auth.api.mapper.UserMapper;
import com.mmc.auth.domain.entity.UserDomain;
import com.mmc.auth.domain.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaUserRepositoryImpl implements UserRepository {

    private final UserEntityRepository userRepository;
    private final UserMapper mapper;

    public JpaUserRepositoryImpl(UserEntityRepository userRepository, UserMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<UserDomain> loadUserByUsername(String username) {
        return userRepository.findByUsername(username).map(mapper::toDomain);
    }

    @Override
    public Boolean usernameAlreadyExist(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public Optional<UserDomain> get(UUID id) {
        return userRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public UserDomain save(UserDomain user) {
        var ue = mapper.toEntity(user);
        ue = userRepository.save(ue);
        return mapper.toDomain(ue);
    }
}
