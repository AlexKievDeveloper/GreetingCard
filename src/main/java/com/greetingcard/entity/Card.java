package com.greetingcard.entity;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Card {
    private long id;
    @EqualsAndHashCode.Exclude
    private User user;
    private String name;
    private String backgroundImage;
    private String backgroundCongratulations;
    private String cardLink;
    private Status status;
    @EqualsAndHashCode.Exclude
    private List<Congratulation> congratulationList;
}


