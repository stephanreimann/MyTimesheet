/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sqlite;

import model.Role;
import adapter.Log4jAdapter;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.*;
import org.junit.*;
import service.PropertiesService;

/**
 *
 * @author adrest18
 */
public class RoleDAOTest {
    
    private final static String APP_LANGUAGE_RESOURCE_KEY = "Language";
    private final static String APP_LANGUAGE_DEFAULT_VALUE = "en";
    private final static String LANGUAGE_RESOURCE = "languages.bundle";
    private final static String DATABASE_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/sqlite/testdb.sqlite";
    private final static String LOG4J2_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/log4j2.xml";
    
    private static ResourceBundle bundle;
    private static ConnectionFactory connectionFactory;
    private static Connection connection;
    
    public RoleDAOTest() throws FileNotFoundException { 
        bundle = ResourceBundle.getBundle(LANGUAGE_RESOURCE, new Locale(PropertiesService.getInstance().getProperty(APP_LANGUAGE_RESOURCE_KEY, APP_LANGUAGE_DEFAULT_VALUE)));    
        connectionFactory = new ConnectionFactory(bundle, new Log4jAdapter(LOG4J2_PATH_AND_FULL_NAME));
        connection = connectionFactory.getConnection(DATABASE_PATH_AND_FULL_NAME);   
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
        truncateTable();
    }

    @Test(expected = NullPointerException.class)
    public void T00_Calling_Ctor_With_ConnectionIsNull_Throws_NullPointerException() {
        //Arrange
        //Act
        //Assert
        RoleDAO roleDAO = new RoleDAO(null);
    }
    
    @Test()
    public void T01_Calling_Ctor_With_Valid_Connection_Returns_Initialized_Instance() {
        //Arrange
        //Act
        RoleDAO roleDAO = new RoleDAO(connection);
        
        //Assert
        Assert.assertNotNull(roleDAO);
    }

    @Test()
    public void T10_Calling_SelectAll_Returns_All_Found_Roles() throws SQLException {
        //Arrange
        //Act
        Role role1 = new Role(1L, "Admin", "The Administrator role has access to all application features");
        Role role2 = new Role(2L, "User", "The User role has access to all user data");

        RoleDAO roleDAO = new RoleDAO(connection);
        roleDAO.create(role1);
        roleDAO.create(role2);
        
        //Act
        List<Role> roleList = roleDAO.selectAll();

        //Assert
        Assert.assertEquals(2, roleList.size());
        Assert.assertEquals(role1, roleList.get(0));
        Assert.assertEquals(role2, roleList.get(1));
    }
    
    @Test()
    public void T11_Calling_SelectAll_Returns_EmptyList_If_No_Roles_Found() throws SQLException {
        //Arrange
        RoleDAO roleDAO = new RoleDAO(connection);
        
        //Act
        List<Role> roleList = roleDAO.selectAll();

        //Assert
        Assert.assertTrue(roleList.isEmpty());
    }

    @Test()
    public void T20_Calling_SelectRoleFromId_Returns_The_Stored_Role() throws SQLException {
        //Arrange
        Role role1 = new Role(1L, "Admin", "The Administrator role has access to all application features");
        Role role2 = new Role(2L, "User", "The User role has access to all user data");

        RoleDAO roleDAO = new RoleDAO(connection);
        roleDAO.create(role1);
        roleDAO.create(role2);
        
        //Act
        Role role = roleDAO.selectRoleFromId(1L);

        //Assert
        Assert.assertEquals(role1, role);
    }

    @Test()
    public void T21_Calling_SelectRoleFromId_Returns_Null_If_Role_NotFound() throws SQLException {
        //Arrange
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");

        RoleDAO roleDAO = new RoleDAO(connection);
        roleDAO.create(role);
        
        //Act
        Role result = roleDAO.selectRoleFromId(2L);

        //Assert
        Assert.assertNull(result);
    }

    @Test(expected = NullPointerException.class)
    public void T30_Calling_Create_Role_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        RoleDAO roleDAO = new RoleDAO(connection);

        //Act
        boolean result = roleDAO.create(null);

