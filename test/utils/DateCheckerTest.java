/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.Test;

import java.time.LocalDate;
import model.Holyday;

import static org.junit.Assert.*;

public class DateCheckerTest {

    // --- Tests for isHolyday ---

    @Test
    public void testIsHolyday_WhenDateMatches_ReturnsTrue() {
        LocalDate holidayDate = LocalDate.of(2026, 12, 25);
        Holyday holiday = new Holyday(1L);
        holiday.setDate(holidayDate);

        ObservableList<Holyday> holidays = FXCollections.observableArrayList(holiday);

        assertTrue(DateChecker.isHolyday(holidays, holidayDate));
    }

    @Test
    public void testIsHolyday_WhenDateDoesNotMatch_ReturnsFalse() {
        LocalDate holidayDate = LocalDate.of(2026, 12, 25);
        LocalDate checkDate = LocalDate.of(2026, 12, 26);
        
        Holyday holiday = new Holyday(1L);
        holiday.setDate(holidayDate);

        ObservableList<Holyday> holidays = FXCollections.observableArrayList(holiday);

        assertFalse(DateChecker.isHolyday(holidays, checkDate));
    }

    @Test
    public void testIsHolyday_WhenListIsEmpty_ReturnsFalse() {
        ObservableList<Holyday> holidays = FXCollections.observableArrayList();
        LocalDate checkDate = LocalDate.of(2026, 1, 1);

        assertFalse(DateChecker.isHolyday(holidays, checkDate));
    }

    // --- Tests for isWeekend ---

    @Test
    public void testIsWeekend_Saturday_ReturnsTrue() {
        // October 10, 2026 is a Saturday
        LocalDate saturday = LocalDate.of(2026, 10, 10);
        assertTrue(DateChecker.isWeekend(saturday));
    }

    @Test
    public void testIsWeekend_Sunday_ReturnsTrue() {
        // October 11, 2026 is a Sunday
        LocalDate sunday = LocalDate.of(2026, 10, 11);
        assertTrue(DateChecker.isWeekend(sunday));
    }

    @Test
    public void testIsWeekend_Weekday_ReturnsFalse() {
        // October 6, 2026 is a Tuesday
        LocalDate tuesday = LocalDate.of(2026, 10, 6);
        assertFalse(DateChecker.isWeekend(tuesday));
    }
}