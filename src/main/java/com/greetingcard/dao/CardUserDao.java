package com.greetingcard.dao;

import com.greetingcard.entity.Role;
import com.greetingcard.entity.UserInfo;
import com.greetingcard.entity.UserOrder;

import java.util.List;
import java.util.Optional;

public interface CardUserDao {
    void addUserMember(long cardId, long userId);

    void saveHash(long cardId, String hash);

    Optional<Role> getUserRole(long cardId, long userId);

    List<UserInfo> getUserMembersByCardId(long cardId);

    List<UserInfo> getUserMembersByCardIdForWebSocketNotification(long cardId);

    List<String> getCardHashesByCardId(long cardId);

    void deleteUserFromCard(long cardId, long userId);

    void deleteListUsers(long cardId, List<UserInfo> listUserIds);

    void changeUsersOrder(long cardId, List<UserOrder> usersOrder);
}
