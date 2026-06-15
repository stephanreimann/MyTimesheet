/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

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
@SuppressWarnings("unchecked")
public class TrackingItemTest {
    
    public TrackingItemTest() {
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
        
        //Act
        TrackingItem trackingItem = new TrackingItem(id);
        
        //Assert
        Assert.assertNotNull(trackingItem);
        Assert.assertEquals(Long.valueOf(1), trackingItem.getId());
        Assert.assertNull(trackingItem.getName());
        Assert.assertNull(trackingItem.getShortcut());
        Assert.assertNull(trackingItem.getDescription());
    }

    @Test()
    public void T01_Parameterized_Ctor_Called_Returns_WithParameterInitialized_Instance() {
        //Arrange
        Long id = 1L;
        String name = "Scrum";
        String shortcut = "S";
        String description = "All task related to SCRUM activities";
        
        //Act
        TrackingItem trackingItem = new TrackingItem(id, name, shortcut, description);
        
        //Assert
        Assert.assertNotNull(trackingItem);
        Assert.assertEquals(Long.valueOf(1), trackingItem.getId());
        Assert.assertEquals(name, trackingItem.getName());
        Assert.assertEquals(shortcut, trackingItem.getShortcut());
        Assert.assertEquals(description, trackingItem.getDescription());
    }
    
    @Test()
    public void T02_Copy_Ctor_Called_Returns_WithParameterInitialized_Instance() {
        //Arrange
        Long id = 1L;
        String name = "Scrum";
        String shortcut = "S";
        String description = "All task related to SCRUM activities";
        TrackingItem trackingItem = new TrackingItem(id, name, shortcut, description);
        
        //Act
        TrackingItem copiedTrackingItem = new TrackingItem(trackingItem);
        
        //Assert
        Assert.assertNotNull(copiedTrackingItem);
        Assert.assertEquals(trackingItem.getId(), copiedTrackingItem.getId());
        Assert.assertEquals(trackingItem.getName(), copiedTrackingItem.getName());
        Assert.assertEquals(trackingItem.getShortcut(), copiedTrackingItem.getShortcut());
        Assert.assertEquals(trackingItem.getDescription(), copiedTrackingItem.getDescription());
    }

    @Test()
    public void T10_Requesting_HashCode_Twice_For_Same_Role_Instance_Returns_Identical_HashCode() {
        //Arrange
        Long id = 1L;
        String name = "Scrum";
        String shortcut = "S";
        String description = "All task related to SCRUM activities";
        TrackingItem trackingItem = new TrackingItem(id, name, shortcut, description);

        //Act
        Integer hashCode_FirstCall = trackingItem.hashCode();
        Integer hashCode_SecondCall = trackingItem.hashCode();
        
        //Assert
        Assert.assertEquals(hashCode_FirstCall, hashCode_SecondCall);
    }
    
    @Test()
    public void T11_Requesting_HashCode_For_Different_Role_Instance_Returns_Different_HashCode() {
        //Arrange
        TrackingItem trackingItem1 = new TrackingItem(1L, "Scrum", "S", "All task related to SCRUM activities");
        TrackingItem trackingItem2 = new TrackingItem(2L, "Scrum", "S", "All task related to SCRUM activities");

        //Act
        Integer hashCode1 = trackingItem1.hashCode();
        Integer hashCode2 = trackingItem2.hashCode();

        //Assert
        Assert.assertNotEquals(hashCode2, hashCode1);
    }
    
    @Test()
    @SuppressWarnings("unchecked")
    public void T20_Compare_Equal_Roles_Returns_True() {
        //Arrange
        List<Pair<TrackingItem, TrackingItem>> pairList = new ArrayList<>(); 
        pairList.add(
            new Pair(
                new TrackingItem(1L, "Scrum", "S", "All task related to SCRUM activities"),
                new TrackingItem(1L, "Scrum", "S", "All task related to SCRUM activities")
            )
        );
        
        //Act
        for(Pair<TrackingItem, TrackingItem> pair : pairList) {
            TrackingItem trackingItem1 = pair.getKey();
            TrackingItem trackingItem2 = pair.getValue();
            
            //Act
            boolean result = trackingItem1.equals(trackingItem2);
        
            //Assert
            Assert.assertTrue(result);
        }
    }

    @Test()
    @SuppressWarnings("unchecked")
    public void T21_Compare_Different_Roles_Returns_False() {
        //Arrange
        List<Pair<TrackingItem, TrackingItem>> pairList = new ArrayList<>(); 
        pairList.add(
            new Pair(
                new TrackingItem(1L, "Scrum", "S", "All task related to SCRUM activities"),
                new TrackingItem(2L, "Scrum", "S", "All task related to SCRUM activities")
            )
        );
        pairList.add(
            new Pair(
                new TrackingItem(1L, "Scrum", "S", "All task related to SCRUM activities"),
                new TrackingItem(1L, "Feature Development", "S", "All task related to SCRUM activities")
            )
        );
        pairList.add(
            new Pair(
                new TrackingItem(1L, "Scrum", "S", "All task related to SCRUM activities"),
                new TrackingItem(1L, "Scrum", "FD", "All task related to SCRUM activities")
            )
        );
        pairList.add(
            new Pair(
                new TrackingItem(1L, "Scrum", "S", "All task related to SCRUM activities"),
                new TrackingItem(1L, "Scrum", "S", "No task related to SCRUM activities")
            )
        );
        
        for(Pair<TrackingItem, TrackingItem> pair : pairList) {
            TrackingItem trackingItem1 = pair.getKey();
            TrackingItem trackingItem2 = pair.getValue();
            
            //Act
            boolean result = trackingItem1.equals(trackingItem2);

            //Assert
            Assert.assertFalse(result);
        }
    }

    @Test()
    public void T30_Print_Role_PrintsResults_In_Expected_PrintFormat() {
        //Arrange
        String expectedPrintResult = "Scrum";
        
        Long id = 1L;
        String name = "Scrum";
        String shortcut = "S";
        String description = "All task related to SCRUM activities";

        TrackingItem trackingItem = new TrackingItem(id, name, shortcut, description);
        
        //Act
        String printResult = trackingItem.toString();
        
        //Assert
        Assert.assertEquals(expectedPrintResult, printResult);   
    }
    
}
