package com.greetingcard.entity;

public enum CardsType {
    ALL("all"), MY("my"), OTHER("other");

    private final String type;

    CardsType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
