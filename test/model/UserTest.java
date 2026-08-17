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
public class UserTest {
    
    public UserTest() {
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
        Long userId = 1L;
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        Address addresse = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        String firstName = "Stephan";
        String lastName = "Reimann";
        String login = "stephan";
        String password = "password";
        Long vacationLeft = 30L;
        
        //Act
        User user = new User(userId, role, addresse, contract, firstName, lastName, login, password, vacationLeft);
        
        //Assert
        Assert.assertNotNull(user);
        Assert.assertEquals(role, user.getRole());
        Assert.assertEquals(addresse, user.getAddress());
        Assert.assertEquals(contract, user.getContract());
        Assert.assertEquals(firstName, user.getFirstname());
        Assert.assertEquals(lastName, user.getLastname());
        Assert.assertEquals(login, user.getLogin());
        Assert.assertEquals(password, user.getPassword());
        Assert.assertEquals(Long.valueOf(30), user.getVacationleft());
    }

    @Test()
    public void T01_Copy_Ctor_Called_Returns_WithParameterInitialized_Instance() {
        //Arrange
        Long userId = 1L;
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        Address addresse = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        String firstName = "Stephan";
        String lastName = "Reimann";
        String login = "stephan";
        String password = "password";
        Long vacationLeft = 30L;
        User user = new User(userId, role, addresse, contract, firstName, lastName, login, password, vacationLeft);
        
        //Act
        User copiedUser = new User(user);
        
        //Assert
        Assert.assertNotNull(copiedUser);
        Assert.assertEquals(user.getRole(), copiedUser.getRole());
        Assert.assertEquals(user.getAddress(), copiedUser.getAddress());
        Assert.assertEquals(user.getContract(), copiedUser.getContract());
        Assert.assertEquals(user.getFirstname(), copiedUser.getFirstname());
        Assert.assertEquals(user.getLastname(), copiedUser.getLastname());
        Assert.assertEquals(user.getLogin(), copiedUser.getLogin());
        Assert.assertEquals(user.getPassword(), copiedUser.getPassword());
        Assert.assertEquals(user.getVacationleft(), copiedUser.getVacationleft());
    }
    
    @Test()
    public void T10_Requesting_HashCode_Twice_For_Same_User_Instance_Returns_Identical_HashCode() {
        //Arrange
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        Address addresse = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));

        User user = new User(1L, role, addresse, contract, "Stephan", "Reimann", "stephan", "password", 30L);

        //Act
        Integer hashCode_FirstCall = user.hashCode();
        Integer hashCode_SecondCall = user.hashCode();
        
        //Assert
        Assert.assertEquals(hashCode_FirstCall, hashCode_SecondCall);
    }
    
    @Test()
    public void T11_Requesting_HashCode_For_Different_User_Instance_Returns_Different_HashCode() {
        //Arrange
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        Address addresse = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));

        User user1 = new User(1L, role, addresse, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        User user2 = new User(2L, role, addresse, contract, "Stephan", "Reimann", "stephan", "password", 30L);

        //Act
        Integer hashCode1 = user1.hashCode();
        Integer hashCode2 = user2.hashCode();

        //Assert
        Assert.assertNotEquals(hashCode2, hashCode1);
    }
    
    @Test()
    public void T20_Compare_Equal_Users_Returns_True() {
        //Arrange
        Long userId = 1L;
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        Address addresse = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));

        User user1 = new User(userId, role, addresse, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        
        User user2 = new User(userId, role, addresse, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        
        //Act
        boolean result = user1.equals(user2);
        
        //Assert
        Assert.assertTrue(result);
    }

    @Test()
    @SuppressWarnings("unchecked")
    public void T21_Compare_Different_Users_Returns_False() {
        //Arrange
        Long userId = 1L;
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        Address addresse = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));

        List<Pair<User, User>> pairList = new ArrayList<>(); 
        pairList.add(
            new Pair(
                new User(userId, null, addresse, contract, "Stephan", "Reimann", "stephan", "password", 30L),
                new User(userId, role, addresse, contract, "Gabi", "Reimann", "stephan", "password", 30L)
            )
        );
        pairList.add(
            new Pair(
                new User(userId, role, null, contract, "Stephan", "Reimann", "stephan", "password", 30L),
                new User(userId, role, addresse, contract, "Gabi", "Reimann", "stephan", "password", 30L)
            )
        );
        pairList.add(
            new Pair(
                new User(userId, role, addresse, null, "Stephan", "Reimann", "stephan", "password", 30L),
                new User(userId, role, addresse, contract, "Gabi", "Reimann", "stephan", "password", 30L)
            )
        );
        pairList.add(
            new Pair(
                new User(userId, role, addresse, contract, "Stephan", "Reimann", "stephan", "password", 30L),
                new User(userId, role, addresse, contract, "Gabi", "Reimann", "stephan", "password", 30L)
            )
        );
        pairList.add(
            new Pair(
                new User(userId, role, addresse, contract, "Stephan", "Reimann", "stephan", "password", 30L),
                new User(userId, role, addresse, contract, "Stephan", "Golibrzuch", "stephan", "password", 30L)
            )
        );
        pairList.add(
            new Pair(
                new User(userId, role, addresse, contract, "Stephan", "Reimann", "stephan", "password", 30L),
                new User(userId, role, addresse, contract, "Stephan", "Reimann", "gabi", "password", 30L)
            )
        );
        pairList.add(
            new Pair(
                new User(userId, role, addresse, contract, "Stephan", "Reimann", "stephan", "password", 30L),
                new User(userId, role, addresse, contract, "Stephan", "Reimann", "stephan", "xxxx", 30L)
            )
        );
        pairList.add(
            new Pair(
                new User(userId, role, addresse, contract, "Stephan", "Reimann", "stephan", "password", 30L),
                new User(userId, role, addresse, contract, "Stephan", "Reimann", "stephan", "password", 0L)
            )
        );
        
        for(Pair<User, User> pair : pairList) {
            User user1 = pair.getKey();
            User user2 = pair.getValue();
            
            //Act
            boolean result = user1.equals(user2);

            //Assert
            Assert.assertFalse(result);
        }
    }

    @Test()
    public void T30_Print_User_PrintsResults_In_Expected_PrintFormat() {
        //Arrange
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        Address addresse = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));

        String expectedPrintResult = "Reimann";
        
        User user = new User(1l, role, addresse, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        
        //Act
        String printResult = user.toString();
        
        //Assert
        Assert.assertEquals(expectedPrintResult.toString(), printResult);   
    }
    
}
