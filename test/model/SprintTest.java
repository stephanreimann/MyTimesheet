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
@SuppressWarnings("unchecked")
public class SprintTest {
    
    public SprintTest() {
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
        Sprint sprint = new Sprint(id);
        
        //Assert
        Assert.assertNotNull(sprint);
        Assert.assertEquals(Long.valueOf(1), sprint.getId());
        Assert.assertNull(sprint.getStartDate());
        Assert.assertNull(sprint.getEndDate());
        Assert.assertNull(sprint.getNumberOfSprintDays());
    }

    @Test()
    public void T01_Parameterized_Ctor_Called_Returns_WithParameterInitialized_Instance() {
        //Arrange
        Long id = 138L;
        Integer numberOfSprintDays = 10;
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(numberOfSprintDays);
        
        //Act
        Sprint sprint = new Sprint(id, startDate, endDate, numberOfSprintDays);
        
        //Assert
        Assert.assertNotNull(sprint);
        Assert.assertEquals(Long.valueOf(138), sprint.getId());
        Assert.assertEquals(startDate, sprint.getStartDate());
        Assert.assertEquals(endDate, sprint.getEndDate());
        Assert.assertEquals(numberOfSprintDays, sprint.getNumberOfSprintDays());
    }

    @Test()
    public void T02_Copy_Ctor_Called_Returns_WithParameterInitialized_Instance() {
        //Arrange
        Long id = 138L;
        Integer numberOfSprintDays = 10;
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(numberOfSprintDays);
        Sprint sprint = new Sprint(id, startDate, endDate, numberOfSprintDays);
        
        //Act
        Sprint copiedSprint = new Sprint(sprint);
        
        //Assert
        Assert.assertNotNull(copiedSprint);
        Assert.assertEquals(sprint.getId(), copiedSprint.getId());
        Assert.assertEquals(sprint.getStartDate(), copiedSprint.getStartDate());
        Assert.assertEquals(sprint.getEndDate(), copiedSprint.getEndDate());
        Assert.assertEquals(sprint.getNumberOfSprintDays(), copiedSprint.getNumberOfSprintDays());
    }

    @Test()
    public void T10_Requesting_HashCode_Twice_For_Same_Sprint_Instance_Returns_Identical_HashCode() {
        //Arrange
        Long id = 138L;
        Integer numberOfSprintDays = 10;
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(numberOfSprintDays);
        Sprint sprint = new Sprint(id, startDate, endDate, numberOfSprintDays);

        //Act
        Integer hashCode_FirstCall = sprint.hashCode();
        Integer hashCode_SecondCall = sprint.hashCode();
        
        //Assert
        Assert.assertEquals(hashCode_FirstCall, hashCode_SecondCall);
    }
    
    @Test()
    public void T11_Requesting_HashCode_For_Different_Sprint_Instance_Returns_Different_HashCode() {
        //Arrange
        Integer numberOfSprintDays = 10;
        Sprint sprint1 = new Sprint(138L, LocalDate.now(), LocalDate.now().plusDays(numberOfSprintDays), numberOfSprintDays);
        Sprint sprint2 = new Sprint(139L, LocalDate.now(), LocalDate.now().plusDays(numberOfSprintDays), numberOfSprintDays);

        //Act
        Integer hashCode1 = sprint1.hashCode();
        Integer hashCode2 = sprint2.hashCode();

        //Assert
        Assert.assertNotEquals(hashCode2, hashCode1);
    }
    
    @Test()
    @SuppressWarnings({"unchecked"})
    public void T20_Compare_Equal_Roles_Returns_True() {
        //Arrange
        Integer numberOfSprintDays = 10;

        List<Pair<Sprint, Sprint>> pairList = new ArrayList<>(); 
        pairList.add(
            new Pair(
                new Sprint(138L, LocalDate.now(), LocalDate.now().plusDays(numberOfSprintDays), numberOfSprintDays),
                new Sprint(138L, LocalDate.now(), LocalDate.now().plusDays(numberOfSprintDays), numberOfSprintDays)
            )
        );
        
        //Act
        for(Pair<Sprint, Sprint> pair : pairList) {
            Sprint sprint1 = pair.getKey();
            Sprint sprint2 = pair.getValue();
            
            //Act
            boolean result = sprint1.equals(sprint2);
        
            //Assert
            Assert.assertTrue(result);
        }
    }

    @Test()
    @SuppressWarnings("unchecked")
    public void T21_Compare_Different_Roles_Returns_False() {
        //Arrange
        Integer numberOfSprintDays = 10;

        List<Pair<Sprint, Sprint>> pairList = new ArrayList<>(); 
        pairList.add(
            new Pair(
                new Sprint(138L, LocalDate.now(), LocalDate.now().plusDays(numberOfSprintDays), numberOfSprintDays),
                new Sprint(139L, LocalDate.now(), LocalDate.now().plusDays(numberOfSprintDays), numberOfSprintDays)
            )
        );
        pairList.add(
            new Pair(
                new Sprint(138L, LocalDate.now(), LocalDate.now().plusDays(numberOfSprintDays), numberOfSprintDays),
                new Sprint(138L, LocalDate.now().plusDays(1), LocalDate.now().plusDays(numberOfSprintDays), numberOfSprintDays)
            )
        );
        pairList.add(
            new Pair(
                new Sprint(138L, LocalDate.now(), LocalDate.now().plusDays(numberOfSprintDays), numberOfSprintDays),
                new Sprint(138L, LocalDate.now(), LocalDate.now().plusDays(numberOfSprintDays+1), numberOfSprintDays)
            )
        );
        pairList.add(
            new Pair(
                new Sprint(138L, LocalDate.now(), LocalDate.now().plusDays(numberOfSprintDays), numberOfSprintDays),
                new Sprint(138L, LocalDate.now(), LocalDate.now().plusDays(numberOfSprintDays), numberOfSprintDays+1)
            )
        );
        
        for(Pair<Sprint, Sprint> pair : pairList) {
            Sprint sprint1 = pair.getKey();
            Sprint sprint2 = pair.getValue();
            
            //Act
            boolean result = sprint1.equals(sprint2);

            //Assert
            Assert.assertFalse(result);
        }
    }

    @Test()
    public void T30_Print_Role_PrintsResults_In_Expected_PrintFormat() {
        //Arrange
        String expectedPrintResult = "138";
        
        Long id = 138L;
        Integer numberOfSprintDays = 10;
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(numberOfSprintDays);

        Sprint sprint = new Sprint(id, startDate, endDate, numberOfSprintDays);
        
        //Act
        String printResult = sprint.toString();
        
        //Assert
        Assert.assertEquals(expectedPrintResult, printResult);   
    }
    
}
