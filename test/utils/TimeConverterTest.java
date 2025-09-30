/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author adrest18
 */
public class TimeConverterTest {
    
    public TimeConverterTest(){
    }

    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test(expected = NullPointerException.class)
    public void T00_ConvertDurationToLocalTime_Throws_NullPointerException_If_Duration_isNull() {
        //Arrange
        Duration duration = null;
        
        //Act
        //Assert
        DurationConverter.convertDurationToLocalTime(duration);
    }
    
    @Test
    public void T01_ConvertDurationToLocalTime_Duration_0h0m0s_Returns_0h0m0s() {
        //Arrange
        LocalTime startTime = LocalTime.MIN;
        LocalTime endTime = LocalTime.MIN;
        
        Duration duration = Duration.between(startTime, endTime);
        
        //Act
        LocalTime result = DurationConverter.convertDurationToLocalTime(duration);
        
        //Assert
        Assert.assertEquals(endTime, result);
    }

    @Test
    public void T02_ConvertDurationToLocalTime_Duration_23h59m59s_Returns_23h59m59s() {
        //Arrange
        LocalTime startTime = LocalTime.MIN;
        LocalTime endTime = startTime.plusHours(23).plusMinutes(59).plusSeconds(59);
        
        Duration duration = Duration.between(startTime, endTime);
        
        //Act
        LocalTime result = DurationConverter.convertDurationToLocalTime(duration);
        
        //Assert
        Assert.assertEquals(endTime, result);
    }
    
    @Test(expected = DateTimeException.class)
    public void T03_ConvertDurationToLocalTime_Duration_24h00m01s_Throws_DateTimeException() {
        //Arrange
        LocalDateTime oldDate = LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0, 0);
        LocalDateTime newDate = LocalDateTime.of(2024, Month.JANUARY, 2, 0, 0, 1);
        
        Duration duration = Duration.between(oldDate, newDate);
        
        //Act
        //Assert
        DurationConverter.convertDurationToLocalTime(duration);        
    }

}
