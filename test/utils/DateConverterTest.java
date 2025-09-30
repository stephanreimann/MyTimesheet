/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package utils;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.Locale;
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
public class DateConverterTest {
    
    public DateConverterTest() {
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
    public void T00_Format_Throws_NullPointerException_If_Date_isNull() {
        //Arrange
        LocalDate date = null;
        
        //Act
        //Assert
        DateConverter.format(date, DateTimeFormatter.ofPattern("dd.MM.yyyy"));        
    }
    
    @Test
    public void T01_Format_Returns_Formated_DateString_If_PassedArgumentIsADate() {
        //Arrange
        LocalDate date = LocalDate.of(2024, Month.JANUARY, 1);
        String expResult = "01.01.2024";
        
        //Act
        String result = DateConverter.format(date, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        
        //Assert
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void T02_Format_Returns_LocalizedAndFormated_DateString_For_German_Locale() {
        //Arrange
        LocalDate date = LocalDate.of(2024, Month.JANUARY, 1);
        FormatStyle style = FormatStyle.MEDIUM;
        Locale locale = Locale.GERMAN;
        
        String expResult = "01.01.2024";
        
        //Act
        String result = DateConverter.format(date, style, locale);
        
        //Assert
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void T02_Format_Returns_LocalizedAndFormated_DateString_For_English_Locale() {
        //Arrange
        LocalDate date = LocalDate.of(2024, Month.JANUARY, 1);
        FormatStyle style = FormatStyle.MEDIUM;
        Locale locale = Locale.ENGLISH;
        
        String expResult = "Jan 1, 2024";
        
        //Act
        String result = DateConverter.format(date, style, locale);
        
        //Assert
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void T10_Parse_Returns_LocalDate_If_PassedArgumentIsADate() {
        //Arrange
        String date = "01.01.2024";
        LocalDate expResult = LocalDate.of(2024, Month.JANUARY, 1);
        
        //Act
        LocalDate result = DateConverter.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        
        //Assert
        Assert.assertEquals(expResult, result);
    }

    @Test(expected = DateTimeParseException.class)
    public void T11_Parse_Returns_Null_If_PassedArgumentIsNotADate() {
        //Arrange
        String date = "32.13.2024";
        LocalDate expResult = null;
        
        //Act
        //Assert
        LocalDate result = DateConverter.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    @Test(expected = DateTimeParseException.class)
    public void T20_IsValidDateFormat_Returns_False_If_PassedArgumentIsNotADate() {
        //Arrange
        String date = "32.13.2024";
        Boolean expResult = false;
        
        //Act
        //Assert
        Boolean result = DateConverter.isValidDateFormat(date, DateTimeFormatter.ofPattern("dd.MM.yyyy"));        
    }

    @Test
    public void T21_IsValidDateFormat_Returns_True_If_PassedArgumentIsADate() {
        //Arrange
        String date = "01.01.2024";
        Boolean expResult = true;
        
        //Act
        Boolean result = DateConverter.isValidDateFormat(date, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        
        //Assert
        Assert.assertEquals(expResult, result);
    }

}
