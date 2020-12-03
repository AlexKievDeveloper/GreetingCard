package com.greetingcard.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Link {
    private int id;
    private String link;
    private int congratulationId;
    private LinkType type;
}
