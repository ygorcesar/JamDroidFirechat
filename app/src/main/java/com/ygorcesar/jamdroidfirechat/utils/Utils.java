package com.ygorcesar.jamdroidfirechat.utils;

public class Utils {

    public static String encodeEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    public static String decodeEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }
}
