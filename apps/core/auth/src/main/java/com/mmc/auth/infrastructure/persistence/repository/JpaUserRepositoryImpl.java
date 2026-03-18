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

    public JpaUserRepositoryImpl(UserEntityRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<UserDomain> loadUserByUsername(String username) {
        return userRepository.findByUsername(username).map(UserMapper::toDomain);
    }

    @Override
    public Boolean usernameAlreadyExist(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public Optional<UserDomain> get(UUID id) {
        return userRepository.findById(id).map(UserMapper::toDomain);
    }

    @Override
    public UserDomain save(UserDomain user) {
        var ue = UserMapper.toEntity(user);
        ue = userRepository.save(ue);
        return UserMapper.toDomain(ue);
    }
}
