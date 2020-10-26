package com.greetingcard.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@Builder
public class Congratulation {
    private int id;
    private String message;
    private Card card;
    private User user;
    private Status status;
    @EqualsAndHashCode.Exclude
    private List<Link> linkList;
}
