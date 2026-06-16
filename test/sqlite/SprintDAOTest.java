/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sqlite;

import adapter.Log4jAdapter;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import model.Sprint;
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
public class SprintDAOTest {

    private final static String APP_LANGUAGE_RESOURCE_KEY = "Language";
    private final static String APP_LANGUAGE_DEFAULT_VALUE = "en";
    private final static String LANGUAGE_RESOURCE = "languages.bundle";
    private final static String DATABASE_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/sqlite/testdb.sqlite";
    private final static String LOG4J2_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/log4j2.xml";
    
    private static ResourceBundle bundle;
    private static ConnectionFactory connectionFactory;
    private static Connection connection;
    
    public SprintDAOTest() throws FileNotFoundException { 
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
        SprintDAO sprintDAO = new SprintDAO(null);
    }
    
    @Test()
    public void T01_Calling_Ctor_With_Valid_Connection_Returns_Initialized_Instance() {
        //Arrange
        //Act
        SprintDAO sprintDAO = new SprintDAO(connection);
        
        //Assert
        Assert.assertNotNull(sprintDAO);
    }
    
    @Test()
    public void T10_Calling_SelectAll_Returns_All_Found_Sprints() throws SQLException {
        //Arrange
        //Act
        Sprint sprint1 = new Sprint(138L, LocalDate.of(2016, Month.JUNE, 10), LocalDate.of(2016, Month.JUNE, 23), 10);

        SprintDAO sprintDAO = new SprintDAO(connection);
        sprintDAO.create(sprint1);
        
        //Act
        List<Sprint> sprintList = sprintDAO.selectAll();

        //Assert
        Assert.assertEquals(1, sprintList.size());
        Assert.assertEquals(sprint1, sprintList.get(0));
    }
    
    @Test()
    public void T11_Calling_SelectAll_Returns_EmptyList_If_No_Sprint_Found() throws SQLException {
        //Arrange
        SprintDAO sprintDAO = new SprintDAO(connection);
        
        //Act
        List<Sprint> sprintList = sprintDAO.selectAll();

        //Assert
        Assert.assertTrue(sprintList.isEmpty());
    }
  
    @Test()
    public void T20_Calling_SelectTrackingItemFromId_Returns_The_Stored_TrackingItem() throws SQLException {
        //Arrange
        Sprint sprint1 = new Sprint(138L, LocalDate.of(2016, Month.JUNE, 10), LocalDate.of(2016, Month.JUNE, 23), 10);
        Sprint sprint2 = new Sprint(139L, LocalDate.of(2016, Month.JUNE, 24), LocalDate.of(2016, Month.JULY, 7), 10);

        SprintDAO sprintDAO = new SprintDAO(connection);
        sprintDAO.create(sprint1);
        sprintDAO.create(sprint2);
        
        //Act
        Sprint sprintItem = sprintDAO.selectSprintFromId(138L);

        //Assert
        Assert.assertEquals(sprint1, sprintItem);
    }
   
    @Test()
    public void T21_Calling_SelectTrackingItemFromId_Returns_Null_If_TrackingItem_NotFound() throws SQLException {
        //Arrange
        Sprint sprint1 = new Sprint(138L, LocalDate.of(2016, Month.JUNE, 10), LocalDate.of(2016, Month.JUNE, 23), 10);

        SprintDAO sprintDAO = new SprintDAO(connection);
        sprintDAO.create(sprint1);
        
        //Act
        Sprint sprintItem = sprintDAO.selectSprintFromId(139L);

        //Assert
        Assert.assertNull(sprintItem);
    }
    
    @Test(expected = NullPointerException.class)
    public void T30_Calling_Create_TrackingItem_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        SprintDAO sprintDAO = new SprintDAO(connection);

        //Act
        boolean result = sprintDAO.create(null);

