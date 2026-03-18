package com.mmc.auth.api.mapper;

import com.mmc.auth.domain.entity.UserDomain;
import com.mmc.auth.infrastructure.persistence.entity.UserEntity;

public class UserMapper {

    private UserMapper() {
    }

    public static UserEntity toEntity(UserDomain ud) {
        return new UserEntity(ud.getId(), ud.getUsername(), ud.getPassword(), ud.getRoles(), ud.isActive(), ud.getCreateAt(), ud.getUpdateAt());
    }

    public static UserDomain toDomain(UserEntity ue) {
        return new UserDomain(ue.getId(), ue.getUsername(), ue.getPassword(), ue.getRoles(), ue.getActive(), ue.getCreateAt(), ue.getUpdateAt());
    }
}
