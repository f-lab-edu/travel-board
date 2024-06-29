package com.support.utils;

import java.util.regex.Pattern;

public class StringUtils {

    private static final Pattern ALPHABET_NUMBER = Pattern.compile("^[a-zA-Z0-9]*$");
    private static final Pattern ALPHABET_NUMBER_SPECIAL_CHARACTER = Pattern.compile("^[a-zA-Z0-9!@#$%^&*]*$");

    public static String checkIfNull(String value) {
        return value == null ? "" : value;
    }

    public static boolean isNotAlphabetAndNumber(String value) {
        return !ALPHABET_NUMBER.matcher(value).matches();
    }

    public static boolean isNotAlphabetAndNumberAndSpecialCharacter(String value) {
        return !ALPHABET_NUMBER_SPECIAL_CHARACTER.matcher(value).matches();
    }
}
