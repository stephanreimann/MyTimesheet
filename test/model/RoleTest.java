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
 * @author adrest18
 */
@SuppressWarnings("unchecked")
public class RoleTest {
    
    public RoleTest() {
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
        String name = "Admin";
        String description = "The Administrator role has access to all application features";
        
        //Act
        Role role = new Role(id, name, description);
        
        //Assert
        Assert.assertNotNull(role);
        Assert.assertEquals(Long.valueOf(1), role.getId());
        Assert.assertEquals(name, role.getName());
        Assert.assertEquals(description, role.getDescription());
    }
    
    @Test()
    public void T01_Copy_Ctor_Called_Returns_WithParameterInitialized_Instance() {
        //Arrange
        Long id = 1L;
        String name = "Admin";
        String description = "The Administrator role has access to all application features";
        Role role = new Role(id, name, description);
        
        //Act
        Role copiedRole = new Role(role);
        
        //Assert
        Assert.assertNotNull(copiedRole);
        Assert.assertEquals(role.getId(), copiedRole.getId());
        Assert.assertEquals(role.getName(), copiedRole.getName());
        Assert.assertEquals(role.getDescription(), copiedRole.getDescription());
    }

    @Test()
    public void T10_Requesting_HashCode_Twice_For_Same_Role_Instance_Returns_Identical_HashCode() {
        //Arrange
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");

        //Act
        Integer hashCode_FirstCall = role.hashCode();
        Integer hashCode_SecondCall = role.hashCode();
        
        //Assert
        Assert.assertEquals(hashCode_FirstCall, hashCode_SecondCall);
    }
    
    @Test()
    public void T11_Requesting_HashCode_For_Different_Role_Instance_Returns_Different_HashCode() {
        //Arrange
        Role role1 = new Role(1L, "Admin", "The Administrator role has access to all application features");
        Role role2 = new Role(2L, "Admin", "The Administrator role has access to all application features");

        //Act
        Integer hashCode1 = role1.hashCode();
        Integer hashCode2 = role2.hashCode();

        //Assert
        Assert.assertNotEquals(hashCode2, hashCode1);
    }
    
    @Test()
    public void T20_Compare_Equal_Roles_Returns_True() {
        //Arrange
        List<Pair<Role, Role>> pairList = new ArrayList<>(); 
        pairList.add(
            new Pair(
                new Role(1L, "Admin", "The Administrator role has access to all application features"),
                new Role(1L, "Admin", "The Administrator role has access to all application features")
            )
        );
        
        //Act
        for(Pair<Role, Role> pair : pairList) {
            Role role1 = pair.getKey();
            Role role2 = pair.getValue();
            
            //Act
            boolean result = role1.equals(role2);
        
            //Assert
            Assert.assertTrue(result);
        }
    }

    @Test()
    public void T21_Compare_Different_Roles_Returns_False() {
        //Arrange
        List<Pair<Role, Role>> pairList = new ArrayList<>(); 
        pairList.add(
            new Pair(
                new Role(1L, "Admin", "The Administrator role has access to all application features"),
                new Role(2L, "Admin", "The Administrator role has access to all application features")
            )
        );
        pairList.add(
            new Pair(
                new Role(1L, "Admin", "The Administrator role has access to all application features"),
                new Role(1L, "User", "The Administrator role has access to all application features")
            )
        );
        pairList.add(
            new Pair(
                new Role(1L, "Admin", "The Administrator role has access to all application features"),
                new Role(1L, "Admin", "Administrator role has access to all application features")
            )
        );
        
        for(Pair<Role, Role> pair : pairList) {
            Role role1 = pair.getKey();
            Role role2 = pair.getValue();
            
            //Act
            boolean result = role1.equals(role2);

            //Assert
            Assert.assertFalse(result);
        }
    }

    @Test()
    public void T30_Print_Role_PrintsResults_In_Expected_PrintFormat() {
        //Arrange
        String expectedPrintResult = "Admin";
        
        Long id = 1L;
        String name = "Admin";
        String description = "The Administrator role has access to all application features";

        Role role = new Role(id, name, description);
        
        //Act
        String printResult = role.toString();
        
        //Assert
        Assert.assertEquals(expectedPrintResult, printResult);   
    }
    
}
