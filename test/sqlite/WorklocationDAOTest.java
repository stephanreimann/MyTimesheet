/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sqlite;

import model.Worklocation;
import adapter.Log4jAdapter;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import service.PropertiesService;

/**
 *
 * @author adrest18
 */
public class WorklocationDAOTest {
    
    private final static String APP_LANGUAGE_RESOURCE_KEY = "Language";
    private final static String APP_LANGUAGE_DEFAULT_VALUE = "en";
    private final static String LANGUAGE_RESOURCE = "languages.bundle";
    private final static String DATABASE_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/sqlite/testdb.sqlite";
    private final static String LOG4J2_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/log4j2.xml";
    
    private static ResourceBundle bundle;
    private static ConnectionFactory connectionFactory;
    private static Connection connection;
    
    public WorklocationDAOTest() throws FileNotFoundException { 
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
        WorklocationDAO worklocationDAO = new WorklocationDAO(null);
    }
    
    @Test()
    public void T01_Calling_Ctor_With_Valid_Connection_Returns_Initialized_Instance() {
        //Arrange
        //Act
        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);
        
        //Assert
        org.junit.Assert.assertNotNull(worklocationDAO);
    }
 
    @Test()
    public void T10_Calling_SelectAll_Returns_All_Found_Worklocations() throws SQLException {
        //Arrange
        //Act
        Worklocation worklocation1 = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        Worklocation worklocation2 = new Worklocation(2L, "Erlangen", "Worklocation is Erlangen");

        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);
        worklocationDAO.create(worklocation1);
        worklocationDAO.create(worklocation2);
        
        //Act
        List<Worklocation> worklocationList = worklocationDAO.selectAll();

        //Assert
        org.junit.Assert.assertEquals(2, worklocationList.size());
        org.junit.Assert.assertEquals(worklocation1, worklocationList.get(0));
        org.junit.Assert.assertEquals(worklocation2, worklocationList.get(1));
    }

    @Test()
    public void T11_Calling_SelectAll_Returns_EmptyList_If_No_Worklocations_Found() throws SQLException {
        //Arrange
        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);
        
        //Act
        List<Worklocation> worklocationList = worklocationDAO.selectAll();

        //Assert
        org.junit.Assert.assertTrue(worklocationList.isEmpty());
    }
    
    @Test()
    public void T20_Calling_SelectRoleFromId_Returns_The_Stored_Role() throws SQLException {
        //Arrange
        Worklocation worklocation1 = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        Worklocation worklocation2 = new Worklocation(2L, "Erlangen", "Worklocation is Erlangen");

        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);
        worklocationDAO.create(worklocation1);
        worklocationDAO.create(worklocation2);
        
        //Act
        Worklocation worklocation = worklocationDAO.selectWorklocationFromId(1L);

        //Assert
        Assert.assertEquals(worklocation1, worklocation);
    }

    @Test()
    public void T21_Calling_SelectRoleFromId_Returns_Null_If_Role_NotFound() throws SQLException {
        //Arrange
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");

        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);
        worklocationDAO.create(worklocation);
        
        //Act
        Worklocation result = worklocationDAO.selectWorklocationFromId(2L);

        //Assert
        Assert.assertNull(result);
    }
    
    @Test(expected = NullPointerException.class)
    public void T30_Calling_Create_Role_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);

        //Act
        boolean result = worklocationDAO.create(null);

        //Assert
        org.junit.Assert.assertFalse(result);
    }
    
    @Test()
    public void T31_Calling_Create_Stores_Worklocation() throws SQLException {
        //Arrange
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");

        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);
        
        //Act
        boolean result = worklocationDAO.create(worklocation);
        
        //Assert
        Assert.assertTrue(result);
    }
    
    @Test(expected = SQLException.class)
    public void T32_Calling_Create_Worklocation_AllreadyExists_Throws_SQLException() throws SQLException {
        //Arrange
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");

        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);
        worklocationDAO.create(worklocation);

        //Act
        //Assert
        boolean result = worklocationDAO.create(worklocation);        
    }
    
    @Test(expected = NullPointerException.class)
    public void T40_Calling_Update_OriginalWorklocation_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        Worklocation modifiedWorklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");

        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);

        //Act
        boolean result = worklocationDAO.update(null, modifiedWorklocation);

        //Assert
        Assert.assertFalse(result);
    }
    
    @Test(expected = NullPointerException.class)
    public void T41_Calling_Update_ModifiedWorklocation_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        Worklocation originalWorklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");

        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);

        //Act
        //Assert
        boolean result = worklocationDAO.update(originalWorklocation, null);
    }
    
    @Test()
    public void T42_Calling_Update_Updates_OriginalRole() throws SQLException {
        //Arrange
        Worklocation originalWorklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        Worklocation modifiedWorklocation = new Worklocation(1L, "Erlangen", "Worklocation is Erlangen");

        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);
        worklocationDAO.create(originalWorklocation);

        //Act
        boolean result = worklocationDAO.update(originalWorklocation, modifiedWorklocation);
        Worklocation worklocationResult = worklocationDAO.selectWorklocationFromId(modifiedWorklocation.getId());
        
        //Assert
        Assert.assertTrue(result);
        Assert.assertEquals(modifiedWorklocation, worklocationResult);
    }
    
    @Test()
    public void T43_Calling_Update_OriginalWorklocation_DoesNotExists_Returns_False() throws SQLException {
        //Arrange
        Worklocation originalWorklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        Worklocation modifiedWorklocation = new Worklocation(2L, "Erlangen", "Worklocation is Erlangen");

        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);
        
        //Act
        boolean result = worklocationDAO.update(originalWorklocation, modifiedWorklocation);
        
        //Assert
        Assert.assertFalse(result);
    }
   
    @Test()
    public void T44_Calling_Update_On_Different_Worklocation_DoenNotChange_Worklocation() throws SQLException {
        //Arrange
        Worklocation originalWorklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        Worklocation modifiedWorklocation = new Worklocation(2L, "Erlangen", "Worklocation is Erlangen");

        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);
        worklocationDAO.create(originalWorklocation);

        //Act
        boolean result = worklocationDAO.update(originalWorklocation, modifiedWorklocation);
        Worklocation worklocationResult = worklocationDAO.selectWorklocationFromId(originalWorklocation.getId());

        //Assert
        Assert.assertFalse(result);
        Assert.assertEquals(originalWorklocation, worklocationResult);
    }
    
    @Test(expected = NullPointerException.class)
    public void T50_Calling_Delete_Worklocation_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);

        //Act
        //Assert
        boolean result = worklocationDAO.delete(null);
    }
    
    @Test()
    public void T51_Calling_Delete_Worklocation_DoesNotExists_Returns_False() throws SQLException {
        //Arrange
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");

        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);

        //Act
        boolean result = worklocationDAO.delete(worklocation);

        //Assert
        Assert.assertFalse(result);
    }

    @Test()
    public void T52_Calling_Delete_Worklocation_Exists_Deletes_Contract() throws SQLException {
        //Arrange
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");

        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);
        worklocationDAO.create(worklocation);
        
        //Act
        boolean result = worklocationDAO.delete(worklocation);
        Worklocation worklocationResult = worklocationDAO.selectWorklocationFromId(worklocation.getId());

        //Assert
        Assert.assertTrue(result);
        Assert.assertNull(worklocationResult);
    }
  
    @Test()
    public void T60_Calling_GetNextId_On_WorklocationTable_Containing_One_Record_Returns_2() throws SQLException {
        //Arrange
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");

        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);
        worklocationDAO.create(worklocation);
        
        //Act
        long receivedId = worklocationDAO.getNextId();
        
        //Assert
        Assert.assertEquals(2L, receivedId);
    }
    
    @Test()
    public void T61_Calling_GetNextId_Twice_On_WorklocationTable_Returns_SameId() throws SQLException {
        //Arrange
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");

        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);
        worklocationDAO.create(worklocation);
        
        //Act
        long receivedIdFirstCall = worklocationDAO.getNextId();
        long receivedIdSecondCall = worklocationDAO.getNextId();
        
        //Assert
        Assert.assertEquals(receivedIdSecondCall, receivedIdFirstCall);
    }

    @Test
    public void T62_Calling_GetNextId_On_Truncated_Sqlite_Sequence_Table_Returns_0() throws SQLException {
        //Arrange
        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);
        
        //Act
        long resultId = worklocationDAO.getNextId();

        //Assert
        Assert.assertEquals(0L, resultId);
    }
    
    private synchronized boolean truncateTable() {
        boolean result = false;
        StringBuilder statement = new StringBuilder();
        statement.append("DELETE FROM worklocation ");
        
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }
        
        statement = new StringBuilder();        
        //statement.append("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name='Worklocation'");
        statement.append("DELETE FROM SQLITE_SEQUENCE WHERE name='Worklocation'");
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }
        return result;
    }
    
}
