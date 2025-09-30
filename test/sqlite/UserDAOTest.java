/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sqlite;

import model.User;
import model.Role;
import model.Address;
import model.Contract;
import adapter.Log4jAdapter;
import java.io.FileNotFoundException;
import java.sql.*;
import java.time.LocalTime;
import java.util.*;
import org.junit.*;
import service.PropertiesService;

/**
 *
 * @author adrest18
 */
public class UserDAOTest {
    
    private final static String APP_LANGUAGE_RESOURCE_KEY = "Language";
    private final static String APP_LANGUAGE_DEFAULT_VALUE = "en";
    private final static String LANGUAGE_RESOURCE = "languages.bundle";
    private final static String DATABASE_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/sqlite/testdb.sqlite";
    private final static String LOG4J2_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/log4j2.xml";
    
    private static ResourceBundle bundle;
    private static ConnectionFactory connectionFactory;
    private static Connection connection;
    
    public UserDAOTest() throws FileNotFoundException { 
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
        UserDAO userDAO = new UserDAO(null);
    }
    
    @Test()
    public void T01_Calling_Ctor_With_Valid_Connection_Returns_Initialized_Instance() {
        //Arrange
        //Act
        UserDAO userDAO = new UserDAO(connection);
        
        //Assert
        Assert.assertNotNull(userDAO);
    }

    @Test()
    public void T10_Calling_SelectAll_Returns_All_Found_User() throws SQLException {
        //Arrange
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        RoleDAO roleDAO = new RoleDAO(connection);
        roleDAO.create(role);

        Address address = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        AddressDAO addressDAO = new AddressDAO(connection);
        addressDAO.create(address);

        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L ,30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        ContractDAO contractDAO = new ContractDAO(connection);
        contractDAO.create(contract);
        
        User user1 = new User(1L, role, address, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        User user2 = new User(2L, role, address, contract, "Gabi", "Golibrzuch", "gabi", "password", 30L);

        UserDAO userDAO = new UserDAO(connection);
        userDAO.create(user1);
        userDAO.create(user2);
        
        //Act
        List<User> userList = userDAO.selectAll();

        //Assert
        Assert.assertEquals(2, userList.size());
        Assert.assertEquals(user1, userList.get(0));
        Assert.assertEquals(user2, userList.get(1));
    }
    
    @Test()
    public void T11_Calling_SelectAll_Returns_EmptyList_If_No_User_Found() throws SQLException {
        //Arrange
        UserDAO userDAO = new UserDAO(connection);
        
        //Act
        List<User> userList = userDAO.selectAll();

        //Assert
        Assert.assertTrue(userList.isEmpty());
    }

    @Test()
    public void T20_Calling_SelectContractFromId_Returns_The_Stored_User() throws SQLException {
        //Arrange
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        RoleDAO roleDAO = new RoleDAO(connection);
        roleDAO.create(role);

        Address address = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        AddressDAO addressDAO = new AddressDAO(connection);
        addressDAO.create(address);

        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L ,30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        ContractDAO contractDAO = new ContractDAO(connection);
        contractDAO.create(contract);
               
        User user1 = new User(1L, role, address, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        User user2 = new User(2L, role, address, contract, "Gabi", "Golibrzuch", "gabi", "password", 30L);

        UserDAO userDAO = new UserDAO(connection);
        userDAO.create(user1);
        userDAO.create(user2);

        //Act
        User user = userDAO.selectUserFromId(1L);

        //Assert
        Assert.assertEquals(user1, user);
    }

    @Test()
    public void T21_Calling_SelectUserFromId_Returns_Null_If_User_NotFound() throws SQLException {
        //Arrange
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        Address addresse = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L ,30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        
        User user = new User(1L, role, addresse, contract, "Stephan", "Reimann", "stephan", "password", 30L);

        UserDAO userDAO = new UserDAO(connection);
        userDAO.create(user);
        
        //Act
        User result = userDAO.selectUserFromId(2L);

        //Assert
        Assert.assertNull(result);
    }

    @Test(expected = NullPointerException.class)
    public void T30_Calling_Create_User_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        UserDAO userDAO = new UserDAO(connection);

        //Act
        //Assert
        boolean result = userDAO.create(null);
    }

    @Test()
    public void T31_Calling_Create_Stores_User() throws SQLException {
        //Arrange
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        Address addresse = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L ,30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        
        User user = new User(1L, role, addresse, contract, "Stephan", "Reimann", "stephan", "password", 30L);

        UserDAO userDAO = new UserDAO(connection);
        
        //Act
        boolean result = userDAO.create(user);
        
        //Assert
        Assert.assertTrue(result);
    }
    
    @Test(expected = SQLException.class)
    public void T32_Calling_Create_User_AllreadyExists_Throws_SQLException() throws SQLException {
        //Arrange
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        Address addresse = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L ,30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        
        User user = new User(1L, role, addresse, contract, "Stephan", "Reimann", "stephan", "password", 30L);

        UserDAO userDAO = new UserDAO(connection);
        userDAO.create(user);

        //Act
        boolean result = userDAO.create(user);        

        //Assert
        Assert.assertFalse(result);
    }

    @Test(expected = NullPointerException.class)
    public void T40_Calling_Update_OriginalUser_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        Address addresse = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L ,30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        
        User modifiedUser = new User(1L, role, addresse, contract, "Stephan", "Reimann", "stephan", "password", 30L);

        UserDAO userDAO = new UserDAO(connection);

        //Act
        boolean result = userDAO.update(null, modifiedUser);

        //Assert
        Assert.assertFalse(result);
    }

