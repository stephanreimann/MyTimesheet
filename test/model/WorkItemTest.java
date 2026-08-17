/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalTime;
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
public class WorkItemTest {
    
    public WorkItemTest() {
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
        WorkItem workItem = new WorkItem(id);
        
        //Assert
        Assert.assertNotNull(workItem);
        Assert.assertEquals(Long.valueOf(1), workItem.getId());
        Assert.assertEquals(Long.valueOf(0), workItem.getWorkrecordId());
        Assert.assertEquals(Long.valueOf(0), workItem.getSprintId());
        Assert.assertEquals(Long.valueOf(0), workItem.getTrackingItemId());
        Assert.assertNull(workItem.getStartTime());
        Assert.assertNull(workItem.getEndTime());
        Assert.assertNull(workItem.getDescription());
    }    

    @Test()
    public void T01_Parameterized_Ctor_Called_Returns_WithParameterInitialized_Instance() {
        //Arrange
        Long id = 1L;
        Long workrecordId = 1L;
        Long sprintId = 1L;
        Long trackingItemId = 1L;
        LocalTime starttime = LocalTime.now();
        LocalTime endtime = LocalTime.now();
        String description = "TestDescription";
        String shortcut = "TestShortcut";
        String name = "TestName";

        //Act
        WorkItem workItem = new WorkItem(id, workrecordId, sprintId, trackingItemId, starttime, endtime, description, shortcut, name);
        
        //Assert
        Assert.assertNotNull(workItem);
        Assert.assertEquals(id, workItem.getId());
        Assert.assertEquals(workrecordId, workItem.getWorkrecordId());
        Assert.assertEquals(sprintId, workItem.getSprintId());
        Assert.assertEquals(trackingItemId, workItem.getTrackingItemId());
        Assert.assertEquals(starttime, workItem.getStartTime());
        Assert.assertEquals(endtime, workItem.getEndTime());
        Assert.assertEquals(description, workItem.getDescription());
        Assert.assertEquals(shortcut, workItem.getShortcut());
        Assert.assertEquals(name, workItem.getName());
    }    

    @Test()
    public void T10_Requesting_HashCode_Twice_For_Same_Project_Instance_Returns_Identical_HashCode() {
        //Arrange
        Long id = 1L;
        Long workrecordId = 1L;
        Long sprintId = 1L;
        Long trackingItemId = 1L;
        LocalTime starttime = LocalTime.now();
        LocalTime endtime = LocalTime.now();
        String description = "TestDescription";
        String shortcut = "TestShortcut";
        String name = "TestName";
        WorkItem workItem = new WorkItem(id, workrecordId, sprintId, trackingItemId, starttime, endtime, description, shortcut, name);

        //Act
        Integer hashCode_FirstCall = workItem.hashCode();
        Integer hashCode_SecondCall = workItem.hashCode();
        
        //Assert
        Assert.assertEquals(hashCode_FirstCall, hashCode_SecondCall);
    }
    
    @Test()
    public void T11_Requesting_HashCode_For_Different_Workrecords_Instance_Returns_Different_HashCode() {
        //Arrange
        Long workrecordId = 1L;
        Long sprintId = 1L;
        Long trackingItemId = 1L;
        LocalTime starttime = LocalTime.now();
        LocalTime endtime = LocalTime.now();
        String description = "TestDescription";
        String shortcut = "TestShortcut";
        String name = "TestName";
        WorkItem workItem1 = new WorkItem(1L, workrecordId, sprintId, trackingItemId, starttime, endtime, description, shortcut, name);
        WorkItem workItem2 = new WorkItem(2L, workrecordId, sprintId, trackingItemId, starttime, endtime, description, shortcut, name);

        //Act
        Integer hashCode1 = workItem1.hashCode();
        Integer hashCode2 = workItem2.hashCode();

        //Assert
        Assert.assertNotEquals(hashCode2, hashCode1);
    }
    
    @Test()
    public void T20_Compare_Equal_Workrecords_Returns_True() {
        //Arrange
        Long workrecordId = 1L;
        Long sprintId = 1L;
        Long trackingItemId = 1L;
        LocalTime starttime = LocalTime.now();
        LocalTime endtime = LocalTime.now();
        String description = "TestDescription";
        String shortcut = "TestShortcut";
        String name = "TestName";
        WorkItem workItem1 = new WorkItem(1L, workrecordId, sprintId, trackingItemId, starttime, endtime, description, shortcut, name);
        WorkItem workItem2 = new WorkItem(1L, workrecordId, sprintId, trackingItemId, starttime, endtime, description, shortcut, name);
        
        //Act
        boolean result = workItem1.equals(workItem2);
        
        //Assert
        Assert.assertTrue(result);
    }
    
