/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sqlite;

import model.Address;
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
public class AddressDAOTest {
    
    private final static String APP_LANGUAGE_RESOURCE_KEY = "Language";
    private final static String APP_LANGUAGE_DEFAULT_VALUE = "en";
    private final static String LANGUAGE_RESOURCE = "languages.bundle";
    private final static String DATABASE_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/sqlite/testdb.sqlite";
    private final static String LOG4J2_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/log4j2.xml";
    
    private static ResourceBundle bundle;
    private static ConnectionFactory connectionFactory;
    private static Connection connection;
    
    public AddressDAOTest() throws FileNotFoundException { 
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
        AddressDAO addressDAO = new AddressDAO(null);
    }
    
    @Test()
    public void T01_Calling_Ctor_With_Valid_Connection_Returns_Initialized_Instance() {
        //Arrange
        //Act
        AddressDAO addressDAO = new AddressDAO(connection);
        
        //Assert
        Assert.assertNotNull(addressDAO);
    }

    @Test()
    public void T10_Calling_SelectAll_Returns_All_Found_Addresses() throws SQLException {
        //Arrange
        Address address1 = new Address(1L, "Humboldtstrasse", 90L, "Etage",
            1L, "Rechts", "Nürenberg", "Bayern", 90459L, "Deutschland");
        Address address2 = new Address(2L, "Humboldtstrasse", 90L, "Etage",
            1L, "Links", "Nürenberg", "Bayern", 90459L, "Deutschland");

        AddressDAO addressDAO = new AddressDAO(connection);
        addressDAO.create(address1);
        addressDAO.create(address2);
        
        //Act
        List<Address> addressList = addressDAO.selectAll();

        //Assert
        Assert.assertEquals(2, addressList.size());
        Assert.assertEquals(address1, addressList.get(0));
        Assert.assertEquals(address2, addressList.get(1));
    }
    
    //@Test()
    public void T11_Calling_SelectAll_Returns_EmptyList_If_No_Addresses_Found() throws SQLException {
        //Arrange
        AddressDAO addressDAO = new AddressDAO(connection);
        
        //Act
        List<Address> addressList = addressDAO.selectAll();

        //Assert
        Assert.assertTrue(addressList.isEmpty());
    }

    @Test()
    public void T20_Calling_SelectAddressFromId_Returns_The_Stored_Address() throws SQLException {
        //Arrange
        Address address1 = new Address(1L, "Humboldtstrasse", 90L, "Etage",
            1L, "Rechts", "Nürenberg", "Bayern", 90459L, "Deutschland");
        Address address2 = new Address(2L, "Humboldtstrasse", 90L, "Etage",
            1L, "Links", "Nürenberg", "Bayern", 90459L, "Deutschland");

        AddressDAO addressDAO = new AddressDAO(connection);
        addressDAO.create(address1);
        addressDAO.create(address2);
        
        //Act
        Address address = addressDAO.selectAddressFromId(1L);

        //Assert
        Assert.assertEquals(address1, address);
    }

    @Test()
    public void T21_Calling_SelectAddressFromId_Returns_Null_If_Address_NotFound() throws SQLException {
        //Arrange
        Address address = new Address(1L, "Humboldtstrasse", 90L, "Etage",
            1L, "Rechts", "Nürenberg", "Bayern", 90459L, "Deutschland");

        AddressDAO addressDAO = new AddressDAO(connection);
        addressDAO.create(address);
        
        //Act
        Address result = addressDAO.selectAddressFromId(2L);

        //Assert
        Assert.assertNull(result);
    }

    @Test(expected = NullPointerException.class)
    public void T30_Calling_Create_Address_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        AddressDAO addressDAO = new AddressDAO(connection);

        //Act
        boolean result = addressDAO.create(null);

