package com.greetingcard.entity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum Language {
    UKRAINE("UA", 1), ENGLISH("EN", 2);

    private final String name;
    private final int number;

    Language(String name, int number) {
        this.name = name;
        this.number = number;
    }

    public static Language getByNumber(int number) {
        Language[] languages = Language.values();
        for (Language language : languages) {
            if (language.getLanguageNumber() == number) {
                return language;
            }
        }
        log.error("No role for number: {}", number);
        throw new IllegalArgumentException("No role for number " + number);
    }

    public static Language getByName(String name) {
        Language[] languages = Language.values();
        for (Language language : languages) {
            if (name.equals(language.getName())) {
                return language;
            }
        }
        log.error("No role for name: {}", name);
        throw new IllegalArgumentException("No role for name " + name);
    }

    public int getLanguageNumber() {
        return number;
    }
    public String getName() {
        return name;
    }
    @Override
    public String toString() {
        return "Language{" +
                "language='" + name + '\'' +
                ", number=" + number +
                '}';
    }
}



