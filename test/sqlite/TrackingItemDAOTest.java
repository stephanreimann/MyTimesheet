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
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import model.TrackingItem;
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
public class TrackingItemDAOTest {
    
    private final static String APP_LANGUAGE_RESOURCE_KEY = "Language";
    private final static String APP_LANGUAGE_DEFAULT_VALUE = "en";
    private final static String LANGUAGE_RESOURCE = "languages.bundle";
    private final static String DATABASE_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/sqlite/testdb.sqlite";
    private final static String LOG4J2_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/log4j2.xml";
    
    private static ResourceBundle bundle;
    private static ConnectionFactory connectionFactory;
    private static Connection connection;
    
    public TrackingItemDAOTest() throws FileNotFoundException { 
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
        TrackingItemDAO trackingItemDAO = new TrackingItemDAO(null);
    }
    
    @Test()
    public void T01_Calling_Ctor_With_Valid_Connection_Returns_Initialized_Instance() {
        //Arrange
        //Act
        TrackingItemDAO trackingItemDAO = new TrackingItemDAO(connection);
        
        //Assert
        Assert.assertNotNull(trackingItemDAO);
    }

    @Test()
    public void T10_Calling_SelectAll_Returns_All_Found_TrackingItems() throws SQLException {
        //Arrange
        //Act
        TrackingItem trackingItem1 = new TrackingItem(1L, "Scrum", "S", "All task related to SCRUM activities");
        TrackingItem trackingItem2 = new TrackingItem(2L, "Feature Development", "FD", "Feature Development, provide a Product Backlog Item");
        TrackingItem trackingItem3 = new TrackingItem(3L, "Bug Fixing", "BF", "Bug Fixing, provide a Bug ID");
        TrackingItem trackingItem4 = new TrackingItem(4L, "Test Evaluation and Maintenance", "TE", "All task related to Test evaluation and/or Test maintenance activities");
        TrackingItem trackingItem5 = new TrackingItem(5L, "Technical Debts", "TD", "Working on Technical Debts, provide ID");
        TrackingItem trackingItem6 = new TrackingItem(6L, "Misc", "M", "Misc Task, like learning sessions, global Meetings, etc.");

        TrackingItemDAO trackingItemDAO = new TrackingItemDAO(connection);
        trackingItemDAO.create(trackingItem1);
        trackingItemDAO.create(trackingItem2);
        trackingItemDAO.create(trackingItem3);
        trackingItemDAO.create(trackingItem4);
        trackingItemDAO.create(trackingItem5);
        trackingItemDAO.create(trackingItem6);
        
        //Act
        List<TrackingItem> trackingItemList = trackingItemDAO.selectAll();

        //Assert
        Assert.assertEquals(6, trackingItemList.size());
        Assert.assertEquals(trackingItem1, trackingItemList.get(0));
        Assert.assertEquals(trackingItem2, trackingItemList.get(1));
        Assert.assertEquals(trackingItem3, trackingItemList.get(2));
        Assert.assertEquals(trackingItem4, trackingItemList.get(3));
        Assert.assertEquals(trackingItem5, trackingItemList.get(4));
        Assert.assertEquals(trackingItem6, trackingItemList.get(5));
    }
    
    @Test()
    public void T11_Calling_SelectAll_Returns_EmptyList_If_No_TrackingItems_Found() throws SQLException {
        //Arrange
        TrackingItemDAO trackingItemDAO = new TrackingItemDAO(connection);
        
        //Act
        List<TrackingItem> trackingItemList = trackingItemDAO.selectAll();

        //Assert
        Assert.assertTrue(trackingItemList.isEmpty());
    }

    @Test()
    public void T20_Calling_SelectTrackingItemFromId_Returns_The_Stored_TrackingItem() throws SQLException {
        //Arrange
        TrackingItem trackingItem1 = new TrackingItem(1L, "Scrum", "S", "All task related to SCRUM activities");
        TrackingItem trackingItem2 = new TrackingItem(2L, "Feature Development", "FD", "Feature Development, provide a Product Backlog Item");

        TrackingItemDAO trackingItemDAO = new TrackingItemDAO(connection);
        trackingItemDAO.create(trackingItem1);
        trackingItemDAO.create(trackingItem2);
        
        //Act
        TrackingItem trackingItem = trackingItemDAO.selectTrackingItemFromId(1L);

        //Assert
        Assert.assertEquals(trackingItem1, trackingItem);
    }