        //Assert
        Assert.assertFalse(result);
    }

    @Test()
    public void T31_Calling_Create_Stores_Address() throws SQLException {
        //Arrange
        Address address = new Address(1L, "Humboldtstrasse", 90L, "Etage",
            1L, "Rechts", "Nürenberg", "Bayern", 90459L, "Deutschland");

        AddressDAO addressDAO = new AddressDAO(connection);
        
        //Act
        boolean result = addressDAO.create(address);
        
        //Assert
        Assert.assertTrue(result);
    }
    
    @Test(expected = SQLException.class)
    public void T32_Calling_Create_Address_AllreadyExists_Throws_SQLException() throws SQLException {
        //Arrange
        Address address = new Address(1L, "Humboldtstrasse", 90L, "Etage",
            1L, "Rechts", "Nürenberg", "Bayern", 90459L, "Deutschland");

        AddressDAO addressDAO = new AddressDAO(connection);
        
        addressDAO.create(address);

        //Act
        boolean result = addressDAO.create(address);        

        //Assert
        Assert.assertFalse(result);
    }

    @Test(expected = NullPointerException.class)
    public void T40_Calling_Update_OriginalAddress_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        Address modifiedAddress = new Address(1L, "Humboldtstrasse", 90L, "Etage",
            1L, "Rechts", "Nürenberg", "Bayern", 90459L, "Deutschland");
        
        AddressDAO addressDAO = new AddressDAO(connection);

        //Act
        boolean result = addressDAO.update(null, modifiedAddress);

        //Assert
        Assert.assertFalse(result);
    }

    @Test(expected = NullPointerException.class)
    public void T41_Calling_Update_ModifiedAddress_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        Address originalAddress = new Address(1L, "Humboldtstrasse", 90L, "Etage",
            1L, "Rechts", "Nürenberg", "Bayern", 90459L, "Deutschland");
        
        AddressDAO addressDAO = new AddressDAO(connection);

        //Act
        boolean result = addressDAO.update(originalAddress, null);

        //Assert
        Assert.assertFalse(result);
    }

    @Test()
    public void T42_Calling_Update_Updates_OriginalAddress() throws SQLException {
        //Arrange
        Address originalAddress = new Address(1L, "Humboldtstrasse", 90L, "Etage",
            1L, "Rechts", "Nürenberg", "Bayern", 90459L, "Deutschland");
        Address modifiedAddress = new Address(1L, "Humboldtstrasse", 90L, "Etage",
            1L, "Links", "Nürenberg", "Bayern", 90459L, "Deutschland");
        
        AddressDAO addressDAO = new AddressDAO(connection);
        addressDAO.create(originalAddress);
        
        //Act
        boolean result = addressDAO.update(originalAddress, modifiedAddress);
        Address addressResult = addressDAO.selectAddressFromId(modifiedAddress.getId());
        
        //Assert
        Assert.assertTrue(result);
        Assert.assertEquals(modifiedAddress, addressResult);
    }

    @Test()
    public void T43_Calling_Update_OriginalAddress_DoesNotExists_Returns_False() throws SQLException {
        //Arrange
        Address originalAddress = new Address(1L, "Humboldtstrasse", 90L, "Etage",
            1L, "Rechts", "Nürenberg", "Bayern", 90459L, "Deutschland");
        Address modifiedAddress = new Address(1L, "Humboldtstrasse", 90L, "Etage",
            1L, "Links", "Nürenberg", "Bayern", 90459L, "Deutschland");
        
        AddressDAO addressDAO = new AddressDAO(connection);
        
        //Act
        boolean result = addressDAO.update(originalAddress, modifiedAddress);
        
        //Assert
        Assert.assertFalse(result);
    }
    
    @Test()
    public void T44_Calling_Update_On_Different_Addresses_DoenNotChange_Address() throws SQLException {
        //Arrange
        Address originalAddress = new Address(1L, "Humboldtstrasse", 90L, "Etage",
            1L, "Rechts", "Nürenberg", "Bayern", 90459L, "Deutschland");
        Address modifiedAddress = new Address(2L, "Humboldtstrasse", 90L, "Etage",
            1L, "Links", "Nürenberg", "Bayern", 90459L, "Deutschland");
        
        AddressDAO addressDAO = new AddressDAO(connection);
        addressDAO.create(originalAddress);

        //Act
        boolean result = addressDAO.update(originalAddress, modifiedAddress);
        Address addressResult = addressDAO.selectAddressFromId(originalAddress.getId());

        //Assert
        Assert.assertFalse(result);
        Assert.assertEquals(originalAddress, addressResult);
    }

    @Test(expected = NullPointerException.class)
    public void T50_Calling_Delete_Address_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        AddressDAO addressDAO = new AddressDAO(connection);

        //Act
        //Assert
        boolean result = addressDAO.delete(null);
    }
    
    @Test()
    public void T51_Calling_Delete_Address_DoesNotExists_Returns_False() throws SQLException {
        //Arrange
        Address address = new Address(1L, "Humboldtstrasse", 90L, "Etage",
            1L, "Rechts", "Nürenberg", "Bayern", 90459L, "Deutschland");

        AddressDAO addressDAO = new AddressDAO(connection);

        //Act
        boolean result = addressDAO.delete(address);

        //Assert
        Assert.assertFalse(result);
    }

    @Test()
    public void T52_Calling_Delete_Address_Exists_Deletes_Address() throws SQLException {
        //Arrange
        Address address = new Address(1L, "Humboldtstrasse", 90L, "Etage",
            1L, "Rechts", "Nürenberg", "Bayern", 90459L, "Deutschland");

        AddressDAO addressDAO = new AddressDAO(connection);
        addressDAO.create(address);
        
        //Act
        boolean result = addressDAO.delete(address);
        Address addressResult = addressDAO.selectAddressFromId(address.getId());

        //Assert
        Assert.assertTrue(result);
        Assert.assertNull(addressResult);
    }

    @Test()
    public void T60_Calling_GetNextId_On_AddressTable_Containing_One_Record_Returns_2() throws SQLException {
        //Arrange
        Address address = new Address(1L, "Humboldtstrasse", 90L, "Etage",
            1L, "Rechts", "Nürenberg", "Bayern", 90459L, "Deutschland");

        AddressDAO addressDAO = new AddressDAO(connection);
        addressDAO.create(address);
        
        //Act
        long receivedId = addressDAO.getNextId();
        
        //Assert
        Assert.assertEquals(2L, receivedId);
    }
    
    @Test()
    public void T61_Calling_GetNextId_Twice_On_AddressTable_Returns_SameId() throws SQLException {
        //Arrange
        Address address = new Address(1L, "Humboldtstrasse", 90L, "Etage",
            1L, "Rechts", "Nürenberg", "Bayern", 90459L, "Deutschland");

        AddressDAO addressDAO = new AddressDAO(connection);
        addressDAO.create(address);
        
        //Act
        long receivedIdFirstCall = addressDAO.getNextId();
        long receivedIdSecondCall = addressDAO.getNextId();
        
        //Assert
        Assert.assertEquals(receivedIdSecondCall, receivedIdFirstCall);
    }

    @Test
    public void T62_Calling_GetNextId_On_Truncated_Sqlite_Sequence_Table_Returns_0() throws SQLException {
        //Arrange
        AddressDAO addressDAO = new AddressDAO(connection);
        
        //Act
        long resultId = addressDAO.getNextId();

        //Assert
        Assert.assertEquals(0L, resultId);
    }

    private synchronized boolean truncateTable() {
        boolean result = false;
        StringBuilder statement = new StringBuilder();
        statement.append("DELETE FROM address ");
        
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }
        
        statement = new StringBuilder();        
        //statement.append("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name='Address'");
        statement.append("DELETE FROM SQLITE_SEQUENCE WHERE name='Address'");
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }
        return result;
    }
    
}
