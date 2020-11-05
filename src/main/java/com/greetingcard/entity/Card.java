package com.greetingcard.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class Card {
    private long id;
    @EqualsAndHashCode.Exclude
    private User user;
    private String name;
    private String backgroundImage;
    private String cardLink;
    private Status status;
    @EqualsAndHashCode.Exclude
    private List<Congratulation> congratulationList;
}


