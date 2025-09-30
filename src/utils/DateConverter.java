/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

/**
 *
 * @author adrest18
 */
public class DateConverter {
    
    public static String format(LocalDate date, DateTimeFormatter formatter) {
        if(date == null) throw new NullPointerException("date");
        if(formatter == null) throw new NullPointerException("formatter");
        
        return formatter.format(date);
    }
    
    public static String format(LocalDate date, FormatStyle style, Locale locale) {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(style).withLocale(locale);
        return formatter.format(date);
    }

    public static LocalDate parse(String dateString, DateTimeFormatter formatter) {
        if(dateString == null) throw new NullPointerException("date");
        if(formatter == null) throw new NullPointerException("formatter");

        return formatter.parse(dateString, LocalDate::from);
    }
    
    public static boolean isValidDateFormat(String dateString, DateTimeFormatter formatter) {
        if(dateString == null) throw new NullPointerException("date");
        if(formatter == null) throw new NullPointerException("formatter");

        return DateConverter.parse(dateString, formatter) != null;
    }
    
}
