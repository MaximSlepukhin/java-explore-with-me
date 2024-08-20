package ru.practicum.Util;

import java.time.format.DateTimeFormatter;

public class DateFormatter {

    public static final String pattern = "yyyy-MM-dd HH:mm:ss";

    public static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
}
