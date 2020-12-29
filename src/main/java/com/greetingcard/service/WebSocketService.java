package com.greetingcard.service;

import com.greetingcard.entity.User;
import com.greetingcard.entity.UserInfo;

import java.util.List;

public interface WebSocketService {

    void notifyAboutCardStatusChanging(long cardId, String statusName, User userLoggedIn);

    void notifyAboutAddingToCard(String message, String login);

    void notifyAdmin(String message, long cardId);

    void notifyAllCardMembersAboutDeletingCongratulation(long congratulationId, User userLoggedIn);

    void notifyAllDeletedCardMembers(List<UserInfo> listUsers, long cardId);
}
