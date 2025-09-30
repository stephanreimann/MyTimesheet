/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.time.DayOfWeek;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import java.time.LocalDate;
import javafx.collections.ObservableList;
import model.Holyday;

/**
 *
 * @author adrest18
 */
public class DateChecker {

    public static Boolean isHolyday(ObservableList<Holyday> holydayData, LocalDate date) {
        for(Holyday holyday : holydayData) {
            if(holyday.getDate().equals(date)) {
                return true;
            }
        }
        return false;
    }
    
    public static Boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        switch (dayOfWeek) {
            case SATURDAY, SUNDAY -> {
                return true;
            }
        }
        return false;
    }
    
}
