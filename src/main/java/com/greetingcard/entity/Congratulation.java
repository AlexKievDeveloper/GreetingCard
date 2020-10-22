package com.greetingcard.entity;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Congratulation {
    private int id;
    private String message;
    private int cardId;
    private int userId;
    private Status status;
    private List<Link> linkList;
}
