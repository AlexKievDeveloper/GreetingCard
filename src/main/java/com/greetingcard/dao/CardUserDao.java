package com.greetingcard.dao;

import com.greetingcard.entity.Role;

import java.util.Optional;

public interface CardUserDao {
    void addUserMember(long cardId, long userId);

    Optional<Role> getUserRole(long cardId, long userId);
}
