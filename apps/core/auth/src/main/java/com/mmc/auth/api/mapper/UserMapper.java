package com.mmc.auth.api.mapper;

import com.mmc.auth.domain.entity.UserDomain;
import com.mmc.auth.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity toEntity(UserDomain ud);
    UserDomain toDomain(UserEntity ue);


}
