/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.time.LocalTime;

/**
 *
 * @author adrest18
 */
public class TimeChecker {
    
    public static boolean isInBetween(LocalTime startTime, LocalTime endTime, LocalTime checkTime) {
        boolean isInBetween = false;
        if (endTime.isAfter(startTime)) {
          if (startTime.isBefore(checkTime) && endTime.isAfter(checkTime)) {
              isInBetween = true;
          }
        } else if (checkTime.isAfter(startTime) || checkTime.isBefore(endTime)) {
            isInBetween = true;
        }
        return isInBetween;
    }

}
