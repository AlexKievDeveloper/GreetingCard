package com.greetingcard.entity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum LinkType {
    VIDEO("VIDEO", 1), PICTURE("PICTURE", 2), AUDIO("AUDIO", 3), PLAIN_LINK("PLAIN_LINK", 4);

    private final String type;
    private final int number;

    LinkType(String type, int number) {
        this.type = type;
        this.number = number;
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
}
