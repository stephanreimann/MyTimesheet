/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDate;
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
        Assert.assertNull(workItem.getUser());
        Assert.assertNull(workItem.getWorkrecord());
        Assert.assertNull(workItem.getSprint());
        Assert.assertNull(workItem.getTrackingItem());
        Assert.assertNull(workItem.getStartTime());
        Assert.assertNull(workItem.getEndTime());
        Assert.assertNull(workItem.getDescription());
    }    

    @Test()
    public void T01_Parameterized_Ctor_Called_Returns_WithParameterInitialized_Instance() {
        //Arrange
        Long id = 1L;
        String description = "TestDescription";
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        Address addresse = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        User user = new User(1L, role, addresse, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        Project project = new Project(1L, "TestProject", "AAAA.BBBB.CCCC.DDDD", "TRUE", "TRUE", "TRUE", "TestDescription");
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        Workrecord workrecord = new Workrecord(id, user, project, LocalDate.now(), LocalTime.now(), LocalTime.now(), LocalTime.now(), LocalTime.now().toString(), LocalTime.now().toString(), 0, worklocation, description);
        Sprint sprint = new Sprint(id, LocalDate.now(), LocalDate.now(), 10L);
        TrackingItem trackingItem = new TrackingItem(1L, "Scrum", "S", "All task related to SCRUM activities");
        LocalTime starttime = LocalTime.now();
        LocalTime endtime = LocalTime.now();

        //Act
        WorkItem workItem = new WorkItem(id, user, workrecord, sprint, trackingItem, starttime, endtime, description);
        
        //Assert
        Assert.assertNotNull(workItem);
        Assert.assertEquals(id, workItem.getId());
        Assert.assertEquals(user, workItem.getUser());
        Assert.assertEquals(workrecord, workItem.getWorkrecord());
        Assert.assertEquals(sprint, workItem.getSprint());
        Assert.assertEquals(trackingItem, workItem.getTrackingItem());
        Assert.assertEquals(starttime, workItem.getStartTime());
        Assert.assertEquals(endtime, workItem.getEndTime());
        Assert.assertEquals(description, workItem.getDescription());
    }    

    @Test()
    public void T10_Requesting_HashCode_Twice_For_Same_Project_Instance_Returns_Identical_HashCode() {
        //Arrange
        Long id = 1L;
        String description = "TestDescription";
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        Address addresse = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        User user = new User(1L, role, addresse, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        Project project = new Project(1L, "TestProject", "AAAA.BBBB.CCCC.DDDD", "TRUE", "TRUE", "TRUE", "TestDescription");
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        Workrecord workrecord = new Workrecord(id, user, project, LocalDate.now(), LocalTime.now(), LocalTime.now(), LocalTime.now(), LocalTime.now().toString(), LocalTime.now().toString(), 0, worklocation, description);
        Sprint sprint = new Sprint(id, LocalDate.now(), LocalDate.now(), 10L);
        TrackingItem trackingItem = new TrackingItem(1L, "Scrum", "S", "All task related to SCRUM activities");
        LocalTime starttime = LocalTime.now();
        LocalTime endtime = LocalTime.now();
        WorkItem workItem = new WorkItem(id, user, workrecord, sprint, trackingItem, starttime, endtime, description);

        //Act
        Integer hashCode_FirstCall = workItem.hashCode();
        Integer hashCode_SecondCall = workItem.hashCode();
        
        //Assert
        Assert.assertEquals(hashCode_FirstCall, hashCode_SecondCall);
    }
    
    @Test()
    public void T11_Requesting_HashCode_For_Different_Workrecords_Instance_Returns_Different_HashCode() {
        //Arrange
        String description = "TestDescription";
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        Address addresse = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        User user = new User(1L, role, addresse, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        Project project = new Project(1L, "TestProject", "AAAA.BBBB.CCCC.DDDD", "TRUE", "TRUE", "TRUE", "TestDescription");
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        Workrecord workrecord = new Workrecord(1L, user, project, LocalDate.now(), LocalTime.now(), LocalTime.now(), LocalTime.now(), LocalTime.now().toString(), LocalTime.now().toString(), 0, worklocation, description);
        Sprint sprint = new Sprint(1L, LocalDate.now(), LocalDate.now(), 10L);
        TrackingItem trackingItem = new TrackingItem(1L, "Scrum", "S", "All task related to SCRUM activities");
        LocalTime starttime = LocalTime.now();
        LocalTime endtime = LocalTime.now();
        WorkItem workItem1 = new WorkItem(1L, user, workrecord, sprint, trackingItem, starttime, endtime, description);
        WorkItem workItem2 = new WorkItem(2L, user, workrecord, sprint, trackingItem, starttime, endtime, description);

        //Act
        Integer hashCode1 = workItem1.hashCode();
        Integer hashCode2 = workItem2.hashCode();

        //Assert
        Assert.assertNotEquals(hashCode2, hashCode1);
    }
    
    @Test()
    public void T20_Compare_Equal_Workrecords_Returns_True() {
        //Arrange
        String description = "TestDescription";
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        Address addresse = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        User user = new User(1L, role, addresse, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        Project project = new Project(1L, "TestProject", "AAAA.BBBB.CCCC.DDDD", "TRUE", "TRUE", "TRUE", "TestDescription");
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        Workrecord workrecord = new Workrecord(1L, user, project, LocalDate.now(), LocalTime.now(), LocalTime.now(), LocalTime.now(), LocalTime.now().toString(), LocalTime.now().toString(), 0, worklocation, description);
        Sprint sprint = new Sprint(1L, LocalDate.now(), LocalDate.now(), 10L);
        TrackingItem trackingItem = new TrackingItem(1L, "Scrum", "S", "All task related to SCRUM activities");
        LocalTime starttime = LocalTime.now();
        LocalTime endtime = LocalTime.now();
        WorkItem workItem1 = new WorkItem(1L, user, workrecord, sprint, trackingItem, starttime, endtime, description);
        WorkItem workItem2 = new WorkItem(1L, user, workrecord, sprint, trackingItem, starttime, endtime, description);
        
        //Act
        boolean result = workItem1.equals(workItem2);
        
        //Assert
        Assert.assertTrue(result);
    }
    
    @Test()
    public void T21_Compare_NotEqual_Workrecords_Returns_False() {
        //Arrange
        String description = "TestDescription";
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        Address addresse = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        User user = new User(1L, role, addresse, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        Project project = new Project(1L, "TestProject", "AAAA.BBBB.CCCC.DDDD", "TRUE", "TRUE", "TRUE", "TestDescription");
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        Workrecord workrecord = new Workrecord(1L, user, project, LocalDate.now(), LocalTime.now(), LocalTime.now(), LocalTime.now(), LocalTime.now().toString(), LocalTime.now().toString(), 0, worklocation, description);
        Sprint sprint = new Sprint(1L, LocalDate.now(), LocalDate.now(), 10L);
        TrackingItem trackingItem = new TrackingItem(1L, "Scrum", "S", "All task related to SCRUM activities");
        LocalTime starttime = LocalTime.now();
        LocalTime endtime = LocalTime.now();

        List<Pair<WorkItem, WorkItem>> pairList = new ArrayList<>(); 
        pairList.add(
            new Pair(
                new WorkItem(1L, user, workrecord, sprint, trackingItem, starttime, endtime, description),
                new WorkItem(2L, user, workrecord, sprint, trackingItem, starttime, endtime, description)
            )
        );
        pairList.add(
            new Pair(
                new WorkItem(1L, user, workrecord, sprint, trackingItem, starttime, endtime, description),
                new WorkItem(1L, null, workrecord, sprint, trackingItem, starttime, endtime, description)
            )
        );
        pairList.add(
            new Pair(
                new WorkItem(1L, user, workrecord, sprint, trackingItem, starttime, endtime, description),
                new WorkItem(1L, user, null, sprint, trackingItem, starttime, endtime, description)
            )
        );
        pairList.add(
            new Pair(
                new WorkItem(1L, user, workrecord, sprint, trackingItem, starttime, endtime, description),
                new WorkItem(1L, user, workrecord, null, trackingItem, starttime, endtime, description)
            )
        );
        pairList.add(
            new Pair(
                new WorkItem(1L, user, workrecord, sprint, trackingItem, starttime, endtime, description),
                new WorkItem(1L, user, workrecord, sprint, null, starttime, endtime, description)
            )
        );
        pairList.add(
            new Pair(
                new WorkItem(1L, user, workrecord, sprint, trackingItem, starttime, endtime, description),
                new WorkItem(1L, user, workrecord, sprint, trackingItem, starttime.plusHours(1), endtime, description)
            )
        );
        pairList.add(
            new Pair(
                new WorkItem(1L, user, workrecord, sprint, trackingItem, starttime, endtime, description),
                new WorkItem(1L, user, workrecord, sprint, trackingItem, starttime, endtime.plusHours(1), description)
            )
        );
        pairList.add(
            new Pair(
                new WorkItem(1L, user, workrecord, sprint, trackingItem, starttime, endtime, description),
                new WorkItem(1L, user, workrecord, sprint, trackingItem, starttime, endtime, description + "_")
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
        String description = "TestDescription";
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        Address addresse = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        User user = new User(1L, role, addresse, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        Project project = new Project(1L, "TestProject", "AAAA.BBBB.CCCC.DDDD", "TRUE", "TRUE", "TRUE", "TestDescription");
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        Workrecord workrecord = new Workrecord(1L, user, project, LocalDate.now(), LocalTime.now(), LocalTime.now(), LocalTime.now(), LocalTime.now().toString(), LocalTime.now().toString(), 0, worklocation, description);
        Sprint sprint = new Sprint(1L, LocalDate.now(), LocalDate.now(), 10L);
        TrackingItem trackingItem = new TrackingItem(1L, "Scrum", "S", "All task related to SCRUM activities");
        LocalTime starttime = LocalTime.of(1, 0, 0, 0);
        LocalTime endtime = LocalTime.of(1, 0, 0, 0);
        
        String expectedPrintResult = "1, 1, 1, 1, 1, 01:00, 01:00, TestDescription";

        WorkItem workItem = new WorkItem(1L, user, workrecord, sprint, trackingItem, starttime, endtime, description);
        
        //Act
        String printResult = workItem.toString();

        //Assert
        Assert.assertEquals(expectedPrintResult, printResult);  
    }
    
}
