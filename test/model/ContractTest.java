/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalTime;
import java.util.*;
import javafx.util.Pair;
import org.junit.*;

/**
 *
 * @author adrest18
 */
public class ContractTest {
    
    public ContractTest() {
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
        String name = "7 hours contract"; 
        Long workhours = 7L; 
        Long maxworkhours = 10L;
        Long vacationdays = 30L;
        String vacationreconciliationdate = "31.03"; 
        LocalTime breakfastofftimeinminutes = LocalTime.of(0, 15);
        LocalTime breakfastofftimestart = LocalTime.of(9, 0); 
        LocalTime lunchofftimeinminutes = LocalTime.of(0, 30);
        LocalTime lunchofftimestart = LocalTime.of(12, 0); 
        LocalTime earliestworktimestart = LocalTime.of(5, 0, 0);
        LocalTime latestworktimeend = LocalTime.of(22, 0, 0);  
                    
        //Act
        Contract contract = new Contract(id, name, workhours, maxworkhours, 
            vacationdays, vacationreconciliationdate, breakfastofftimeinminutes, 
            breakfastofftimestart, lunchofftimeinminutes, lunchofftimestart,
            earliestworktimestart, latestworktimeend);
        
        //Assert
        Assert.assertNotNull(contract);
        Assert.assertEquals(id, contract.getId());
        Assert.assertEquals(name, contract.getName());
        Assert.assertEquals(workhours, contract.getWorkhours());
        Assert.assertEquals(maxworkhours, contract.getMaxworkhours());
        Assert.assertEquals(vacationdays, contract.getVacationdays());
        Assert.assertEquals(vacationreconciliationdate, contract.getVacationreconciliationdate());
        Assert.assertEquals(breakfastofftimeinminutes, contract.getBreakfastofftimeend());
        Assert.assertEquals(breakfastofftimestart, contract.getBreakfastofftimestart());
        Assert.assertEquals(lunchofftimeinminutes, contract.getLunchofftimeend());
        Assert.assertEquals(lunchofftimestart, contract.getLunchofftimestart());
        Assert.assertEquals(earliestworktimestart, contract.getEarliestworktimestart());
        Assert.assertEquals(latestworktimeend, contract.getLatestworktimeend());
    }

    @Test()
    public void T01_Copy_Ctor_Called_Returns_WithParameterInitialized_Instance() {
        //Arrange
        Long id = 1L;
        String name = "7 hours contract"; 
        Long workhours = 7L; 
        Long maxworkhours = 10L;
        Long vacationdays = 30L;
        String vacationreconciliationdate = "31.03"; 
        LocalTime breakfastofftimeinminutes = LocalTime.of(0, 15);
        LocalTime breakfastofftimestart = LocalTime.of(9, 0); 
        LocalTime lunchofftimeinminutes = LocalTime.of(0, 30);
        LocalTime lunchofftimestart = LocalTime.of(12, 0); 
        LocalTime earliestworktimestart = LocalTime.of(5, 0, 0);
        LocalTime latestworktimeend = LocalTime.of(22, 0, 0);  
        Contract contract = new Contract(id, name, workhours, maxworkhours, 
            vacationdays, vacationreconciliationdate, breakfastofftimeinminutes, 
            breakfastofftimestart, lunchofftimeinminutes, lunchofftimestart,
            earliestworktimestart, latestworktimeend);
                    
        //Act
        Contract copiedContract = new Contract(contract);
        
        //Assert
        Assert.assertNotNull(copiedContract);
        Assert.assertEquals(contract.getId(), copiedContract.getId());
        Assert.assertEquals(contract.getName(), copiedContract.getName());
        Assert.assertEquals(contract.getWorkhours(), copiedContract.getWorkhours());
        Assert.assertEquals(contract.getMaxworkhours(), copiedContract.getMaxworkhours());
        Assert.assertEquals(contract.getVacationdays(), copiedContract.getVacationdays());
        Assert.assertEquals(contract.getVacationreconciliationdate(), copiedContract.getVacationreconciliationdate());
        Assert.assertEquals(contract.getBreakfastofftimeend(), copiedContract.getBreakfastofftimeend());
        Assert.assertEquals(contract.getBreakfastofftimestart(), copiedContract.getBreakfastofftimestart());
        Assert.assertEquals(contract.getLunchofftimeend(), copiedContract.getLunchofftimeend());
        Assert.assertEquals(contract.getLunchofftimestart(), copiedContract.getLunchofftimestart());
        Assert.assertEquals(contract.getEarliestworktimestart(), copiedContract.getEarliestworktimestart());
        Assert.assertEquals(contract.getLatestworktimeend(), copiedContract.getLatestworktimeend());
    }

    @Test()
    public void T10_Requesting_HashCode_Twice_For_Same_Contract_Instance_Returns_Identical_HashCode() {
        //Arrange
        Long id = 1L;
        String name = "7 hours contract"; 
        Long workhours = 7L; 
        Long maxworkhours = 10L;
        Long vacationdays = 30L;
        String vacationreconciliationdate = "31.03"; 
        LocalTime breakfastofftimeinminutes = LocalTime.of(0, 15);
        LocalTime breakfastofftimestart = LocalTime.of(9, 0); 
        LocalTime lunchofftimeinminutes = LocalTime.of(0, 30);
        LocalTime lunchofftimestart = LocalTime.of(12, 0); 
        LocalTime earliestworktimestart = LocalTime.of(5, 0, 0);
        LocalTime latestworktimeend = LocalTime.of(22, 0, 0);  
                    
        Contract contract = new Contract(id, name, workhours, maxworkhours, 
            vacationdays, vacationreconciliationdate, breakfastofftimeinminutes, 
            breakfastofftimestart, lunchofftimeinminutes, lunchofftimestart,
            earliestworktimestart, latestworktimeend);

        //Act
        Integer hashCode_FirstCall = contract.hashCode();
        Integer hashCode_SecondCall = contract.hashCode();
        
        //Assert
        Assert.assertEquals(hashCode_FirstCall, hashCode_SecondCall);
    }
    
