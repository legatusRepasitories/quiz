package com.gmail.quiz.util;

public class Util {

    private static long globalId = 100_000;

    public static long generateId() {
        return globalId++;
    }

}
