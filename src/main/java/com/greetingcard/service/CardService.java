package com.greetingcard.service;

import com.greetingcard.entity.Card;
import com.greetingcard.entity.Role;

import java.util.Map;

public interface CardService {
    Map<Card, Role> getAllCardsByUserId(int id);
}
