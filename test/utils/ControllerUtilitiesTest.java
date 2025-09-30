package utils;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */

import controller.IViewController;
import java.util.ResourceBundle;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author adrest18
 */
public class ControllerUtilitiesTest {
    
    public ControllerUtilitiesTest() {
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
    public void T00_IsNullOrEmpty_Returns_True_If_String_IsEmpty() {
        //Arrange
        String string = "";
        boolean expResult = true;
        
        //Act
        boolean result = ControllerUtilities.isNullOrEmpty(string);
        
        //Assert
        assertEquals(expResult, result);
    }
    
    @Test
    public void T01_IsNullOrEmpty_Returns_True_If_String_IsNull() {
        //Arrange
        String string = null;
        boolean expResult = true;
        
        //Act
        boolean result = ControllerUtilities.isNullOrEmpty(string);
        
        //Assert
        assertEquals(expResult, result);
    }

    @Test
    public void T02_IsNullOrEmpty_Returns_False_If_String_IsNotNullOrEmpty() {
        //Arrange
        String string = "TestString";
        boolean expResult = false;
        
        //Act
        boolean result = ControllerUtilities.isNullOrEmpty(string);
        
        //Assert
        assertEquals(expResult, result);
    }

}
