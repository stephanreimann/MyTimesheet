/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;
import org.junit.*;

/**
 *
 * @author stephan
 */
public class WorkrecordTest {

    public WorkrecordTest() {
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
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        Address addresse = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        User user = new User(1L, role, addresse, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        Project project = new Project(1L, "TestProject", "AAAA.BBBB.CCCC.DDDD", "TRUE", "TRUE", "TRUE", "TestDescription");
        LocalDate date = LocalDate.now();
        LocalTime starttime = LocalTime.now();
        LocalTime endtime = LocalTime.now();
        LocalTime worktime = LocalTime.now();
        String overtime = LocalTime.now().toString();
        String overtimecorrection = LocalTime.now().toString();
        Integer vacationcorrection = 0;
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        String description = "TestDescription";
        
        //Act
        Workrecord workrecord = new Workrecord(id, user, project, date, starttime, endtime, worktime, overtime, overtimecorrection, vacationcorrection, worklocation, description);

        //Assert
        Assert.assertNotNull(workrecord);
        Assert.assertEquals(user, workrecord.getUser());
        Assert.assertEquals(project, workrecord.getProject());
        Assert.assertEquals(date, workrecord.getDate());
        Assert.assertEquals(starttime, workrecord.getStarttime());
        Assert.assertEquals(endtime, workrecord.getEndtime());
        Assert.assertEquals(worktime, workrecord.getWorktime());
        Assert.assertEquals(overtime, workrecord.getOvertime());
        Assert.assertEquals(overtimecorrection, workrecord.getOvertimecorrection());
        Assert.assertEquals(vacationcorrection, workrecord.getVacationcorrection());
        Assert.assertEquals(worklocation, workrecord.getWorklocation());
        Assert.assertEquals(description, workrecord.getDescription());
    }
    
    @Test()
    public void T10_Requesting_HashCode_Twice_For_Same_Project_Instance_Returns_Identical_HashCode() {
        //Arrange
        Long id = 1L;
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        Address addresse = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        User user = new User(1L, role, addresse, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        Project project = new Project(1L, "TestProject", "AAAA.BBBB.CCCC.DDDD", "TRUE", "TRUE", "TRUE", "TestDescription");
        LocalDate date = LocalDate.now();
        LocalTime starttime = LocalTime.now();
        LocalTime endtime = LocalTime.now();
        LocalTime worktime = LocalTime.now();
        String overtime = LocalTime.now().toString();
        String overtimecorrection = LocalTime.now().toString();
        Integer vacationcorrection = 0;
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        String description = "TestDescription";
        Workrecord workrecord = new Workrecord(id, user, project, date, starttime, endtime, worktime, overtime, overtimecorrection, vacationcorrection, worklocation, description);

        //Act
        Integer hashCode_FirstCall = workrecord.hashCode();
        Integer hashCode_SecondCall = workrecord.hashCode();
        
        //Assert
        Assert.assertEquals(hashCode_FirstCall, hashCode_SecondCall);
    }
    
    @Test()
    public void T11_Requesting_HashCode_For_Different_Workrecords_Instance_Returns_Different_HashCode() {
        //Arrange
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        Address addresse = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        User user = new User(1L, role, addresse, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        Project project = new Project(1L, "TestProject", "AAAA.BBBB.CCCC.DDDD", "TRUE", "TRUE", "TRUE", "TestDescription");
        LocalDate date = LocalDate.now();
        LocalTime starttime = LocalTime.now();
        LocalTime endtime = LocalTime.now();
        LocalTime worktime = LocalTime.now();
        String overtime = LocalTime.now().toString();
        String overtimecorrection = LocalTime.now().toString();
        Integer vacationcorrection = 0;
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        String description = "TestDescription";
        Workrecord workrecord1 = new Workrecord(1L, user, project, date, starttime, endtime, worktime, overtime, overtimecorrection, vacationcorrection, worklocation, description);
        Workrecord workrecord2 = new Workrecord(2L, user, project, date, starttime, endtime, worktime, overtime, overtimecorrection, vacationcorrection, worklocation, description);

        //Act
        Integer hashCode1 = workrecord1.hashCode();
        Integer hashCode2 = workrecord2.hashCode();

        //Assert
        Assert.assertNotEquals(hashCode2, hashCode1);
    }
    
    @Test()
    public void T20_Compare_Equal_Workrecords_Returns_True() {
        //Arrange
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        Address addresse = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        User user = new User(1L, role, addresse, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        Project project = new Project(1L, "TestProject", "AAAA.BBBB.CCCC.DDDD", "TRUE", "TRUE", "TRUE", "TestDescription");
        LocalDate date = LocalDate.now();
        LocalTime starttime = LocalTime.now();
        LocalTime endtime = LocalTime.now();
        LocalTime worktime = LocalTime.now();
        String overtime = LocalTime.now().toString();
        String overtimecorrection = LocalTime.now().toString();
        Integer vacationcorrection = 0;
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        String description = "TestDescription";
        Workrecord workrecord1 = new Workrecord(1L, user, project, date, starttime, endtime, worktime, overtime, overtimecorrection, vacationcorrection, worklocation, description);
        Workrecord workrecord2 = new Workrecord(1L, user, project, date, starttime, endtime, worktime, overtime, overtimecorrection, vacationcorrection, worklocation, description);
        
        //Act
        boolean result = workrecord1.equals(workrecord2);
        
        //Assert
        Assert.assertTrue(result);
    }

    @Test()
    public void T21_Compare_NotEqual_Workrecords_Returns_False() {
        //Arrange
        LocalDate dateNow = LocalDate.now();
        LocalTime timeNow = LocalTime.now();
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        Address addresse = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        Project project = new Project(1L, "TestProject", "AAAA.BBBB.CCCC.DDDD", "TRUE", "TRUE", "TRUE", "TestDescription");
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        User user = new User(1L, role, addresse, contract, "Stephan", "Reimann", "stephan", "password", 30L);

        List<Pair<Workrecord, Workrecord>> pairList = new ArrayList<>(); 
        pairList.add(
            new Pair(
                new Workrecord(1L, null, project, dateNow, timeNow, timeNow, timeNow, "00:00:00", "00:00:00", 0, worklocation, "TestDescription"),
                new Workrecord(1L, user, project, dateNow, timeNow, timeNow, timeNow, "00:00:00", "00:00:00", 0, worklocation, "TestDescription")
            )
        );
        pairList.add(
            new Pair(
                new Workrecord(1L, user, null, dateNow, timeNow, timeNow, timeNow, "00:00:00", "00:00:00", 0, worklocation, "TestDescription"),
                new Workrecord(1L, user, project, dateNow, timeNow, timeNow, timeNow, "00:00:00", "00:00:00", 0, worklocation, "TestDescription")
            )
        );
        pairList.add(
            new Pair(
                new Workrecord(1L, user, project, dateNow, timeNow, timeNow, timeNow, "00:00:00", "00:00:00", 0, null, "TestDescription"),
                new Workrecord(1L, user, project, dateNow, timeNow, timeNow, timeNow, "00:00:00", "00:00:00", 0, worklocation, "TestDescription")
            )
        );
        pairList.add(
            new Pair(
                new Workrecord(1L, user, project, dateNow, timeNow, timeNow, timeNow, "00:00:00", "00:00:00", 0, worklocation, "TestDescription"),
                new Workrecord(2L, user, project, dateNow, timeNow, timeNow, timeNow, "00:00:00", "00:00:00", 0, worklocation, "TestDescription")
            )
        );
        pairList.add(
            new Pair(
                new Workrecord(1L, user, project, dateNow, timeNow, timeNow, timeNow, "00:00:00", "00:00:00", 0, worklocation, "TestDescription"),
                new Workrecord(2L, user, project, dateNow.plusDays(1), timeNow, timeNow, timeNow, "00:00:00", "00:00:00", 0, worklocation, "TestDescription")
            )
        );
        pairList.add(
            new Pair(
                new Workrecord(1L, user, project, dateNow, timeNow, timeNow, timeNow, "00:00:00", "00:00:00", 0, worklocation, "TestDescription"),
                new Workrecord(2L, user, project, dateNow, timeNow.plusHours(1), timeNow, timeNow, "00:00:00", "00:00:00", 0, worklocation, "TestDescription")
            )
        );
        pairList.add(
            new Pair(
                new Workrecord(1L, user, project, dateNow, timeNow, timeNow, timeNow, "00:00:00", "00:00:00", 0, worklocation, "TestDescription"),
                new Workrecord(2L, user, project, dateNow, timeNow, timeNow.plusHours(1), timeNow, "00:00:00", "00:00:00", 0, worklocation, "TestDescription")
            )
        );
        pairList.add(
            new Pair(
                new Workrecord(1L, user, project, dateNow, timeNow, timeNow, timeNow, "00:00:00", "00:00:00", 0, worklocation, "TestDescription"),
                new Workrecord(2L, user, project, dateNow, timeNow, timeNow, timeNow.plusHours(1), "00:00:00", "00:00:00", 0, worklocation, "TestDescription")
            )
        );
        pairList.add(
            new Pair(
                new Workrecord(1L, user, project, dateNow, timeNow, timeNow, timeNow, "00:00:00", "00:00:00", 0, worklocation, "TestDescription"),
                new Workrecord(2L, user, project, dateNow, timeNow, timeNow, timeNow, "01:00:00", "00:00:00", 0, worklocation, "TestDescription")
            )
        );
        pairList.add(
            new Pair(
                new Workrecord(1L, user, project, dateNow, timeNow, timeNow, timeNow, "00:00:00", "00:00:00", 0, worklocation, "TestDescription"),
                new Workrecord(2L, user, project, dateNow, timeNow, timeNow, timeNow, "00:00:00", "01:00:00", 0, worklocation, "TestDescription")
            )
        );
        pairList.add(
            new Pair(
                new Workrecord(1L, user, project, dateNow, timeNow, timeNow, timeNow, "00:00:00", "00:00:00", 0, worklocation, "TestDescription"),
                new Workrecord(2L, user, project, dateNow, timeNow, timeNow, timeNow, "00:00:00", "01:00:00", 0, new Worklocation(2L, "Homeoffice", "Worklocation is Homeoffice"), "TestDescription")
            )
        );
        pairList.add(
            new Pair(
                new Workrecord(1L, user, project, dateNow, timeNow, timeNow, timeNow, "00:00:00", "00:00:00", 0, worklocation, "TestDescription"),
                new Workrecord(2L, user, project, dateNow, timeNow, timeNow, timeNow, "00:00:00", "01:00:00", 0, worklocation, "OtherTestDescription")
            )
        );
        pairList.add(
            new Pair(
                new Workrecord(1L, user, project, dateNow, timeNow, timeNow, timeNow, "00:00:00", "00:00:00", 0, worklocation, "TestDescription"),
                new Workrecord(2L, user, project, dateNow, timeNow, timeNow, timeNow, "00:00:00", "01:00:00", 1, worklocation, "TestDescription")
            )
        );

        for(Pair<Workrecord, Workrecord> pair : pairList) {
            Workrecord workrecord1 = pair.getKey();
            Workrecord workrecord2 = pair.getValue();
            
            //Act
            boolean result = workrecord1.equals(workrecord2);

            //Assert
            Assert.assertFalse(result);
        }
    }
 
    @Test()
    public void T30_Print_Workrecord_PrintsResults_In_Expected_PrintFormat() {
        //Arrange
        LocalDate dateNow = LocalDate.now(); 
        LocalTime timeNow = LocalTime.now();
        
        String expectedPrintResult = "User: 1, Reimann, TestProject, " + dateNow.toString() + ", " + timeNow.toString() + ", " + timeNow.toString() + ", " + timeNow.toString() + ", 01:00:00, 01:00:00, 00:00:01, 0, Homeoffice, TestDescription";

        Workrecord workrecord = new Workrecord(1L, 
                        new User(1L, new Role(1L, "Admin", "The Administrator role has access to all application features"), 
                                new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland"), 
                                new Contract(1L, "7 hours contract", 7L, 10L ,30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0)), "Stephan", "Reimann", "stephan", "password", 30L),
                        new Project(1L, "TestProject", "AAAA.BBBB.CCCC.DDDD", "TRUE", "TRUE", "TRUE", "TestDescription"), 
                        dateNow, 
                        timeNow, 
                        timeNow, 
                        timeNow, 
                        "01:00:00",
                        "00:00:01",
                        0,
                        new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice"), 
                        "TestDescription");
        
        //Act
        String printResult = workrecord.toString();

        //Assert
        Assert.assertEquals(expectedPrintResult, printResult);  
    }
    
}
