package com.greetingcard.entity;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public enum LinkType {
    VIDEO("VIDEO", 1), PICTURE("PICTURE", 2, "img","image/jpeg", "image/jpg", "image/png"),
    AUDIO("AUDIO",  3, "audio","audio/mpeg")/*, PLAIN_LINK("PLAIN_LINK", 4)*/;

    private final String type;
    private final int number;
    private String pathToStorage;
    private List<String> additionalTypes = new ArrayList<>();


    LinkType(String type, int number) {
        this.type = type;
        this.number = number;
    }

    LinkType(String type, int number, String pathToStorage, String... types) {
        this.type = type;
        this.number = number;
        this.pathToStorage = pathToStorage;
        this.additionalTypes = Arrays.asList(types);
    }

    public static LinkType getByNumber(int number) {
        LinkType[] linkTypes = LinkType.values();
        for (LinkType linkType : linkTypes) {
            if (linkType.getTypeNumber() == number) {
                return linkType;
            }
        }
        log.error("No link for number: {}", number);
        throw new IllegalArgumentException("No link for number " + number);
    }

    public String getLinkType() {
        return type;
    }

    public int getTypeNumber() {
        return number;
    }

    public String getPathToStorage() {
        if (pathToStorage == null){
            throw new RuntimeException("This link type don`t have path to storage");
        }
        return pathToStorage;
    }

    public List<String> getAdditionalTypes() {
        return additionalTypes;
    }
}
