/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.*;
import javafx.util.Pair;
import org.junit.*;

/**
 *
 * @author stephan
 */
@SuppressWarnings("unchecked")
public class ProjectTest {

    public ProjectTest() {
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
        String name = "TestProject";
        String costUnit = "TestCostUnit";
        String isWorktimeRelevant = "TRUE";
        String isVacationRelevant = "TRUE";
        String isComptimeRelevant = "TRUE";
        String description ="TestDescription";
        
        //Act
        Project project = new Project(id, name, costUnit, isWorktimeRelevant, isVacationRelevant, isComptimeRelevant, description);
        
        //Assert
        Assert.assertNotNull(project);
        Assert.assertEquals(id, project.getId());
        Assert.assertEquals(name, project.getName());
        Assert.assertEquals(costUnit, project.getCostunit());
        Assert.assertEquals(isWorktimeRelevant, project.getIsWorktimeRelevant());
        Assert.assertEquals(isVacationRelevant, project.getIsVacationRelevant());
        Assert.assertEquals(description, project.getDescription());
    }
    
    @Test()
    public void T10_Requesting_HashCode_Twice_For_Same_Project_Instance_Returns_Identical_HashCode() {
        //Arrange
        Long id = 1L;
        String name = "TestProject";
        String costUnit = "TestCostUnit";
        String isWorktimeRelevant = "TRUE";
        String isVacationRelevant = "TRUE";
        String isComptimeRelevant = "TRUE";
        String description ="TestDescription";
        
        Project project = new Project(id, name, costUnit, isWorktimeRelevant, isVacationRelevant, isComptimeRelevant, description);

        //Act
        Integer hashCode_FirstCall = project.hashCode();
        Integer hashCode_SecondCall = project.hashCode();
        
        //Assert
        Assert.assertEquals(hashCode_FirstCall, hashCode_SecondCall);
    }
    
    @Test()
    public void T11_Requesting_HashCode_For_Different_Address_Instance_Returns_Different_HashCode() {
        //Arrange
        Project project1 = new Project(1L, "TestProject1", "TestCostUnit1", "TRUE", "TRUE", "TRUE", "TestDescription1");
        Project project2 = new Project(2L, "TestProject2", "TestCostUnit2", "TRUE", "TRUE", "TRUE", "TestDescription2");

        //Act
        Integer hashCode1 = project1.hashCode();
        Integer hashCode2 = project2.hashCode();

        //Assert
        Assert.assertNotEquals(hashCode2, hashCode1);
    }

    @Test()
    public void T20_Compare_Equal_Addresses_Returns_True() {
        //Arrange
        Project project1 = new Project(1L, "TestProject", "TestCostUnit", "TRUE", "TRUE", "TRUE", "TestDescription");
        Project project2 = new Project(1L, "TestProject", "TestCostUnit", "TRUE", "TRUE", "TRUE", "TestDescription");
        
        //Act
        boolean result = project1.equals(project2);
        
        //Assert
        Assert.assertTrue(result);
    }

    @Test()
    public void T21_Compare_NotEqual_Addresses_Returns_False() {
        //Arrange
        List<Pair<Project, Project>> pairList = new ArrayList<>(); 
        pairList.add(
            new Pair(
                new Project(1L, "TestProject", "TestCostUnit", "TRUE", "TRUE", "TRUE", "TestDescription"),
                new Project(2L, "TestProject", "TestCostUnit", "TRUE", "TRUE", "TRUE", "TestDescription")
            )
        );
        pairList.add(
            new Pair(
                new Project(1L, "TestProject", "TestCostUnit", "TRUE", "TRUE", "TRUE", "TestDescription"),
                new Project(1L, "TestProject1", "TestCostUnit", "TRUE", "TRUE", "TRUE", "TestDescription")
            )
        );
        pairList.add(
            new Pair(
                new Project(1L, "TestProject", "TestCostUnit", "TRUE", "TRUE", "TRUE", "TestDescription"),
                new Project(1L, "TestProject", "TestCostUnit1", "TRUE", "TRUE", "TRUE", "TestDescription")
            )
        );
        pairList.add(
            new Pair(
                new Project(1L, "TestProject", "TestCostUnit", "TRUE", "TRUE", "TRUE", "TestDescription"),
                new Project(1L, "TestProject", "TestCostUnit1", "FALSE", "TRUE", "TRUE", "TestDescription")
            )
        );
        pairList.add(
            new Pair(
                new Project(1L, "TestProject", "TestCostUnit", "TRUE", "TRUE", "TRUE", "TestDescription"),
                new Project(1L, "TestProject", "TestCostUnit1", "TRUE", "FALSE", "TRUE", "TestDescription")
            )
        );
        pairList.add(
            new Pair(
                new Project(1L, "TestProject", "TestCostUnit", "TRUE", "TRUE", "TRUE", "TestDescription"),
                new Project(1L, "TestProject", "TestCostUnit", "TRUE", "TRUE", "TRUE", "TestDescription1")
            )
        );
        
        for(Pair<Project, Project> pair : pairList) {
            Project project1 = pair.getKey();
            Project project2 = pair.getValue();
            
            //Act
            boolean result = project1.equals(project2);

            //Assert
            Assert.assertFalse(result);
        }
    }
 
    @Test()
    public void T30_Print_Addresse_PrintsResults_In_Expected_PrintFormat() {
        //Arrange
        String expectedPrintResult = "TestProject";
        
        Long id = 1L;
        String name = "TestProject";
        String costUnit = "TestCostUnit";
        String isWorktimeRelevant = "TRUE";
        String isVacationRelevant = "TRUE";
        String isComptimeRelevant = "TRUE";
        String description = "TestDescription";

        Project project = new Project(id, name, costUnit, isWorktimeRelevant, isVacationRelevant, isComptimeRelevant, description);
        
        //Act
        String printResult = project.toString();
        
        //Assert
        Assert.assertEquals(expectedPrintResult, printResult);
        
    }

}
