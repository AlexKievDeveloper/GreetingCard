package com.greetingcard.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class Congratulation {
    private long id;
    private String message;
    private Card card;
    private User user;
    private Status status;
    @EqualsAndHashCode.Exclude
    private List<Link> linkList;
}
