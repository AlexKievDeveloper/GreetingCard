package com.greetingcard.entity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum Status {
    STARTUP("STARTUP", 1), ISOVER("ISOVER", 2);

    private final String name;
    private final int number;

    Status(String name, int number) {
        this.name = name;
        this.number = number;
    }

    public static Status getByNumber(int number) {
        Status[] statuses = Status.values();
        for (Status status : statuses) {
            if (status.getLanguageNumber() == number) {
                return status;
            }
        }
        log.error("No status for number: {}", number);
        throw new IllegalArgumentException("No status for number " + number);
    }

    public static Status getByName(String name) {
        Status[] statuses= Status.values();
        for (Status status : statuses) {
            if (name.equals(status.getName())) {
                return status;
            }
        }
        log.error("No status for name: {}", name);
        throw new IllegalArgumentException("No status for name " + name);
    }

    public int getLanguageNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Status{" +
                "name='" + name + '\'' +
                ", number=" + number +
                '}';
    }
}
