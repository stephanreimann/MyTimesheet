/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

/**
 *
 * @author stephan
 */
public class TimeConverter {

    public static String hoursAndMinutesToString(long timeInHoursAndMinutes) {
        boolean isNegative = false;
        
        if(Long.signum(timeInHoursAndMinutes) == -1) {
            isNegative = true;
        }
        
        long hours = Math.abs(timeInHoursAndMinutes) / 60;
        long minutes = Math.abs(timeInHoursAndMinutes) % 60;
        
        String hoursAsString = String.format("%02d", hours);
        String minutesAsString = String.format("%02d", minutes);
        
        StringBuilder sb = new StringBuilder();
        if(isNegative == true) {
            sb.append("-");
        }
        sb.append(hoursAsString).append(":").append(minutesAsString);
        
        return sb.toString();
    }

    public static Long hoursAndMinutesToLong(String timeString) {
        boolean isNegative = false;
        
        if(timeString.charAt(0) == '-') {
            isNegative = true;
            timeString = timeString.replace("-", "");
        }
        
        String[] tokens = timeString.split(":");
        long hours = getLongField(tokens, 0);
        long minutes = getLongField(tokens, 1);
        
        if(isNegative) {
            return (hours * 60 + minutes) * -1;
        }
        return hours * 60 + minutes;
    }
  
    private static long getLongField(String[] tokens, int index) {
        if(tokens.length <= index || tokens[index].isEmpty()) {
            return 0;
        }
        return Long.parseLong(tokens[index]);
    }

}
