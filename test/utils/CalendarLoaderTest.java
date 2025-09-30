/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.collections.ObservableList;
import model.Holyday;
import net.fortuna.ical4j.data.ParserException;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Works for files of /www.feiertage-kalender.de
 * @author adrest18
 */
public class CalendarLoaderTest {

    public CalendarLoaderTest() {
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

    @Test
    public void T00_Ctor_Called_Initializes_Holydays() throws IOException, ParserException {
        //Arrange
        //Act
        CalendarLoader instance = new CalendarLoader();

        //Assert
        assertNotNull(instance.getHolydays());
        assertEquals(true, instance.getHolydays().isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void T10_LoadHolydays_Throws_IllegalArgumentException_If_State_IsNull() throws IOException, ParserException {
        //Arrange
        String state = null;
        CalendarLoader instance = new CalendarLoader();
        
        //Act
        //Assert
        instance.loadHolydays(System.getProperty("user.dir") + "/test/testdata/notexistingfile.ics", state);
    }

    @Test(expected = IllegalArgumentException.class)
    public void T11_LoadHolydays_Throws_IllegalArgumentException_If_State_IsEmpty() throws IOException, ParserException {
        //Arrange
        String state = "";
        CalendarLoader instance = new CalendarLoader();
        
        //Act
        //Assert
        instance.loadHolydays(System.getProperty("user.dir") + "/test/testdata/notexistingfile.ics", state);
    }

    @Test(expected = FileNotFoundException.class)
    public void T12_LoadHolydays_Throws_FileNotFoundException_If_Passed_File_DoesNotExists() throws IOException, ParserException {
        //Arrange
        String state = "Bayern";
        CalendarLoader instance = new CalendarLoader();
        
        //Act
        //Assert
        instance.loadHolydays(System.getProperty("user.dir") + "/test/testdata/notexistingfile.ics", state);
    }

    @Test(expected = FileNotFoundException.class)
    public void T13_LoadHolydays_Throws_FileNotFoundException_If_Passed_File_DoesNotHave_ExpectedFileExtension() throws IOException, ParserException {
        //Arrange
        String state = "Bayern";
        CalendarLoader instance = new CalendarLoader();

        //Act
        //Assert
        instance.loadHolydays(System.getProperty("user.dir") + "/test/testdata/Feiertage2024.abc", state);
    }

    @Test(expected = ParserException.class)
    public void T14_LoadHolydays_Throws_ParserException_If_Passed_File_HasNoValidFormat() throws IOException, ParserException {        //Arrange
        //Arrange
        String state = "Bayern";
        CalendarLoader instance = new CalendarLoader();

        //Act
        //Assert
        instance.loadHolydays(System.getProperty("user.dir") + "/test/testdata/FeiertageInvalidFormat2024.ics", state);
    }

    @Test
    public void T20_GetHolydays_Returns_Loaded_Holydays_From_ExistingAndValidTestDataFile() throws IOException, ParserException {
        //Arrange
        String state = "Bayern";
        CalendarLoader instance = new CalendarLoader();

        //Act
        instance.loadHolydays(System.getProperty("user.dir") + "/test/testdata/FeiertageBY2025.ics", state);
        ObservableList<Holyday> result = instance.getHolydays();
        
        //Assert
        assertEquals(false, result.isEmpty());
    }
    
    @Test
    public void T21_GetHolydays_Returns_Explicite_Loaded_Holydays_From_ExistingAndValidTestDataFile() throws IOException, ParserException {
        //Arrange
        String state = "Bayern";
        int year = 2025;

        List<Holyday> expResult = new ArrayList<>();
        expResult.add(new Holyday(LocalDate.of(year, Month.JANUARY, 1), "Neujahr", state));
        expResult.add(new Holyday(LocalDate.of(year, Month.JANUARY, 6), "Heilige Drei Könige", state));
        expResult.add(new Holyday(LocalDate.of(year, Month.APRIL, 18), "Karfreitag", state));
        expResult.add(new Holyday(LocalDate.of(year, Month.APRIL, 21), "Ostermontag", state));
        expResult.add(new Holyday(LocalDate.of(year, Month.MAY, 1), "Tag der Arbeit", state));
        expResult.add(new Holyday(LocalDate.of(year, Month.MAY, 29), "Christi Himmelfahrt", state));
        expResult.add(new Holyday(LocalDate.of(year, Month.JUNE, 9), "Pfingstmontag", state));
        expResult.add(new Holyday(LocalDate.of(year, Month.JUNE, 19), "Fronleichnam", state));
        expResult.add(new Holyday(LocalDate.of(year, Month.AUGUST, 15), "Mariä Himmelfahrt", state));
        expResult.add(new Holyday(LocalDate.of(year, Month.OCTOBER, 3), "Tag der Deutschen Einheit", state));
        expResult.add(new Holyday(LocalDate.of(year, Month.NOVEMBER, 1), "Allerheiligen", state));
        expResult.add(new Holyday(LocalDate.of(year, Month.DECEMBER, 25), "1. Weihnachtstag", state));
        expResult.add(new Holyday(LocalDate.of(year, Month.DECEMBER, 26), "2. Weihnachtstag", state));
        
        CalendarLoader instance = new CalendarLoader();

        //Act
        instance.loadHolydays(System.getProperty("user.dir") + "/test/testdata/FeiertageBY2025.ics", "Bayern");
        ObservableList<Holyday> result = instance.getHolydays();
        
        Collections.sort(result, (a,b)->a.getDate().compareTo(b.getDate()));
                
        //Assert
        assertEquals(13, result.size());
        assertEquals(expResult, result);    
    }

}
