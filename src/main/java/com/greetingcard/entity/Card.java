package com.greetingcard.entity;

import lombok.*;

import java.time.LocalDate;
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
    private LocalDate dateOfFinish;
    @EqualsAndHashCode.Exclude
    private List<Congratulation> congratulationList;
}


