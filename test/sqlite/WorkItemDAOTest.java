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
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import model.TrackingItem;
import model.WorkItem;
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
public class WorkItemDAOTest {
    
    private final static String APP_LANGUAGE_RESOURCE_KEY = "Language";
    private final static String APP_LANGUAGE_DEFAULT_VALUE = "en";
    private final static String LANGUAGE_RESOURCE = "languages.bundle";
    private final static String DATABASE_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/sqlite/testdb.sqlite";
    private final static String LOG4J2_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/log4j2.xml";
    
    private static ResourceBundle bundle;
    private static ConnectionFactory connectionFactory;
    private static Connection connection;
    
    public WorkItemDAOTest() throws FileNotFoundException { 
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
        WorkItemDAO workItemDAO = new WorkItemDAO(null);
    }
    
    @Test()
    public void T01_Calling_Ctor_With_Valid_Connection_Returns_Initialized_Instance() {
        //Arrange
        //Act
        WorkItemDAO workItemDAO = new WorkItemDAO(connection);
        
        //Assert
        Assert.assertNotNull(workItemDAO);
    }
    
    @Test()
    public void T10_Calling_SelectAll_Returns_All_Found_WorkItems() throws SQLException {
        //Arrange
        Long workrecordId = 1L;
        Long sprintId = 1L;
        Long trackingItemId = 1L;
        LocalTime starttime = LocalTime.of(1, 0);
        LocalTime endtime = LocalTime.of(1, 0);
        String description = "All task related to SCRUM activities";
        String shortcut = "S";
        String name = "Scrum";

        TrackingItem trackingItem1 = new TrackingItem(1L, "Scrum", "S", "All task related to SCRUM activities");
        TrackingItemDAO trackingItemDAO = new TrackingItemDAO(connection);
        trackingItemDAO.create(trackingItem1);     
        
        WorkItem workItem1 = new WorkItem(1L, workrecordId, sprintId, trackingItemId, starttime, endtime, description, shortcut, name);        
        WorkItem workItem2 = new WorkItem(2L, workrecordId, sprintId, trackingItemId, starttime, endtime, description, shortcut, name);        

        WorkItemDAO workItemDAO = new WorkItemDAO(connection);
        workItemDAO.create(workItem1);
        workItemDAO.create(workItem2);
        
        //Act
        List<WorkItem> workItems = workItemDAO.selectAll();
        
        //Assert
        Assert.assertEquals(2, workItems.size());
        Assert.assertEquals(workItem1, workItems.get(0));
        Assert.assertEquals(workItem2, workItems.get(1));
    }    
    
    @Test()
    public void T11_Calling_SelectAll_Returns_EmptyList_If_No_WorkItem_Found() throws SQLException {
        //Arrange
        WorkItemDAO workItemDAO = new WorkItemDAO(connection);
        
        //Act
        List<WorkItem> workItemList = workItemDAO.selectAll();

        //Assert
        Assert.assertTrue(workItemList.isEmpty());
    }
    
    @Test(expected = NullPointerException.class)
    public void T30_Calling_Create_WorkItem_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        WorkItemDAO workItemDAO = new WorkItemDAO(connection);

