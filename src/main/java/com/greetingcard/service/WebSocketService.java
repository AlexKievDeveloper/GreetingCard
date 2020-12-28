package com.greetingcard.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.greetingcard.entity.User;
import com.greetingcard.entity.UserInfo;

import java.util.List;

public interface WebSocketService {

    void notifyAboutCardStatusChanging(long cardId, String statusName, User userLoggedIn) throws JsonProcessingException;

    void notifyAboutGettingCards(String message, long userId) throws JsonProcessingException;

    void notifyAboutAddingToCard(String message, String login) throws JsonProcessingException;

    void notifyAdminAboutLeavingCard(String message, long cardId) throws JsonProcessingException;

    void notifyAdminAboutCreatingCongratulation(String message, long cardId) throws JsonProcessingException;

    void notifyAllCardMembersAboutDeletingCongratulation(long congratulationId, User userLoggedIn) throws JsonProcessingException;

    void notifyAllDeletedCardMembers(List<UserInfo> listUsers, long cardId) throws JsonProcessingException;
}
