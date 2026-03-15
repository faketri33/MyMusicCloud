package com.mmc.auth.domain.entity;


import com.mmc.auth.infrastructure.persistence.entity.ERoles;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UserDomain {
    private UUID id;

    private String username;
    private String password;

    private Set<ERoles> roles = new HashSet<>();

    private Boolean isActive = true;

    private Instant createAt;
    private Instant updateAt;

    public UserDomain(UUID id, String username, String password, Set<ERoles> roles, Boolean isActive, Instant createAt, Instant updateAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.isActive = isActive;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    public UserDomain() {    }

    public UUID getId(){
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean isActive() {
        return isActive;
    }

    public void activate(){
        isActive = true;
    }

    public void deactivate(){
        isActive = false;
    }

    public Instant getCreateAt() {
        return createAt;
    }


    public Instant getUpdateAt() {
        return updateAt;
    }

    public Set<ERoles> getRoles(){
        return roles;
    }

    public void addRole(ERoles role){
        this.roles.add(role);
    }
}