        //Act
        //Assert
        boolean result = workItemDAO.create(null);
    }
    
    @Test()
    public void T31_Calling_Create_Stores_WorkItem() throws SQLException {
        //Arrange
        Long workrecordId = 1L;
        Long sprintId = 1L;
        Long trackingItemId = 1L;
        LocalTime starttime = LocalTime.of(1, 0);
        LocalTime endtime = LocalTime.of(1, 0);
        String description = "TestDescription";
        String shortcut = "TestShortcut";
        String name = "TestName";
        
        WorkItem workItem = new WorkItem(1L, workrecordId, sprintId, trackingItemId, starttime, endtime, description, shortcut, name);        

        WorkItemDAO workItemDAO = new WorkItemDAO(connection);

        //Act
        boolean result = workItemDAO.create(workItem);

         //Assert
        Assert.assertTrue(result);
    }
    
    @Test(expected = SQLException.class)
    public void T32_Calling_Create_WorkItem_AllreadyExists_Throws_SQLException() throws SQLException {
        //Arrange
        Long workrecordId = 1L;
        Long sprintId = 1L;
        Long trackingItemId = 1L;
        LocalTime starttime = LocalTime.of(1, 0);
        LocalTime endtime = LocalTime.of(1, 0);
        String description = "TestDescription";
        String shortcut = "TestShortcut";
        String name = "TestName";
        
        WorkItem workItem = new WorkItem(1L, workrecordId, sprintId, trackingItemId, starttime, endtime, description, shortcut, name);        

        WorkItemDAO workItemDAO = new WorkItemDAO(connection);
        workItemDAO.create(workItem);

        //Act
         //Assert
        workItemDAO.create(workItem);
    }
    
    @Test(expected = NullPointerException.class)
    public void T40_Calling_Update_OriginalWorkItem_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        Long workrecordId = 1L;
        Long sprintId = 1L;
        Long trackingItemId = 1L;
        LocalTime starttime = LocalTime.of(1, 0);
        LocalTime endtime = LocalTime.of(1, 0);
        String description = "All task related to SCRUM activities";
        String shortcut = "S";
        String name = "Scrum";

        TrackingItem trackingItem1 = new TrackingItem(1L, "Scrum", "S", "All task related to SCRUM activities");
        TrackingItemDAO trackingItemDAO = new TrackingItemDAO(connection);
        trackingItemDAO.create(trackingItem1);     
               
        WorkItem modifiedWorkItem = new WorkItem(1L, workrecordId, sprintId, trackingItemId, starttime, endtime, description, shortcut, name);        

        WorkItemDAO workItemDAO = new WorkItemDAO(connection);

        //Act
        boolean result = workItemDAO.update(null, modifiedWorkItem);

        //Assert
        Assert.assertFalse(result);        
    }
    
    @Test(expected = NullPointerException.class)
    public void T41_Calling_Update_ModifiedWorkItem_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        Long workrecordId = 1L;
        Long sprintId = 1L;
        Long trackingItemId = 1L;
        LocalTime starttime = LocalTime.of(1, 0);
        LocalTime endtime = LocalTime.of(1, 0);
        String description = "TestDescription";
        String shortcut = "TestShortcut";
        String name = "TestName";
        
        WorkItem originalWorkItem = new WorkItem(1L, workrecordId, sprintId, trackingItemId, starttime, endtime, description, shortcut, name);        

        WorkItemDAO workItemDAO = new WorkItemDAO(connection);

        //Act
        boolean result = workItemDAO.update(originalWorkItem, null);

        //Assert
        Assert.assertFalse(result);        
    }
    
    @Test()
    public void T42_Calling_Update_Updates_OriginalWorkItem() throws SQLException {
        //Arrange
        Long workrecordId = 1L;
        Long sprintId = 1L;
        Long trackingItemId = 1L;
        LocalTime starttime = LocalTime.of(1, 0);
        LocalTime endtime = LocalTime.of(1, 0);
        String description = "All task related to SCRUM activities";
        String shortcut = "S";
        String name = "Scrum";

        TrackingItem trackingItem1 = new TrackingItem(1L, "Scrum", "S", "All task related to SCRUM activities");
        TrackingItemDAO trackingItemDAO = new TrackingItemDAO(connection);
        trackingItemDAO.create(trackingItem1);     
        
        String modifiedDescription = "modified TestDescription";
        
        WorkItem originalWorkItem = new WorkItem(1L, workrecordId, sprintId, trackingItemId, starttime, endtime, description, shortcut, name);        
        WorkItem modifiedWorkItem = new WorkItem(1L, workrecordId, sprintId, trackingItemId, starttime, endtime, modifiedDescription, shortcut, name);        

        WorkItemDAO workItemDAO = new WorkItemDAO(connection);
        workItemDAO.create(originalWorkItem);

        //Act
        boolean result = workItemDAO.update(originalWorkItem, modifiedWorkItem);
        List<WorkItem> workItemResult = workItemDAO.selectAll();
        
        //Assert
        Assert.assertTrue(result);
        Assert.assertEquals(modifiedWorkItem, workItemResult.get(0));
    }
    
    @Test()
    public void T43_Calling_Update_OriginalWorkItem_DoesNotExists_Returns_False() throws SQLException {
        //Arrange
        Long workrecordId = 1L;
        Long sprintId = 1L;
        Long trackingItemId = 1L;
        LocalTime starttime = LocalTime.of(1, 0);
        LocalTime endtime = LocalTime.of(1, 0);
        String description = "TestDescription";
        String shortcut = "TestShortcut";
        String name = "TestName";
        
        String modifiedDescription = "modified TestDescription";
        
        WorkItem originalWorkItem = new WorkItem(1L, workrecordId, sprintId, trackingItemId, starttime, endtime, description, shortcut, name);        
        WorkItem modifiedWorkItem = new WorkItem(1L, workrecordId, sprintId, trackingItemId, starttime, endtime, modifiedDescription, shortcut, name);        

        WorkItemDAO workItemDAO = new WorkItemDAO(connection);

        //Act
        boolean result = workItemDAO.update(originalWorkItem, modifiedWorkItem);
        List<WorkItem> workItemResult = workItemDAO.selectAll();
        
        //Assert
        Assert.assertFalse(result);
    }
    
    @Test()
    public void T44_Calling_Update_On_Different_WorkItem_DoenNotChange_WorkItem() throws SQLException {
        //Arrange
        Long workrecordId = 1L;
        Long sprintId = 1L;
        Long trackingItemId = 1L;
        LocalTime starttime = LocalTime.of(1, 0);
        LocalTime endtime = LocalTime.of(1, 0);
        String description = "All task related to SCRUM activities";
        String shortcut = "S";
        String name = "Scrum";

        TrackingItem trackingItem1 = new TrackingItem(1L, "Scrum", "S", "All task related to SCRUM activities");
        TrackingItemDAO trackingItemDAO = new TrackingItemDAO(connection);
        trackingItemDAO.create(trackingItem1);     
        
        String modifiedDescription = "modified TestDescription";
        
        WorkItem originalWorkItem = new WorkItem(1L, workrecordId, sprintId, trackingItemId, starttime, endtime, description, shortcut, name);        
        WorkItem modifiedWorkItem = new WorkItem(2L, workrecordId, sprintId, trackingItemId, starttime, endtime, modifiedDescription, shortcut, name);        

        WorkItemDAO workItemDAO = new WorkItemDAO(connection);
        workItemDAO.create(originalWorkItem);

        //Act
        boolean result = workItemDAO.update(originalWorkItem, modifiedWorkItem);
        List<WorkItem> workItemResult = workItemDAO.selectAll();
        
        //Assert
        Assert.assertFalse(result);
        Assert.assertEquals(originalWorkItem, workItemResult.get(0));
    }    

    @Test(expected = NullPointerException.class)
    public void T50_Calling_Delete_WorkItem_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        WorkItemDAO workItemDAO = new WorkItemDAO(connection);

        //Act
        //Assert
        boolean result = workItemDAO.delete(null);
    }
    
    @Test()
    public void T51_Calling_Delete_WorkItem_DoesNotExists_Returns_False() throws SQLException {
        //Arrange
        Long workrecordId = 1L;
        Long sprintId = 1L;
        Long trackingItemId = 1L;
        LocalTime starttime = LocalTime.of(1, 0);
        LocalTime endtime = LocalTime.of(1, 0);
        String description = "TestDescription";
        String shortcut = "TestShortcut";
        String name = "TestName";
        
        WorkItem workItem = new WorkItem(1L, workrecordId, sprintId, trackingItemId, starttime, endtime, description, shortcut, name);        

        WorkItemDAO workItemDAO = new WorkItemDAO(connection);

        //Act
        boolean result = workItemDAO.delete(workItem);

        //Assert
        Assert.assertFalse(result);        
    }
    
    @Test()
    public void T52_Calling_Delete_WorkItem_Exists_Deletes_Workrecord() throws SQLException {
        //Arrange
        Long workrecordId = 1L;
        Long sprintId = 1L;
        Long trackingItemId = 1L;
        LocalTime starttime = LocalTime.of(1, 0);
        LocalTime endtime = LocalTime.of(1, 0);
        String description = "TestDescription";
        String shortcut = "TestShortcut";
        String name = "TestName";
        
        WorkItem workItem = new WorkItem(1L, workrecordId, sprintId, trackingItemId, starttime, endtime, description, shortcut, name);        

        WorkItemDAO workItemDAO = new WorkItemDAO(connection);
        workItemDAO.create(workItem);

        //Act
        boolean result = workItemDAO.delete(workItem);
        List<WorkItem> workItemResult = workItemDAO.selectAll();
        
        //Assert
        Assert.assertTrue(result);
        Assert.assertTrue(workItemResult.isEmpty());    }
    
    @Test()
    public void T60_Calling_GetNextId_On_WorkItemTable_Containing_One_WorkItem_Returns_2() throws SQLException {
        //Arrange
        Long workrecordId = 1L;
        Long sprintId = 1L;
        Long trackingItemId = 1L;
        LocalTime starttime = LocalTime.of(1, 0);
        LocalTime endtime = LocalTime.of(1, 0);
        String description = "TestDescription";
        String shortcut = "TestShortcut";
        String name = "TestName";
        
        WorkItem workItem = new WorkItem(1L, workrecordId, sprintId, trackingItemId, starttime, endtime, description, shortcut, name);        

        WorkItemDAO workItemDAO = new WorkItemDAO(connection);
        workItemDAO.create(workItem);

        //Act
        long receivedId = workItemDAO.getNextId();
        
        //Assert
        Assert.assertEquals(2L, receivedId);        
    }
    
    @Test()
    public void T61_Calling_GetNextId_Twice_On_WorkItemTable_Returns_SameId() throws SQLException {
        //Arrange
        Long workrecordId = 1L;
        Long sprintId = 1L;
        Long trackingItemId = 1L;
        LocalTime starttime = LocalTime.of(1, 0);
        LocalTime endtime = LocalTime.of(1, 0);
        String description = "TestDescription";
        String shortcut = "TestShortcut";
        String name = "TestName";
        
        WorkItem workItem = new WorkItem(1L, workrecordId, sprintId, trackingItemId, starttime, endtime, description, shortcut, name);        

        WorkItemDAO workItemDAO = new WorkItemDAO(connection);
        workItemDAO.create(workItem);

        //Act
        long receivedIdFirstCall = workItemDAO.getNextId();
        long receivedIdSecondCall = workItemDAO.getNextId();
        
        //Assert
        Assert.assertEquals(receivedIdSecondCall, receivedIdFirstCall);
    }
    
    @Test
    public void T62_Calling_GetNextId_On_Truncated_Sqlite_Sequence_Table_Returns_0() throws SQLException {
        //Arrange
        WorkItemDAO workItemDAO = new WorkItemDAO(connection);
        
        //Act
        long resultId = workItemDAO.getNextId();

        //Assert
        Assert.assertEquals(0L, resultId);
    }
    
    private synchronized boolean truncateTable() {
        
        boolean result = false;
        
        StringBuilder statement = new StringBuilder();
        statement.append("DELETE FROM workitem ");
        
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }
        
        statement = new StringBuilder();
        statement.append("DELETE FROM trackingitem ");
        
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }

        statement = new StringBuilder();        
        statement.append("DELETE FROM SQLITE_SEQUENCE WHERE name='WorkItem'");
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }
        
        statement = new StringBuilder();        
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
