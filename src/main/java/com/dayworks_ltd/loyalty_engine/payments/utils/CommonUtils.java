package com.dayworks_ltd.loyalty_engine.payments.utils;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommonUtils {

    private static String dateTimeFormat = "yyyyMMddHHmmss";

    public static String formatedDateTime(){
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);
        return dateTime.format(formatter);
    }
}
