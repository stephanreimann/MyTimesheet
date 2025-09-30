/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.time.Duration;
import java.time.LocalTime;

/**
 *
 * @author adrest18
 */
public class DurationConverter {
    
    public static LocalTime convertDurationToLocalTime(Duration duration) {
        if(duration == null) throw new NullPointerException("duration");
            
        long hours   = duration.toHours();
        long minutes = duration.minusHours(hours).toMinutes();
        long seconds = duration.minusHours(hours).minusMinutes(minutes).toSeconds();
            
        return LocalTime.of((int)hours, (int)minutes, (int)seconds);
    }

    public static String convertDurationToSignedStringOfHoursAndMinutes(Duration duration) {
        if(duration == null) throw new NullPointerException("duration");
        
        String hours =  String.format("%02d", Math.abs(duration.toHoursPart()));
        String minutes = String.format("%02d", Math.abs(duration.toMinutesPart()));
        
        if(duration.isNegative() && !duration.equals(Duration.ofHours(-24))) {
            return "-" + hours + ":" + minutes;
        }
        return hours + ":" + minutes;
    }

    public static String convertDurationStringToSignedStringOfHoursAndMinutes(String durationString) {
        if(durationString == null) throw new NullPointerException("duration");

        Duration duration = Duration.parse(durationString);

        long totalMinutes = duration.toMinutes();
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;

        String formatted = String.format("%02d:%02d", hours, minutes);
        return formatted;
    }
    
    public static Duration  convertSignedStringOfHoursAndMinutesToDuration(String signedHoursAndMinutes) {
        if(signedHoursAndMinutes.isEmpty() || signedHoursAndMinutes.isBlank()) {
            return Duration.ZERO;
        }

        boolean isNegative = false;
        
        if(signedHoursAndMinutes.charAt(0) == '-') {
            isNegative = true;
            signedHoursAndMinutes = signedHoursAndMinutes.replace("-", "");
        }
        
        String[] tokens = signedHoursAndMinutes.split(":");
        long hours = getLongField(tokens, 0);
        long minutes = getLongField(tokens, 1);
        long durationAsMinutes = hours * 60 + minutes;
        
        if(isNegative) {
            return Duration.ofMinutes(durationAsMinutes).negated();
        }
        return Duration.ofMinutes(durationAsMinutes);
    }
    
    private static long getLongField(String[] tokens, int index) {
        if(tokens.length <= index || tokens[index].isEmpty()) {
            return 0;
        }
        return Long.parseLong(tokens[index]);
    }

}
