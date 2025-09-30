/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import model.Worklocation;
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
public class WorklocationTest {
    
    public WorklocationTest() {
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
        String name = "Homeoffice";
        String description = "Worklocation is Homeoffice";
        
        //Act
        Worklocation worklocation = new Worklocation(id, name, description);
        
        //Assert
        Assert.assertNotNull(worklocation);
        Assert.assertEquals(Long.valueOf(1), worklocation.getId());
        Assert.assertEquals(name, worklocation.getName());
        Assert.assertEquals(description, worklocation.getDescription());
    }

    @Test()
    public void T10_Requesting_HashCode_Twice_For_Same_Worklocation_Instance_Returns_Identical_HashCode() {
        //Arrange
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");

        //Act
        Integer hashCode_FirstCall = worklocation.hashCode();
        Integer hashCode_SecondCall = worklocation.hashCode();
        
        //Assert
        Assert.assertEquals(hashCode_FirstCall, hashCode_SecondCall);
    }
    
    @Test()
    public void T11_Requesting_HashCode_For_Different_Role_Instance_Returns_Different_HashCode() {
        //Arrange
        Worklocation worklocation1 = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        Worklocation worklocation2 = new Worklocation(2L, "Homeoffice", "Worklocation is Homeoffice");

        //Act
        Integer hashCode1 = worklocation1.hashCode();
        Integer hashCode2 = worklocation2.hashCode();

        //Assert
        Assert.assertNotEquals(hashCode2, hashCode1);
    }
 
    @Test()
    public void T20_Compare_Equal_Worklocations_Returns_True() {
        //Arrange
        Worklocation worklocation1 = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        Worklocation worklocation2 = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        
        //Act
        boolean result = worklocation1.equals(worklocation2);
        
        //Assert
        Assert.assertTrue(result);
    }
   
    @Test()
    public void T21_Compare_NotEqual_Worklocations_Returns_False() {
        //Arrange
        List<Pair<Worklocation, Worklocation>> pairList = new ArrayList<>(); 
        pairList.add(
            new Pair(
                new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice"),
                new Worklocation(2L, "Homeoffice", "Worklocation is Homeoffice")
            )
        );
        pairList.add(
            new Pair(
                new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice"),
                new Worklocation(1L, "Erlangen", "Worklocation is Homeoffice")
            )
        );
        pairList.add(
            new Pair(
                new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice"),
                new Worklocation(1L, "Homeoffice", "Worklocation is Erlangen")
            )
        );
        
        for(Pair<Worklocation, Worklocation> pair : pairList) {
            Worklocation worklocation1 = pair.getKey();
            Worklocation worklocation2 = pair.getValue();
            
            //Act
            boolean result = worklocation1.equals(worklocation2);

            //Assert
            Assert.assertFalse(result);
        }
    }
  
    @Test()
    public void T30_Print_Worklocation_PrintsResults_In_Expected_PrintFormat() {
        //Arrange
        String expectedPrintResult = "Homeoffice";
        
        Long id = 1L;
        String name = "Homeoffice";
        String description = "Worklocation is Homeoffice";

        Worklocation worklocation = new Worklocation(id, name, description);
        
        //Act
        String printResult = worklocation.toString();
        
        //Assert
        Assert.assertEquals(expectedPrintResult, printResult);   
    }
    
}
