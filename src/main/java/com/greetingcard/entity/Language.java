package com.greetingcard.entity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum Language {
    UKRAINE("UKRAINE", 1), ENGLISH("ENGLISH", 2);

    public String getLanguage() {
        return language;
    }

    private final String language;
    private final int number;

    Language(String language, int number) {
        this.language = language;
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

    private int getLanguageNumber() {
        return number;
    }

    @Override
    public String toString() {
        return "Language{" +
                "language='" + language + '\'' +
                ", number=" + number +
                '}';
    }
}