    @Test()
    @SuppressWarnings("unchecked")
    public void T21_Compare_NotEqual_Workrecords_Returns_False() {
        //Arrange
        Long workrecordId = 1L;
        Long sprintId = 1L;
        Long trackingItemId = 1L;
        LocalTime starttime = LocalTime.now();
        LocalTime endtime = LocalTime.now();
        String description = "TestDescription";
        String shortcut = "TestShortcut";
        String name = "TestName";

        List<Pair<WorkItem, WorkItem>> pairList = new ArrayList<>(); 
        pairList.add(
            new Pair(
                new WorkItem(1L, workrecordId, sprintId, trackingItemId, starttime, endtime, description, shortcut, name),
                new WorkItem(2L, workrecordId, sprintId, trackingItemId, starttime, endtime, description, shortcut, name)
            )
        );
        pairList.add(
            new Pair(
                new WorkItem(1L, 1L, sprintId, trackingItemId, starttime, endtime, description, shortcut, name),
                new WorkItem(1L, 2L, sprintId, trackingItemId, starttime, endtime, description, shortcut, name)
            )
        );
        pairList.add(
            new Pair(
                new WorkItem(1L, workrecordId, 1L, trackingItemId, starttime, endtime, description, shortcut, name),
                new WorkItem(1L, workrecordId, 2L, trackingItemId, starttime, endtime, description, shortcut, name)
            )
        );
        pairList.add(
            new Pair(
                new WorkItem(1L, workrecordId, sprintId, 1L, starttime, endtime, description, shortcut, name),
                new WorkItem(1L, workrecordId, sprintId, 2L, starttime, endtime, description, shortcut, name)
            )
        );
        pairList.add(
            new Pair(
                new WorkItem(1L, workrecordId, sprintId, trackingItemId, starttime, endtime, description, shortcut, name),
                new WorkItem(1L, workrecordId, sprintId, trackingItemId, starttime.plusHours(1), endtime, description, shortcut, name)
            )
        );
        pairList.add(
            new Pair(
                new WorkItem(1L, workrecordId, sprintId, trackingItemId, starttime, endtime, description, shortcut, name),
                new WorkItem(1L, workrecordId, sprintId, trackingItemId, starttime, endtime.plusHours(1), description, shortcut, name)
            )
        );
        pairList.add(
            new Pair(
                new WorkItem(1L, workrecordId, sprintId, trackingItemId, starttime, endtime, description, shortcut, name),
                new WorkItem(1L, workrecordId, sprintId, trackingItemId, starttime, endtime, description.concat("Test"), shortcut, name)
            )
        );
        pairList.add(
            new Pair(
                new WorkItem(1L, workrecordId, sprintId, trackingItemId, starttime, endtime, description, shortcut, name),
                new WorkItem(1L, workrecordId, sprintId, trackingItemId, starttime, endtime, description, shortcut.concat("X"), name)
            )
        );
        pairList.add(
            new Pair(
                new WorkItem(1L, workrecordId, sprintId, trackingItemId, starttime, endtime, description, shortcut, name),
                new WorkItem(1L, workrecordId, sprintId, trackingItemId, starttime, endtime, description, shortcut, name.concat("Test"))
            )
        );

        for(Pair<WorkItem, WorkItem> pair : pairList) {
            WorkItem workItem1 = pair.getKey();
            WorkItem workItem2 = pair.getValue();
            
            //Act
            boolean result = workItem1.equals(workItem2);

            //Assert
            Assert.assertFalse(result);
        }
    }
    
    @Test()
    public void T30_Print_Workrecord_PrintsResults_In_Expected_PrintFormat() {
        //Arrange
        Long id = 1L;
        Long workrecordId = 1L;
        Long sprintId = 1L;
        Long trackingItemId = 1L;
        LocalTime starttime = LocalTime.of(1, 0);
        LocalTime endtime = LocalTime.of(1, 0);
        String description = "TestDescription";
        String shortcut = "TestShortcut";
        String name = "TestName";
        
        String expectedPrintResult = "1, 1, 1, 1, 01:00, 01:00, TestDescription, TestShortcut, TestName";

        WorkItem workItem = new WorkItem(id, workrecordId, sprintId, trackingItemId, starttime, endtime, description, shortcut, name);
        
        //Act
        String printResult = workItem.toString();

        //Assert
        Assert.assertEquals(expectedPrintResult, printResult);  
    }
    
}
