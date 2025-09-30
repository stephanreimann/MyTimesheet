/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import java.time.LocalDate;
import java.time.format.*;

/**
 *
 * @author ADREST18
 */
public class DateFormatService {

    public static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm.ss");

    public static String toString(LocalDate localDate) {
        if (localDate == null) {
            return "";
        } else {
            return dateFormat.format(localDate);
        }
    }

    public static LocalDate fromString(String string) {
        try {
            return LocalDate.parse(string, dateFormat);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static void setFormat(String pattern) {
        dateFormat = DateTimeFormatter.ofPattern(pattern);
    }

}
