package com.greetingcard.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Link {
    private int id;
    private String link;
    private int congratulationId;
    private LinkType type;
}