        //Assert
        Assert.assertFalse(result);
    }

    @Test()
    public void T31_Calling_Create_Stores_TrackingItem() throws SQLException {
        //Arrange
        Sprint sprint1 = new Sprint(138L, LocalDate.of(2016, Month.JUNE, 10), LocalDate.of(2016, Month.JUNE, 23), 10);

        SprintDAO sprintDAO = new SprintDAO(connection);
        
        //Act
        boolean result = sprintDAO.create(sprint1);
        
        //Assert
        Assert.assertTrue(result);
    }
    
    @Test(expected = SQLException.class)
    public void T32_Calling_Create_TrackingItem_AllreadyExists_Throws_SQLException() throws SQLException {
        //Arrange
        Sprint sprint1 = new Sprint(138L, LocalDate.of(2016, Month.JUNE, 10), LocalDate.of(2016, Month.JUNE, 23), 10);

        SprintDAO sprintDAO = new SprintDAO(connection);
        sprintDAO.create(sprint1);

        //Act
        //Assert
        boolean result = sprintDAO.create(sprint1);
    }
    
    @Test(expected = NullPointerException.class)
    public void T40_Calling_Update_OriginalTrackingItem_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        Sprint modifiedSprint = new Sprint(138L, LocalDate.of(2016, Month.JUNE, 10), LocalDate.of(2016, Month.JUNE, 23), 10);

        SprintDAO sprintDAO = new SprintDAO(connection);

        //Act
        boolean result = sprintDAO.update(null, modifiedSprint);

        //Assert
        Assert.assertFalse(result);
    }

    @Test(expected = NullPointerException.class)
    public void T41_Calling_Update_ModifiedTrackingItem_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        Sprint originalSprint = new Sprint(138L, LocalDate.of(2016, Month.JUNE, 10), LocalDate.of(2016, Month.JUNE, 23), 10);

        SprintDAO sprintDAO = new SprintDAO(connection);

        //Act
        //Assert
        boolean result = sprintDAO.update(originalSprint, null);
    }

    @Test()
    public void T42_Calling_Update_Updates_OriginalTrackingItem() throws SQLException {
        //Arrange
        Sprint originalSprint = new Sprint(138L, LocalDate.of(2016, Month.JUNE, 10), LocalDate.of(2016, Month.JUNE, 23), 10);
        Sprint modifiedSprint = new Sprint(138L, LocalDate.of(2016, Month.JUNE, 27), LocalDate.of(2016, Month.JULY, 7), 10);

        SprintDAO sprintDAO = new SprintDAO(connection);
        sprintDAO.create(originalSprint);
        
        //Act
        boolean result = sprintDAO.update(originalSprint, modifiedSprint);
        Sprint sprintResult = sprintDAO.selectSprintFromId(modifiedSprint.getId());
        
        //Assert
        Assert.assertTrue(result);
        Assert.assertEquals(modifiedSprint, sprintResult);
    }

    @Test()
    public void T43_Calling_Update_OriginalTrackingItem_DoesNotExists_Returns_False() throws SQLException {
        //Arrange
        Sprint originalSprint = new Sprint(138L, LocalDate.of(2016, Month.JUNE, 10), LocalDate.of(2016, Month.JUNE, 23), 10);
        Sprint modifiedSprint = new Sprint(138L, LocalDate.of(2016, Month.JUNE, 27), LocalDate.of(2016, Month.JULY, 7), 10);

        SprintDAO sprintDAO = new SprintDAO(connection);
        
        //Act
        boolean result = sprintDAO.update(originalSprint, modifiedSprint);
        
        //Assert
        Assert.assertFalse(result);
    }
    
    @Test()
    public void T44_Calling_Update_On_Different_TrackingItem_DoenNotChange_Role() throws SQLException {
        //Arrange
        Sprint originalSprint = new Sprint(138L, LocalDate.of(2016, Month.JUNE, 10), LocalDate.of(2016, Month.JUNE, 23), 10);
        Sprint modifiedSprint = new Sprint(139L, LocalDate.of(2016, Month.JUNE, 27), LocalDate.of(2016, Month.JULY, 7), 10);

        SprintDAO sprintDAO = new SprintDAO(connection);
        sprintDAO.create(originalSprint);

        //Act
        boolean result = sprintDAO.update(originalSprint, modifiedSprint);
        Sprint sprintResult = sprintDAO.selectSprintFromId(originalSprint.getId());

        //Assert
        Assert.assertFalse(result);
        Assert.assertEquals(originalSprint, sprintResult);
    }

    @Test(expected = NullPointerException.class)
    public void T50_Calling_Delete_Sprint_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        SprintDAO sprintDAO = new SprintDAO(connection);

        //Act
        //Assert
        boolean result = sprintDAO.delete(null);
    }
    
    @Test()
    public void T51_Calling_Delete_TrackingItem_DoesNotExists_Returns_False() throws SQLException {
        //Arrange
        Sprint sprint = new Sprint(138L, LocalDate.of(2016, Month.JUNE, 10), LocalDate.of(2016, Month.JUNE, 23), 10);

        SprintDAO sprintDAO = new SprintDAO(connection);

        //Act
        boolean result = sprintDAO.delete(sprint);

        //Assert
        Assert.assertFalse(result);
    }

    @Test()
    public void T52_Calling_Delete_TrackingItem_Exists_Deletes_TrackingItem() throws SQLException {
        //Arrange
        Sprint sprint = new Sprint(138L, LocalDate.of(2016, Month.JUNE, 10), LocalDate.of(2016, Month.JUNE, 23), 10);

        SprintDAO sprintDAO = new SprintDAO(connection);
        sprintDAO.create(sprint);
        
        //Act
        boolean result = sprintDAO.delete(sprint);
        Sprint sprintResult = sprintDAO.selectSprintFromId(sprint.getId());

        //Assert
        Assert.assertTrue(result);
        Assert.assertNull(sprintResult);
    }
    
    @Test()
    public void T60_Calling_GetNextId_On_TrackingItemTable_Containing_One_Record_Returns_2() throws SQLException {
        //Arrange
        Sprint sprint = new Sprint(138L, LocalDate.of(2016, Month.JUNE, 10), LocalDate.of(2016, Month.JUNE, 23), 10);

        SprintDAO sprintDAO = new SprintDAO(connection);
        sprintDAO.create(sprint);
        
        //Act
        long receivedId = sprintDAO.getNextId();
        
        //Assert
        Assert.assertEquals(139L, receivedId);
    }
    
    @Test()
    public void T61_Calling_GetNextId_Twice_On_TrackingItemTable_Returns_SameId() throws SQLException {
        //Arrange
        Sprint sprint = new Sprint(138L, LocalDate.of(2016, Month.JUNE, 10), LocalDate.of(2016, Month.JUNE, 23), 10);

        SprintDAO sprintDAO = new SprintDAO(connection);
        sprintDAO.create(sprint);
        
        //Act
        long receivedIdFirstCall = sprintDAO.getNextId();
        long receivedIdSecondCall = sprintDAO.getNextId();
        
        //Assert
        Assert.assertEquals(receivedIdSecondCall, receivedIdFirstCall);
    }

    @Test
    public void T62_Calling_GetNextId_On_Truncated_Sqlite_Sequence_Table_Returns_0() throws SQLException {
        //Arrange
        SprintDAO sprintDAO = new SprintDAO(connection);
        
        //Act
        long receivedId = sprintDAO.getNextId();

        //Assert
        Assert.assertEquals(0L, receivedId);
    }
    
    private synchronized boolean truncateTable() {
        boolean result = false;
        StringBuilder statement = new StringBuilder();
        statement.append("DELETE FROM sprint ");
        
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }
        
        statement = new StringBuilder();        
        //statement.append("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name='Sprint'");
        statement.append("DELETE FROM SQLITE_SEQUENCE WHERE name='Sprint'");
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }
        return result;
    }

}
