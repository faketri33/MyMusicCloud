package com.mmc.auth.domain.repository;

import com.mmc.auth.domain.entity.UserDomain;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    Optional<UserDomain> loadUserByUsername(String username);

    Boolean usernameAlreadyExist(String username);

    Optional<UserDomain> get(UUID id);

    UserDomain save(UserDomain user);
}
