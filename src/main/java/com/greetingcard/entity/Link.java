package com.greetingcard.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Link {
    private int id;
    private String link;
    private int congratulationId;
    private LinkType type;
}