    @Test(expected = NullPointerException.class)
    public void T41_Calling_Update_ModifiedUser_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        Address addresse = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L ,30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        
        User originalUser = new User(1L, role, addresse, contract, "Stephan", "Reimann", "stephan", "password", 30L);

        UserDAO userDAO = new UserDAO(connection);

        //Act
        boolean result = userDAO.update(originalUser, null);

        //Assert
        Assert.assertFalse(result);
    }

    @Test()
    public void T42_Calling_Update_Updates_OriginalUser() throws SQLException {
        //Arrange
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        RoleDAO roleDAO = new RoleDAO(connection);
        roleDAO.create(role);

        Address address = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        AddressDAO addressDAO = new AddressDAO(connection);
        addressDAO.create(address);

        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L ,30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        ContractDAO contractDAO = new ContractDAO(connection);
        contractDAO.create(contract);
               
        User originalUser = new User(1L, role, address, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        User modifiedUser = new User(1L, role, address, contract, "Stephan", "Reimann", "Login", "password", 30L);

        UserDAO userDAO = new UserDAO(connection);
        userDAO.create(originalUser);
        
        //Act
        boolean result = userDAO.update(originalUser, modifiedUser);
        User userResult = userDAO.selectUserFromId(modifiedUser.getId());
        
        //Assert
        Assert.assertTrue(result);
        Assert.assertEquals(modifiedUser, userResult);
    }

    @Test()
    public void T43_Calling_Update_OriginalUser_DoesNotExists_Returns_False() throws SQLException {
        //Arrange
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        RoleDAO roleDAO = new RoleDAO(connection);
        roleDAO.create(role);

        Address address = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        AddressDAO addressDAO = new AddressDAO(connection);
        addressDAO.create(address);

        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L ,30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        ContractDAO contractDAO = new ContractDAO(connection);
        contractDAO.create(contract);
               
        User originalUser = new User(1L, role, address, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        User modifiedUser = new User(1L, role, address, contract, "Stephan", "Reimann", "Login", "password", 30L);

        UserDAO userDAO = new UserDAO(connection);
        
        //Act
        boolean result = userDAO.update(originalUser, modifiedUser);
        
        //Assert
        Assert.assertFalse(result);
    }
    
    @Test()
    public void T44_Calling_Update_On_Different_Users_DoenNotChange_User() throws SQLException {
        //Arrange
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        RoleDAO roleDAO = new RoleDAO(connection);
        roleDAO.create(role);

        Address address = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        AddressDAO addressDAO = new AddressDAO(connection);
        addressDAO.create(address);

        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L ,30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        ContractDAO contractDAO = new ContractDAO(connection);
        contractDAO.create(contract);
               
        
        User originalUser = new User(1L, role, address, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        User modifiedUser = new User(2L, role, address, contract, "Stephan", "Reimann", "Login", "password", 30L);

        UserDAO userDAO = new UserDAO(connection);
        userDAO.create(originalUser);

        //Act
        boolean result = userDAO.update(originalUser, modifiedUser);
        User userResult = userDAO.selectUserFromId(originalUser.getId());

        //Assert
        Assert.assertFalse(result);
        Assert.assertEquals(originalUser, userResult);
    }

    @Test(expected = NullPointerException.class)
    public void T50_Calling_Delete_User_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        UserDAO userDAO = new UserDAO(connection);

        //Act
        //Assert
        boolean result = userDAO.delete(null);
    }
    
    @Test()
    public void T51_Calling_Delete_User_DoesNotExists_Returns_False() throws SQLException {
        //Arrange
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        Address addresse = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L ,30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        
        User user = new User(1L, role, addresse, contract, "Stephan", "Reimann", "stephan", "password", 30L);

        UserDAO userDAO = new UserDAO(connection);

        //Act
        boolean result = userDAO.delete(user);

        //Assert
        Assert.assertFalse(result);
    }

    @Test()
    public void T52_Calling_Delete_User_Exists_Deletes_User() throws SQLException {
        //Arrange
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        Address addresse = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L ,30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        
        User user = new User(1L, role, addresse, contract, "Stephan", "Reimann", "stephan", "password", 30L);

        UserDAO userDAO = new UserDAO(connection);
        userDAO.create(user);
        
        //Act
        boolean result = userDAO.delete(user);
        User userResult = userDAO.selectUserFromId(user.getId());

        //Assert
        Assert.assertTrue(result);
        Assert.assertNull(userResult);
    }

    @Test()
    public void T60_Calling_GetNextId_On_UserTable_Containing_One_Record_Returns_2() throws SQLException {
        //Arrange
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        Address addresse = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L ,30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        
        User user = new User(1L, role, addresse, contract, "Stephan", "Reimann", "stephan", "password", 30L);

        UserDAO userDAO = new UserDAO(connection);
        userDAO.create(user);
        
        //Act
        long receivedId = userDAO.getNextId();
        
        //Assert
        Assert.assertEquals(2L, receivedId);
    }
    
    @Test()
    public void T61_Calling_GetNextId_Twice_On_UserTable_Returns_SameId() throws SQLException {
        //Arrange
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        Address addresse = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L ,30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        
        User user = new User(1L, role, addresse, contract, "Stephan", "Reimann", "stephan", "password", 30L);

        UserDAO userDAO = new UserDAO(connection);
        userDAO.create(user);
        
        //Act
        long receivedIdFirstCall = userDAO.getNextId();
        long receivedIdSecondCall = userDAO.getNextId();
        
        //Assert
        Assert.assertEquals(receivedIdSecondCall, receivedIdFirstCall);
    }

    @Test
    public void T62_Calling_GetNextId_On_Truncated_Sqlite_Sequence_Table_Returns_0() throws SQLException {
        //Arrange
        UserDAO userDAO = new UserDAO(connection);
        
        //Act
        long resultId = userDAO.getNextId();

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
        statement.append("DELETE FROM address ");
        
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }

        statement = new StringBuilder();
        statement.append("DELETE FROM contract ");
        
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }

        statement = new StringBuilder();
        statement.append("DELETE FROM user ");
        
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }
        
        statement = new StringBuilder();        
        statement.append("DELETE FROM SQLITE_SEQUENCE WHERE name='Role'");
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }

        statement = new StringBuilder();        
        statement.append("DELETE FROM SQLITE_SEQUENCE WHERE name='Address'");
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }

        statement = new StringBuilder();        
        statement.append("DELETE FROM SQLITE_SEQUENCE WHERE name='Contract'");
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }

        statement = new StringBuilder();        
        statement.append("DELETE FROM SQLITE_SEQUENCE WHERE name='User'");
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }
        
        return result;
    }
    
}
