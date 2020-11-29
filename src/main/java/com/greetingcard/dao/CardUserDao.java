package com.greetingcard.dao;

import com.greetingcard.entity.Role;
import com.greetingcard.entity.UserInfo;

import java.util.List;
import java.util.Optional;

public interface CardUserDao {
    void addUserMember(long cardId, long userId);

    Optional<Role> getUserRole(long cardId, long userId);

    List<UserInfo> getUserMembersByCardId(long cardId);
}