        //Assert
        Assert.assertFalse(result);
    }

    @Test()
    public void T31_Calling_Create_Stores_Role() throws SQLException {
        //Arrange
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");

        RoleDAO roleDAO = new RoleDAO(connection);
        
        //Act
        boolean result = roleDAO.create(role);
        
        //Assert
        Assert.assertTrue(result);
    }
    
    @Test(expected = SQLException.class)
    public void T32_Calling_Create_Role_AllreadyExists_Throws_SQLException() throws SQLException {
        //Arrange
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");

        RoleDAO roleDAO = new RoleDAO(connection);
        roleDAO.create(role);

        //Act
        //Assert
        boolean result = roleDAO.create(role);        
    }

    @Test(expected = NullPointerException.class)
    public void T40_Calling_Update_OriginalRole_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        Role modifiedRole = new Role(1L, "Admin", "The Administrator role has access to all application features");

        RoleDAO roleDAO = new RoleDAO(connection);

        //Act
        boolean result = roleDAO.update(null, modifiedRole);

        //Assert
        Assert.assertFalse(result);
    }

    @Test(expected = NullPointerException.class)
    public void T41_Calling_Update_ModifiedRole_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        Role originalRole = new Role(1L, "Admin", "The Administrator role has access to all application features");

        RoleDAO roleDAO = new RoleDAO(connection);

        //Act
        //Assert
        boolean result = roleDAO.update(originalRole, null);
    }

    @Test()
    public void T42_Calling_Update_Updates_OriginalRole() throws SQLException {
        //Arrange
        Role originalRole = new Role(1L, "Admin", "The Administrator role has access to all application features");
        Role modifiedRole = new Role(1L, "User", "The User role has access to all user data");

        RoleDAO roleDAO = new RoleDAO(connection);
        roleDAO.create(originalRole);
        
        //Act
        boolean result = roleDAO.update(originalRole, modifiedRole);
        Role roleResult = roleDAO.selectRoleFromId(modifiedRole.getId());
        
        //Assert
        Assert.assertTrue(result);
        Assert.assertEquals(modifiedRole, roleResult);
    }

    @Test()
    public void T43_Calling_Update_OriginalRole_DoesNotExists_Returns_False() throws SQLException {
        //Arrange
        Role originalRole = new Role(1L, "Admin", "The Administrator role has access to all application features");
        Role modifiedRole = new Role(1L, "User", "The User role has access to all user data");

        RoleDAO roleDAO = new RoleDAO(connection);
        
        //Act
        boolean result = roleDAO.update(originalRole, modifiedRole);
        
        //Assert
        Assert.assertFalse(result);
    }
    
    @Test()
    public void T44_Calling_Update_On_Different_Role_DoenNotChange_Role() throws SQLException {
        //Arrange
        Role originalRole = new Role(1L, "Admin", "The Administrator role has access to all application features");
        Role modifiedRole = new Role(2L, "User", "The User role has access to all user data");

        RoleDAO roleDAO = new RoleDAO(connection);
        roleDAO.create(originalRole);

        //Act
        boolean result = roleDAO.update(originalRole, modifiedRole);
        Role roleResult = roleDAO.selectRoleFromId(originalRole.getId());

        //Assert
        Assert.assertFalse(result);
        Assert.assertEquals(originalRole, roleResult);
    }

    @Test(expected = NullPointerException.class)
    public void T50_Calling_Delete_Role_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        RoleDAO roleDAO = new RoleDAO(connection);

        //Act
        //Assert
        boolean result = roleDAO.delete(null);
    }
    
    @Test()
    public void T51_Calling_Delete_Role_DoesNotExists_Returns_False() throws SQLException {
        //Arrange
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");

        RoleDAO roleDAO = new RoleDAO(connection);

        //Act
        boolean result = roleDAO.delete(role);

        //Assert
        Assert.assertFalse(result);
    }

    @Test()
    public void T52_Calling_Delete_Role_Exists_Deletes_Contract() throws SQLException {
        //Arrange
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");

        RoleDAO roleDAO = new RoleDAO(connection);
        roleDAO.create(role);
        
        //Act
        boolean result = roleDAO.delete(role);
        Role roleResult = roleDAO.selectRoleFromId(role.getId());

        //Assert
        Assert.assertTrue(result);
        Assert.assertNull(roleResult);
    }

    @Test()
    public void T60_Calling_GetNextId_On_RoleTable_Containing_One_Record_Returns_2() throws SQLException {
        //Arrange
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");

        RoleDAO roleDAO = new RoleDAO(connection);
        roleDAO.create(role);
        
        //Act
        long receivedId = roleDAO.getNextId();
        
        //Assert
        Assert.assertEquals(2L, receivedId);
    }
    
    @Test()
    public void T61_Calling_GetNextId_Twice_On_RoleTable_Returns_SameId() throws SQLException {
        //Arrange
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");

        RoleDAO roleDAO = new RoleDAO(connection);
        roleDAO.create(role);
        
        //Act
        long receivedIdFirstCall = roleDAO.getNextId();
        long receivedIdSecondCall = roleDAO.getNextId();
        
        //Assert
        Assert.assertEquals(receivedIdSecondCall, receivedIdFirstCall);
    }

    @Test
    public void T62_Calling_GetNextId_On_Truncated_Sqlite_Sequence_Table_Returns_0() throws SQLException {
        //Arrange
        RoleDAO roleDAO = new RoleDAO(connection);
        
        //Act
        long resultId = roleDAO.getNextId();

        //Assert
        Assert.assertEquals(0L, resultId);
    }

    private synchronized boolean truncateTable() {
        boolean result = false;
        StringBuilder statement = new StringBuilder();
        statement.append("DELETE FROM role ");
        
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }
        
        statement = new StringBuilder();        
        //statement.append("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name='Role'");
        statement.append("DELETE FROM SQLITE_SEQUENCE WHERE name='Role'");
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }
        return result;
    }
    
}
