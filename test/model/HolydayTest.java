/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;
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
public class HolydayTest {

    public HolydayTest() {
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

    @Test()
    public void T00_Parameterized_Ctor_Called_Returns_WithParameterInitialized_Instance() {
        //Arrange
        Long id = 1L;
        String state = "Bayern";
        String name = "Neujahr";
        LocalDate date = LocalDate.now();
        
        //Act
        Holyday holyday = new Holyday(id, date, name, state);
        
        //Assert
        Assert.assertNotNull(holyday);
        Assert.assertEquals(id, holyday.getId());
        Assert.assertEquals(date, holyday.getDate());
        Assert.assertEquals(name, holyday.getName());
        Assert.assertEquals(state, holyday.getState());
    }
    
    @Test()
    public void T01_Copy_Ctor_Called_Returns_WithParameterInitialized_Instance() {
        //Arrange
        Long id = 1L;
        String state = "Bayern";
        String name = "Neujahr";
        LocalDate date = LocalDate.now();
        Holyday holyday = new Holyday(id, date, name, state);
        
        //Act
        Holyday copiedHolyday = new Holyday(holyday);
        
        //Assert
        Assert.assertNotNull(copiedHolyday);
        Assert.assertEquals(id, copiedHolyday.getId());
        Assert.assertEquals(date, copiedHolyday.getDate());
        Assert.assertEquals(name, copiedHolyday.getName());
        Assert.assertEquals(state, copiedHolyday.getState());
    }
    
    @Test()
    public void T10_Requesting_HashCode_Twice_For_Same_Holyday_Instance_Returns_Identical_HashCode() {
        //Arrange
        Holyday holyday = new Holyday(1L, LocalDate.now(), "Neujahr", "Bayern");

        //Act
        Integer hashCode_FirstCall = holyday.hashCode();
        Integer hashCode_SecondCall = holyday.hashCode();
        
        //Assert
        Assert.assertEquals(hashCode_FirstCall, hashCode_SecondCall);
    }
    
    @Test()
    public void T11_Requesting_HashCode_For_Different_Address_Instance_Returns_Different_HashCode() {
        //Arrange
        Holyday holyday1 = new Holyday(1L, LocalDate.now(), "Neujahr", "Bayern");
        Holyday holyday2 = new Holyday(1L, LocalDate.now(), "Neujahr", "Sachsen");

        //Act
        Integer hashCode1 = holyday1.hashCode();
        Integer hashCode2 = holyday2.hashCode();

        //Assert
        Assert.assertNotEquals(hashCode2, hashCode1);
    }
    
    @Test()
    public void T20_Compare_Equal_Holydays_Returns_True() {
        //Arrange
        List<Pair<Holyday, Holyday>> pairList = new ArrayList<>(); 
        pairList.add(
            new Pair(
                new Holyday(1L, LocalDate.now(), "Neujahr", "Bayern"),
                new Holyday(1L, LocalDate.now(), "Neujahr", "Bayern")
            )
        );
        pairList.add(
            new Pair(
                new Holyday(1L, LocalDate.now(), "Neujahr", "Bayern"),
                new Holyday(2L, LocalDate.now(), "Neujahr", "Bayern")
            )
        );
        
        for(Pair<Holyday, Holyday> pair : pairList) {
            Holyday holyday1 = pair.getKey();
            Holyday holyday2 = pair.getValue();
            
            //Act
            boolean result = holyday1.equals(holyday2);

            //Assert
            Assert.assertTrue(result);
        }
    }
    
    @Test()
    public void T21_Compare_Different_Addresses_Returns_False() {
        //Arrange
        List<Pair<Holyday, Holyday>> pairList = new ArrayList<>(); 
        pairList.add(
            new Pair(
                new Holyday(1L, LocalDate.now(), "Neujahr", "Bayern"),
                new Holyday(1L, LocalDate.now(), "Neujahr", "Sachsen")
            )
        );
        pairList.add(
            new Pair(
                new Holyday(1L, LocalDate.now(), "Neujahr", "Bayern"),
                new Holyday(1L, LocalDate.now(), "WasAnderes", "Bayern")
            )
        );
        pairList.add(
            new Pair(
                new Holyday(1L, LocalDate.now(), "Neujahr", "Bayern"),
                new Holyday(1L, LocalDate.now().plusDays(1), "Neujahr", "Bayern")
            )
        );
        
        for(Pair<Holyday, Holyday> pair : pairList) {
            Holyday holyday1 = pair.getKey();
            Holyday holyday2 = pair.getValue();
            
            //Act
            boolean result = holyday1.equals(holyday2);

            //Assert
            Assert.assertFalse(result);
        }
    }
    
    @Test()
    public void T30_Print_Holyday_PrintsResults_In_Expected_PrintFormat() {
        //Arrange
        String expectedPrintResult = LocalDate.now() + ", Neujahr, Bayern";
        
        Long id = 1L;
        LocalDate date = LocalDate.now();
        String name = "Neujahr";
        String country = "Bayern";
        
        Holyday holyday = new Holyday(id, date, name, country);
        
        //Act
        String printResult = holyday.toString();
        
        //Assert
        Assert.assertEquals(expectedPrintResult, printResult);       
    }
    
    @Test()
    public void T40_Compare_Two_Holydays_That_Have_Equal_Dates_Returns_True() {
        //Arrange
        int expectedCompareResult = 0;
        
        Long id = 1L;
        LocalDate date = LocalDate.now();
        String name = "Neujahr";
        String country = "Bayern";
        
        Holyday holyday = new Holyday(id, date, name, country);
        
        //Act
        boolean comapareResult = holyday.equals(holyday);
        
        //Assert
        Assert.assertTrue(comapareResult);
    }

    @Test()
    public void T41_Compare_Two_Holydays_That_Differ_In_Date_Returns_False() {
        //Arrange
        int expectedCompareResult = 0;
        
        Long id = 1L;
        LocalDate date1 = LocalDate.now();
        LocalDate date2 = LocalDate.now().plusDays(1);
        String name = "Neujahr";
        String country = "Bayern";
        
        Holyday holyday1 = new Holyday(id, date1, name, country);
        Holyday holyday2 = new Holyday(id, date2, name, country);
        
        //Act
        boolean comapareResult = holyday1.equals(holyday2);
        
        //Assert
        Assert.assertFalse(comapareResult);       
    }

}
