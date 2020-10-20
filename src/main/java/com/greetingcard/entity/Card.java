package com.greetingcard.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Card {
    private int id;
    private String name;
    private String backgroundImage;
    private String cardLink;
    private Status status;
}
