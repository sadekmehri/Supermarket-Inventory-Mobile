package com.example.caisse.utils;

public class StringUtils {

    /* Capitalize the first letter */
    public static String capitalize(String str) {
        if (str == null) return null;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}
