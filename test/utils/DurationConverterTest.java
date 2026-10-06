/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import org.junit.Test;

import java.time.Duration;
import java.time.LocalTime;

import static org.junit.Assert.*;

public class DurationConverterTest {

    // --- Tests for convertDurationToLocalTime(Duration) ---

    @Test
    public void testConvertDurationToLocalTime_StandardDuration_ReturnsLocalTime() {
        Duration duration = Duration.ofHours(5).plusMinutes(30).plusSeconds(45);
        LocalTime result = DurationConverter.convertDurationToLocalTime(duration);

        assertEquals(LocalTime.of(5, 30, 45), result);
    }

    @Test
    public void testConvertDurationToLocalTime_ZeroDuration_ReturnsMidnight() {
        LocalTime result = DurationConverter.convertDurationToLocalTime(Duration.ZERO);
        assertEquals(LocalTime.MIDNIGHT, result);
    }

    @Test(expected = NullPointerException.class)
    public void testConvertDurationToLocalTime_NullDuration_ThrowsNullPointerException() {
        DurationConverter.convertDurationToLocalTime(null);
    }

    // --- Tests for convertDurationToSignedStringOfHoursAndMinutes(Duration) ---

    @Test
    public void testConvertDurationToSignedString_PositiveDuration_ReturnsFormattedString() {
        Duration duration = Duration.ofHours(8).plusMinutes(15);
        String result = DurationConverter.convertDurationToSignedStringOfHoursAndMinutes(duration);

        assertEquals("08:15", result);
    }

    @Test
    public void testConvertDurationToSignedString_NegativeDuration_ReturnsNegativeFormattedString() {
        Duration duration = Duration.ofHours(-3).minusMinutes(5);
        String result = DurationConverter.convertDurationToSignedStringOfHoursAndMinutes(duration);

        assertEquals("-03:05", result);
    }

    @Test
    public void testConvertDurationToSignedString_ZeroDuration_ReturnsZeroString() {
        String result = DurationConverter.convertDurationToSignedStringOfHoursAndMinutes(Duration.ZERO);
        assertEquals("00:00", result);
    }

    @Test(expected = NullPointerException.class)
    public void testConvertDurationToSignedString_NullDuration_ThrowsNullPointerException() {
        DurationConverter.convertDurationToSignedStringOfHoursAndMinutes(null);
    }

    // --- Tests for convertDurationStringToSignedStringOfHoursAndMinutes(String) ---

    @Test
    public void testConvertDurationStringToSignedString_ValidISOString_ReturnsFormattedString() {
        // PT2H30M represents 2 hours and 30 minutes
        String result = DurationConverter.convertDurationStringToSignedStringOfHoursAndMinutes("PT2H30M");
        assertEquals("02:30", result);
    }

    @Test(expected = NullPointerException.class)
    public void testConvertDurationStringToSignedString_NullString_ThrowsNullPointerException() {
        DurationConverter.convertDurationStringToSignedStringOfHoursAndMinutes(null);
    }

    // --- Tests for convertSignedStringOfHoursAndMinutesToDuration(String) ---

    @Test
    public void testConvertSignedStringToDuration_PositiveTime_ReturnsDuration() {
        Duration result = DurationConverter.convertSignedStringOfHoursAndMinutesToDuration("07:45");
        assertEquals(Duration.ofHours(7).plusMinutes(45), result);
    }

    @Test
    public void testConvertSignedStringToDuration_NegativeTime_ReturnsNegativeDuration() {
        Duration result = DurationConverter.convertSignedStringOfHoursAndMinutesToDuration("-01:30");
        assertEquals(Duration.ofHours(-1).minusMinutes(30), result);
    }

    @Test
    public void testConvertSignedStringToDuration_EmptyOrBlankString_ReturnsZeroDuration() {
        assertEquals(Duration.ZERO, DurationConverter.convertSignedStringOfHoursAndMinutesToDuration(""));
        assertEquals(Duration.ZERO, DurationConverter.convertSignedStringOfHoursAndMinutesToDuration("   "));
    }

    @Test
    public void testConvertSignedStringToDuration_PartialTokens_ParsesCorrectly() {
        // Only hours provided
        Duration resultHoursOnly = DurationConverter.convertSignedStringOfHoursAndMinutesToDuration("5:");
        assertEquals(Duration.ofHours(5), resultHoursOnly);

        // Only minutes provided (or missing hours token)
        Duration resultMinutesOnly = DurationConverter.convertSignedStringOfHoursAndMinutesToDuration(":15");
        assertEquals(Duration.ofMinutes(15), resultMinutesOnly);
    }
}