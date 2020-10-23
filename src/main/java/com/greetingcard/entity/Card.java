package com.greetingcard.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@Builder
public class Card {
    private int id;
    private String name;
    private String backgroundImage;
    private String cardLink;
    private Status status;
    @EqualsAndHashCode.Exclude
    private List<Congratulation> congratulationList;
}


