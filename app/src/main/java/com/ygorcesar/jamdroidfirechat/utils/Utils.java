package com.ygorcesar.jamdroidfirechat.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Utils {

    /**
     * Encoda o email pois os paths do Firebase não podem conter: '.', '#', '$', '[', ou ']'
     * @param userEmail
     * @return
     */
    public static String encodeEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    /**
     * Decoda o email
     * @param userEmail
     * @return
     */
    public static String decodeEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }

    /**
     * Transformando o timestamp do servidor para TimeZone padrão do Aparelho
     * @param time
     * @return
     */
    public static String timestampToHour(Object time) {
        Timestamp stamp = new Timestamp((long) time);
        Date date = new Date(stamp.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("H:mm", new Locale("pt", "BR"));
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }
}