    @Test()
    public void T11_Requesting_HashCode_For_Different_Contract_Instance_Returns_Different_HashCode() {
        //Arrange
        //Arrange
        String name = "7 hours contract"; 
        Long workhours = 7L; 
        Long maxworkhours = 10L;
        Long vacationdays = 30L;
        String vacationreconciliationdate = "31.03"; 
        LocalTime breakfastofftimeinminutes = LocalTime.of(0, 15);
        LocalTime breakfastofftimestart = LocalTime.of(9, 0); 
        LocalTime lunchofftimeinminutes = LocalTime.of(0, 30);
        LocalTime lunchofftimestart = LocalTime.of(12, 0); 
        LocalTime earliestworktimestart = LocalTime.of(5, 0, 0);
        LocalTime latestworktimeend = LocalTime.of(22, 0, 0);  
                    
        Contract contract1 = new Contract(1L, name, workhours, maxworkhours, 
            vacationdays, vacationreconciliationdate, breakfastofftimeinminutes, 
            breakfastofftimestart, lunchofftimeinminutes, lunchofftimestart,
            earliestworktimestart, latestworktimeend);

                    
        Contract contract2 = new Contract(2L, name, workhours, maxworkhours, 
            vacationdays, vacationreconciliationdate, breakfastofftimeinminutes, 
            breakfastofftimestart, lunchofftimeinminutes, lunchofftimestart,
            earliestworktimestart, latestworktimeend);
        
        //Act
        Integer hashCode1 = contract1.hashCode();
        Integer hashCode2 = contract2.hashCode();

        //Assert
        Assert.assertNotEquals(hashCode2, hashCode1);
    }
    
    @Test()
    public void T20_Compare_Equal_Contracts_Returns_True() {
        //Arrange
        //Arrange
        Long id = 1L;
        String name = "7 hours contract"; 
        Long workhours = 7L; 
        Long maxworkhours = 10L;
        Long vacationdays = 30L;
        String vacationreconciliationdate = "31.03"; 
        LocalTime breakfastofftimeinminutes = LocalTime.of(0, 15);
        LocalTime breakfastofftimestart = LocalTime.of(9, 0); 
        LocalTime lunchofftimeinminutes = LocalTime.of(0, 30);
        LocalTime lunchofftimestart = LocalTime.of(12, 0); 
        LocalTime earliestworktimestart = LocalTime.of(5, 0, 0);
        LocalTime latestworktimeend = LocalTime.of(22, 0, 0);  
                    
        Contract contract1 = new Contract(id, name, workhours, maxworkhours, 
            vacationdays, vacationreconciliationdate, breakfastofftimeinminutes, 
            breakfastofftimestart, lunchofftimeinminutes, lunchofftimestart,
            earliestworktimestart, latestworktimeend);
        
        Contract contract2 = new Contract(id, name, workhours, maxworkhours, 
            vacationdays, vacationreconciliationdate, breakfastofftimeinminutes, 
            breakfastofftimestart, lunchofftimeinminutes, lunchofftimestart,
            earliestworktimestart, latestworktimeend);
        
        //Act
        boolean result = contract1.equals(contract2);
        
        //Assert
        Assert.assertTrue(result);
    }

    @Test()
    public void T21_Compare_Different_Contracts_Returns_False() {
        //Arrange
        List<Pair<Contract, Contract>> pairList = new ArrayList<>(); 
        pairList.add(
            new Pair(
                new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0)),
                new Contract(2L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0))
            )
        );
        pairList.add(
            new Pair(
                new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0)),
                new Contract(1L, "8 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0))
            )
        );
        pairList.add(
            new Pair(
                new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0)),
                new Contract(1L, "7 hours contract", 8L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0))
            )
        );
        pairList.add(
            new Pair(
                new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0)),
                new Contract(1L, "7 hours contract", 7L, 11L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0))
            )
        );
        pairList.add(
            new Pair(
                new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0)),
                new Contract(1L, "7 hours contract", 7L, 10L, 31L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0))
            )
        );
        pairList.add(
            new Pair(
                new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0)),
                new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.04", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0))
            )
        );
        pairList.add(
            new Pair(
                new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0)),
                new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 30), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0))
            )
        );
        pairList.add(
            new Pair(
                new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0)),
                new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(10, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0))
            )
        );
        pairList.add(
            new Pair(
                new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0)),
                new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 45), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0))
            )
        );
        pairList.add(
            new Pair(
                new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0)),
                new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(13, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0))
                )
        );
        pairList.add(
            new Pair(
                new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0)),
                new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(6, 0, 0), LocalTime.of(22, 0, 0))
            )
        );
        pairList.add(
            new Pair(
                new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0)),
                new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(23, 0, 0))
            )
        );
        
        for(Pair<Contract, Contract> pair : pairList) {
            Contract contract1 = pair.getKey();
            Contract contract2 = pair.getValue();
            
            //Act
            boolean result = contract1.equals(contract2);

            //Assert
            Assert.assertFalse(result);
        }
    }

    @Test()
    public void T30_Print_Contract_PrintsResults_In_Expected_PrintFormat() {
        //Arrange
        String expectedPrintResult = "7 hours contract";
        
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        
        //Act
        String printResult = contract.toString();
        
        //Assert
        Assert.assertEquals(expectedPrintResult, printResult);   
    }
    
}