    @Test()
    public void T21_Calling_SelectTrackingItemFromId_Returns_Null_If_TrackingItem_NotFound() throws SQLException {
        //Arrange
        TrackingItem trackingItem1 = new TrackingItem(1L, "Scrum", "S", "All task related to SCRUM activities");

        TrackingItemDAO trackingItemDAO = new TrackingItemDAO(connection);
        trackingItemDAO.create(trackingItem1);
        
        //Act
        TrackingItem trackingItem = trackingItemDAO.selectTrackingItemFromId(2L);

        //Assert
        Assert.assertNull(trackingItem);
    }

    @Test(expected = NullPointerException.class)
    public void T30_Calling_Create_TrackingItem_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        TrackingItemDAO trackingItemDAO = new TrackingItemDAO(connection);

        //Act
        boolean result = trackingItemDAO.create(null);

        //Assert
        Assert.assertFalse(result);
    }

    @Test()
    public void T31_Calling_Create_Stores_TrackingItem() throws SQLException {
        //Arrange
        TrackingItem trackingItem = new TrackingItem(1L, "Scrum", "S", "All task related to SCRUM activities");

        TrackingItemDAO trackingItemDAO = new TrackingItemDAO(connection);
        
        //Act
        boolean result = trackingItemDAO.create(trackingItem);
        
        //Assert
        Assert.assertTrue(result);
    }
    
    @Test(expected = SQLException.class)
    public void T32_Calling_Create_TrackingItem_AllreadyExists_Throws_SQLException() throws SQLException {
        //Arrange
        TrackingItem trackingItem = new TrackingItem(1L, "Scrum", "S", "All task related to SCRUM activities");

        TrackingItemDAO trackingItemDAO = new TrackingItemDAO(connection);
        trackingItemDAO.create(trackingItem);

        //Act
        //Assert
        boolean result = trackingItemDAO.create(trackingItem);        
    }

    @Test(expected = NullPointerException.class)
    public void T40_Calling_Update_OriginalTrackingItem_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        TrackingItem modifiedTrackingItem = new TrackingItem(1L, "Scrum", "S", "All task related to SCRUM activities");

        TrackingItemDAO trackingItemDAO = new TrackingItemDAO(connection);

        //Act
        boolean result = trackingItemDAO.update(null, modifiedTrackingItem);

        //Assert
        Assert.assertFalse(result);
    }

    @Test(expected = NullPointerException.class)
    public void T41_Calling_Update_ModifiedTrackingItem_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        TrackingItem originalTrackingItem = new TrackingItem(1L, "Scrum", "S", "All task related to SCRUM activities");

        TrackingItemDAO trackingItemDAO = new TrackingItemDAO(connection);

        //Act
        //Assert
        boolean result = trackingItemDAO.update(originalTrackingItem, null);
    }

    @Test()
    public void T42_Calling_Update_Updates_OriginalTrackingItem() throws SQLException {
        //Arrange
        TrackingItem originalTrackingItem = new TrackingItem(1L, "Scrum", "S", "All task related to SCRUM activities");
        TrackingItem modifiedTrackingItem = new TrackingItem(1L, "Bug Fixing", "BF", "Bug Fixing, provide a Bug ID");

        TrackingItemDAO trackingItemDAO = new TrackingItemDAO(connection);
        trackingItemDAO.create(originalTrackingItem);
        
        //Act
        boolean result = trackingItemDAO.update(originalTrackingItem, modifiedTrackingItem);
        TrackingItem trackinItemResult = trackingItemDAO.selectTrackingItemFromId(modifiedTrackingItem.getId());
        
        //Assert
        Assert.assertTrue(result);
        Assert.assertEquals(modifiedTrackingItem, trackinItemResult);
    }

    @Test()
    public void T43_Calling_Update_OriginalTrackingItem_DoesNotExists_Returns_False() throws SQLException {
        //Arrange
        TrackingItem originalTrackingItem = new TrackingItem(1L, "Scrum", "S", "All task related to SCRUM activities");
        TrackingItem modifiedTrackingItem = new TrackingItem(1L, "Bug Fixing", "BF", "Bug Fixing, provide a Bug ID");

        TrackingItemDAO trackingItemDAO = new TrackingItemDAO(connection);
        
        //Act
        boolean result = trackingItemDAO.update(originalTrackingItem, modifiedTrackingItem);
        
        //Assert
        Assert.assertFalse(result);
    }
    
    @Test()
    public void T44_Calling_Update_On_Different_TrackingItem_DoenNotChange_Role() throws SQLException {
        //Arrange
        TrackingItem originalTrackingItem = new TrackingItem(1L, "Scrum", "S", "All task related to SCRUM activities");
        TrackingItem modifiedTrackingItem = new TrackingItem(2L, "Bug Fixing", "BF", "Bug Fixing, provide a Bug ID");

        TrackingItemDAO trackingItemDAO = new TrackingItemDAO(connection);
        trackingItemDAO.create(originalTrackingItem);

        //Act
        boolean result = trackingItemDAO.update(originalTrackingItem, modifiedTrackingItem);
        TrackingItem trackinItemResult = trackingItemDAO.selectTrackingItemFromId(originalTrackingItem.getId());

        //Assert
        Assert.assertFalse(result);
        Assert.assertEquals(originalTrackingItem, trackinItemResult);
    }

    @Test(expected = NullPointerException.class)
    public void T50_Calling_Delete_TrackingItem_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        TrackingItemDAO trackingItemDAO = new TrackingItemDAO(connection);

        //Act
        //Assert
        boolean result = trackingItemDAO.delete(null);
    }
    
    @Test()
    public void T51_Calling_Delete_TrackingItem_DoesNotExists_Returns_False() throws SQLException {
        //Arrange
        TrackingItem trackingItem = new TrackingItem(1L, "Scrum", "S", "All task related to SCRUM activities");

        TrackingItemDAO trackingItemDAO = new TrackingItemDAO(connection);

        //Act
        boolean result = trackingItemDAO.delete(trackingItem);

        //Assert
        Assert.assertFalse(result);
    }

    @Test()
    public void T52_Calling_Delete_TrackingItem_Exists_Deletes_TrackingItem() throws SQLException {
        //Arrange
        TrackingItem trackingItem = new TrackingItem(1L, "Scrum", "S", "All task related to SCRUM activities");

        TrackingItemDAO trackingItemDAO = new TrackingItemDAO(connection);
        trackingItemDAO.create(trackingItem);
        
        //Act
        boolean result = trackingItemDAO.delete(trackingItem);
        TrackingItem trackingItemResult = trackingItemDAO.selectTrackingItemFromId(trackingItem.getId());

        //Assert
        Assert.assertTrue(result);
        Assert.assertNull(trackingItemResult);
    }

    @Test()
    public void T60_Calling_GetNextId_On_TrackingItemTable_Containing_One_Record_Returns_2() throws SQLException {
        //Arrange
        TrackingItem trackingItem = new TrackingItem(1L, "Scrum", "S", "All task related to SCRUM activities");

        TrackingItemDAO trackingItemDAO = new TrackingItemDAO(connection);
        trackingItemDAO.create(trackingItem);
        
        //Act
        long receivedId = trackingItemDAO.getNextId();
        
        //Assert
        Assert.assertEquals(2L, receivedId);
    }
    
    @Test()
    public void T61_Calling_GetNextId_Twice_On_TrackingItemTable_Returns_SameId() throws SQLException {
        //Arrange
        TrackingItem trackingItem = new TrackingItem(1L, "Scrum", "S", "All task related to SCRUM activities");

        TrackingItemDAO trackingItemDAO = new TrackingItemDAO(connection);
        trackingItemDAO.create(trackingItem);
        
        //Act
        long receivedIdFirstCall = trackingItemDAO.getNextId();
        long receivedIdSecondCall = trackingItemDAO.getNextId();
        
        //Assert
        Assert.assertEquals(receivedIdSecondCall, receivedIdFirstCall);
    }

    @Test
    public void T62_Calling_GetNextId_On_Truncated_Sqlite_Sequence_Table_Returns_0() throws SQLException {
        //Arrange
        TrackingItemDAO trackingItemDAO = new TrackingItemDAO(connection);
        
        //Act
        long resultId = trackingItemDAO.getNextId();

        //Assert
        Assert.assertEquals(0L, resultId);
    }

    private synchronized boolean truncateTable() {
        boolean result = false;
        StringBuilder statement = new StringBuilder();
        statement.append("DELETE FROM trackingitem ");
        
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }
        
        statement = new StringBuilder();        
        //statement.append("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name='TrackingItem'");
        statement.append("DELETE FROM SQLITE_SEQUENCE WHERE name='TrackingItem'");
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }
        return result;
    }
    
}
