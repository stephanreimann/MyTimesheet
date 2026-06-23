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
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import model.Address;
import model.Contract;
import model.Project;
import model.Role;
import model.Sprint;
import model.TrackingItem;
import model.User;
import model.WorkItem;
import model.Worklocation;
import model.Workrecord;
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
        String description = "TestDescription";
        
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        RoleDAO roleDAO = new RoleDAO(connection);
        roleDAO.create(role);
        
        Address address = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        AddressDAO addressDAO = new AddressDAO(connection);
        addressDAO.create(address);
        
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        ContractDAO contractDAO = new ContractDAO(connection);
        contractDAO.create(contract);
        
        User user = new User(1L, role, address, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        UserDAO userDAO = new UserDAO(connection);
        userDAO.create(user);
        
        Project project = new Project(1L, "TestProject", "AAAA.BBBB.CCCC.DDDD", "TRUE", "TRUE", "TRUE", "TestDescription");
        ProjectDAO projectDAO = new ProjectDAO(connection);
        projectDAO.create(project);
        
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);
        worklocationDAO.create(worklocation);
        
        Workrecord workrecord = new Workrecord(1L, user, project, LocalDate.now(), LocalTime.now(), LocalTime.now(), LocalTime.now(), LocalTime.now().toString(), LocalTime.now().toString(), 0, worklocation, description);
        WorkrecordDAO workrecordDAO = new WorkrecordDAO(connection);
        workrecordDAO.create(workrecord);
        
        Sprint sprint = new Sprint(1L, LocalDate.now(), LocalDate.now(), 10);
        SprintDAO sprintDAO = new SprintDAO(connection);
        sprintDAO.create(sprint);
        
        TrackingItem trackingItem = new TrackingItem(1L, "Scrum", "S", "All task related to SCRUM activities");
        TrackingItemDAO trackingItemDAO = new TrackingItemDAO(connection);
        trackingItemDAO.create(trackingItem);
        
        WorkItem workItem1 = new WorkItem(1L, user, workrecord, sprint, trackingItem, LocalTime.now(), LocalTime.now(), description);        
        WorkItem workItem2 = new WorkItem(2L, user, workrecord, sprint, trackingItem, LocalTime.now(), LocalTime.now(), description);        

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
    
    @Test
    public void T12_Calling_SelectAll_Returns_All_Found_WorkItems_That_Match_The_QueryParameter() throws SQLException {
        //Arrange
        String description = "TestDescription";
        
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        RoleDAO roleDAO = new RoleDAO(connection);
        roleDAO.create(role);
        
        Address address = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        AddressDAO addressDAO = new AddressDAO(connection);
        addressDAO.create(address);
        
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        ContractDAO contractDAO = new ContractDAO(connection);
        contractDAO.create(contract);
        
        User user1 = new User(1L, role, address, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        User user2 = new User(2L, role, address, contract, "Gabi", "Golibrzuch", "gabi", "password", 30L);
        UserDAO userDAO = new UserDAO(connection);
        userDAO.create(user1);
        userDAO.create(user2);
        
        Project project = new Project(1L, "TestProject", "AAAA.BBBB.CCCC.DDDD", "TRUE", "TRUE", "TRUE", "TestDescription");
        ProjectDAO projectDAO = new ProjectDAO(connection);
        projectDAO.create(project);
        
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);
        worklocationDAO.create(worklocation);
        
        Workrecord workrecord1 = new Workrecord(1L, user1, project, LocalDate.now(), LocalTime.now(), LocalTime.now(), LocalTime.now(), LocalTime.now().toString(), LocalTime.now().toString(), 0, worklocation, description);
        Workrecord workrecord2 = new Workrecord(2L, user2, project, LocalDate.now(), LocalTime.now(), LocalTime.now(), LocalTime.now(), LocalTime.now().toString(), LocalTime.now().toString(), 0, worklocation, description);
        WorkrecordDAO workrecordDAO = new WorkrecordDAO(connection);
        workrecordDAO.create(workrecord1);
        workrecordDAO.create(workrecord2);
        
        Sprint sprint1 = new Sprint(139L, LocalDate.now(), LocalDate.now(), 10);
        Sprint sprint2 = new Sprint(140L, LocalDate.now(), LocalDate.now(), 10);
        SprintDAO sprintDAO = new SprintDAO(connection);
        sprintDAO.create(sprint1);
        sprintDAO.create(sprint2);
        
        TrackingItem trackingItem = new TrackingItem(139L, "Scrum", "S", "All task related to SCRUM activities");
        TrackingItemDAO trackingItemDAO = new TrackingItemDAO(connection);
        trackingItemDAO.create(trackingItem);

        WorkItem workItem1 = new WorkItem(1L, user1, workrecord1, sprint1, trackingItem, LocalTime.now(), LocalTime.now(), description);        
        WorkItem workItem2 = new WorkItem(2L, user2, workrecord2, sprint2, trackingItem, LocalTime.now(), LocalTime.now(), description);        
        
        //Act
        WorkItemDAO workItemDAO = new WorkItemDAO(connection);
        workItemDAO.create(workItem1);
        workItemDAO.create(workItem2);
        
        //Act
        List<WorkItem> workItems = workItemDAO.selectAll(user1, sprint1);

        //Assert
        Assert.assertEquals(1, workItems.size());
        Assert.assertEquals(workItem1, workItems.get(0));
    }    
    
    @Test
    public void T13_Calling_SelectAll_Returns_EmptyList_If_User_NotFound() throws SQLException {
        //Arrange
        String description = "TestDescription";
        
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        RoleDAO roleDAO = new RoleDAO(connection);
        roleDAO.create(role);
        
        Address address = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        AddressDAO addressDAO = new AddressDAO(connection);
        addressDAO.create(address);
        
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        ContractDAO contractDAO = new ContractDAO(connection);
        contractDAO.create(contract);
        
        User user1 = new User(1L, role, address, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        User user2 = new User(2L, role, address, contract, "Gabi", "Golibrzuch", "gabi", "password", 30L);
        User notFoundUser = new User(3L, role, address, contract, "Gabi", "Golibrzuch", "gabi", "password", 30L);
        UserDAO userDAO = new UserDAO(connection);
        userDAO.create(user1);
        userDAO.create(user2);
        
        Project project = new Project(1L, "TestProject", "AAAA.BBBB.CCCC.DDDD", "TRUE", "TRUE", "TRUE", "TestDescription");
        ProjectDAO projectDAO = new ProjectDAO(connection);
        projectDAO.create(project);
        
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);
        worklocationDAO.create(worklocation);
        
        Workrecord workrecord1 = new Workrecord(1L, user1, project, LocalDate.now(), LocalTime.now(), LocalTime.now(), LocalTime.now(), LocalTime.now().toString(), LocalTime.now().toString(), 0, worklocation, description);
        Workrecord workrecord2 = new Workrecord(2L, user2, project, LocalDate.now(), LocalTime.now(), LocalTime.now(), LocalTime.now(), LocalTime.now().toString(), LocalTime.now().toString(), 0, worklocation, description);
        WorkrecordDAO workrecordDAO = new WorkrecordDAO(connection);
        workrecordDAO.create(workrecord1);
        workrecordDAO.create(workrecord2);
        
        Sprint sprint1 = new Sprint(139L, LocalDate.now(), LocalDate.now(), 10);
        Sprint sprint2 = new Sprint(140L, LocalDate.now(), LocalDate.now(), 10);
        SprintDAO sprintDAO = new SprintDAO(connection);
        sprintDAO.create(sprint1);
        sprintDAO.create(sprint2);
        
        TrackingItem trackingItem = new TrackingItem(139L, "Scrum", "S", "All task related to SCRUM activities");
        TrackingItemDAO trackingItemDAO = new TrackingItemDAO(connection);
        trackingItemDAO.create(trackingItem);

        WorkItem workItem1 = new WorkItem(1L, user1, workrecord1, sprint1, trackingItem, LocalTime.now(), LocalTime.now(), description);        
        WorkItem workItem2 = new WorkItem(2L, user2, workrecord2, sprint2, trackingItem, LocalTime.now(), LocalTime.now(), description);        
        
        //Act
        WorkItemDAO workItemDAO = new WorkItemDAO(connection);
        workItemDAO.create(workItem1);
        workItemDAO.create(workItem2);
        
        //Act
        List<WorkItem> workItems = workItemDAO.selectAll(notFoundUser, sprint1);

        //Assert
        Assert.assertTrue(workItems.isEmpty());
    }    
    
    @Test
    public void T14_Calling_SelectAll_Returns_EmptyList_If_Sprint_NotFound() throws SQLException {
        //Arrange
        String description = "TestDescription";
        
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        RoleDAO roleDAO = new RoleDAO(connection);
        roleDAO.create(role);
        
        Address address = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        AddressDAO addressDAO = new AddressDAO(connection);
        addressDAO.create(address);
        
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        ContractDAO contractDAO = new ContractDAO(connection);
        contractDAO.create(contract);
        
        User user1 = new User(1L, role, address, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        User user2 = new User(2L, role, address, contract, "Gabi", "Golibrzuch", "gabi", "password", 30L);
        UserDAO userDAO = new UserDAO(connection);
        userDAO.create(user1);
        userDAO.create(user2);
        
        Project project = new Project(1L, "TestProject", "AAAA.BBBB.CCCC.DDDD", "TRUE", "TRUE", "TRUE", "TestDescription");
        ProjectDAO projectDAO = new ProjectDAO(connection);
        projectDAO.create(project);
        
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);
        worklocationDAO.create(worklocation);
        
        Workrecord workrecord1 = new Workrecord(1L, user1, project, LocalDate.now(), LocalTime.now(), LocalTime.now(), LocalTime.now(), LocalTime.now().toString(), LocalTime.now().toString(), 0, worklocation, description);
        Workrecord workrecord2 = new Workrecord(2L, user2, project, LocalDate.now(), LocalTime.now(), LocalTime.now(), LocalTime.now(), LocalTime.now().toString(), LocalTime.now().toString(), 0, worklocation, description);
        WorkrecordDAO workrecordDAO = new WorkrecordDAO(connection);
        workrecordDAO.create(workrecord1);
        workrecordDAO.create(workrecord2);
        
        Sprint sprint1 = new Sprint(139L, LocalDate.now(), LocalDate.now(), 10);
        Sprint sprint2 = new Sprint(140L, LocalDate.now(), LocalDate.now(), 10);
        Sprint notFoundSprint = new Sprint(141L, LocalDate.now(), LocalDate.now(), 10);
        SprintDAO sprintDAO = new SprintDAO(connection);
        sprintDAO.create(sprint1);
        sprintDAO.create(sprint2);
        
        TrackingItem trackingItem = new TrackingItem(139L, "Scrum", "S", "All task related to SCRUM activities");
        TrackingItemDAO trackingItemDAO = new TrackingItemDAO(connection);
        trackingItemDAO.create(trackingItem);

        WorkItem workItem1 = new WorkItem(1L, user1, workrecord1, sprint1, trackingItem, LocalTime.now(), LocalTime.now(), description);        
        WorkItem workItem2 = new WorkItem(2L, user2, workrecord2, sprint2, trackingItem, LocalTime.now(), LocalTime.now(), description);        
        
        //Act
        WorkItemDAO workItemDAO = new WorkItemDAO(connection);
        workItemDAO.create(workItem1);
        workItemDAO.create(workItem2);
        
        //Act
        List<WorkItem> workItems = workItemDAO.selectAll(user1, notFoundSprint);

        //Assert
        Assert.assertTrue(workItems.isEmpty());
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
        String description = "TestDescription";
        
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        RoleDAO roleDAO = new RoleDAO(connection);
        roleDAO.create(role);
        
        Address address = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        AddressDAO addressDAO = new AddressDAO(connection);
        addressDAO.create(address);
        
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        ContractDAO contractDAO = new ContractDAO(connection);
        contractDAO.create(contract);
        
        User user = new User(1L, role, address, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        UserDAO userDAO = new UserDAO(connection);
        userDAO.create(user);
        
        Project project = new Project(1L, "TestProject", "AAAA.BBBB.CCCC.DDDD", "TRUE", "TRUE", "TRUE", "TestDescription");
        ProjectDAO projectDAO = new ProjectDAO(connection);
        projectDAO.create(project);
        
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);
        worklocationDAO.create(worklocation);
        
        Workrecord workrecord = new Workrecord(1L, user, project, LocalDate.now(), LocalTime.now(), LocalTime.now(), LocalTime.now(), LocalTime.now().toString(), LocalTime.now().toString(), 0, worklocation, description);
        WorkrecordDAO workrecordDAO = new WorkrecordDAO(connection);
        workrecordDAO.create(workrecord);
        
        Sprint sprint = new Sprint(1L, LocalDate.now(), LocalDate.now(), 10);
        SprintDAO sprintDAO = new SprintDAO(connection);
        sprintDAO.create(sprint);
        
        TrackingItem trackingItem = new TrackingItem(1L, "Scrum", "S", "All task related to SCRUM activities");
        TrackingItemDAO trackingItemDAO = new TrackingItemDAO(connection);
        trackingItemDAO.create(trackingItem);
        
        WorkItem workItem = new WorkItem(1L, user, workrecord, sprint, trackingItem, LocalTime.now(), LocalTime.now(), description);        

        WorkItemDAO workItemDAO = new WorkItemDAO(connection);

        //Act
        boolean result = workItemDAO.create(workItem);

         //Assert
        Assert.assertTrue(result);
    }
    
    @Test(expected = SQLException.class)
    public void T32_Calling_Create_WorkItem_AllreadyExists_Throws_SQLException() throws SQLException {
        //Arrange
        String description = "TestDescription";
        
        Role role = new Role(1L, "Admin", "The Administrator role has access to all application features");
        RoleDAO roleDAO = new RoleDAO(connection);
        roleDAO.create(role);
        
        Address address = new Address(1L, "Humboldtstrasse", 90L, "Etage",2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        AddressDAO addressDAO = new AddressDAO(connection);
        addressDAO.create(address);
        
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        ContractDAO contractDAO = new ContractDAO(connection);
        contractDAO.create(contract);
        
        User user = new User(1L, role, address, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        UserDAO userDAO = new UserDAO(connection);
        userDAO.create(user);
        
        Project project = new Project(1L, "TestProject", "AAAA.BBBB.CCCC.DDDD", "TRUE", "TRUE", "TRUE", "TestDescription");
        ProjectDAO projectDAO = new ProjectDAO(connection);
        projectDAO.create(project);
        
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);
        worklocationDAO.create(worklocation);
        
        Workrecord workrecord = new Workrecord(1L, user, project, LocalDate.now(), LocalTime.now(), LocalTime.now(), LocalTime.now(), LocalTime.now().toString(), LocalTime.now().toString(), 0, worklocation, description);
        WorkrecordDAO workrecordDAO = new WorkrecordDAO(connection);
        workrecordDAO.create(workrecord);
        
        Sprint sprint = new Sprint(1L, LocalDate.now(), LocalDate.now(), 10);
        SprintDAO sprintDAO = new SprintDAO(connection);
        sprintDAO.create(sprint);
        
        TrackingItem trackingItem = new TrackingItem(1L, "Scrum", "S", "All task related to SCRUM activities");
        TrackingItemDAO trackingItemDAO = new TrackingItemDAO(connection);
        trackingItemDAO.create(trackingItem);
        
        WorkItem workItem = new WorkItem(1L, user, workrecord, sprint, trackingItem, LocalTime.now(), LocalTime.now(), description);        

        WorkItemDAO workItemDAO = new WorkItemDAO(connection);
        workItemDAO.create(workItem);

        //Act
         //Assert
        workItemDAO.create(workItem);
    }
    
    @Test(expected = NullPointerException.class)
    public void T40_Calling_Update_OriginalWorkItem_IsNull_Throws_NullPointerException() throws SQLException {
    }
    
    @Test(expected = NullPointerException.class)
    public void T41_Calling_Update_ModifiedWorkItem_IsNull_Throws_NullPointerException() throws SQLException {
    }
    
    @Test()
    public void T42_Calling_Update_Updates_OriginalWorkItem() throws SQLException {
    }
    
    @Test()
    public void T43_Calling_Update_OriginalWorkItem_DoesNotExists_Returns_False() throws SQLException {
    }
    
    @Test()
    public void T44_Calling_Update_On_Different_WorkItem_DoenNotChange_WorkItem() throws SQLException {
    }    

    @Test(expected = NullPointerException.class)
    public void T50_Calling_Delete_WorkItem_IsNull_Throws_NullPointerException() throws SQLException {
    }
    
    @Test()
    public void T51_Calling_Delete_WorkItem_DoesNotExists_Returns_False() throws SQLException {
    }
    
    @Test()
    public void T52_Calling_Delete_WorkItem_Exists_Deletes_Workrecord() throws SQLException {
    }
    
    @Test()
    public void T60_Calling_GetNextId_On_WorkItemTable_Containing_One_WorkItem_Returns_2() throws SQLException {
    }
    
    @Test()
    public void T61_Calling_GetNextId_Twice_On_WorkItemTable_Returns_SameId() throws SQLException {
    }
    
    @Test
    public void T62_Calling_GetNextId_On_Truncated_Sqlite_Sequence_Table_Returns_0() throws SQLException {
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
        statement.append("DELETE FROM user ");
        
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }
        
        statement = new StringBuilder();
        statement.append("DELETE FROM project ");
        
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
        statement.append("DELETE FROM worklocation ");
        
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }
        
        statement = new StringBuilder();
        statement.append("DELETE FROM workrecord ");
        
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }

        statement = new StringBuilder();
        statement.append("DELETE FROM sprint ");
        
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
        statement.append("DELETE FROM workitem ");
        
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
        statement.append("DELETE FROM SQLITE_SEQUENCE WHERE name='Project'");
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

        statement = new StringBuilder();        
        statement.append("DELETE FROM SQLITE_SEQUENCE WHERE name='Contract'");
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }

        statement = new StringBuilder();        
        statement.append("DELETE FROM SQLITE_SEQUENCE WHERE name='Worklocation'");
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }

        statement = new StringBuilder();        
        statement.append("DELETE FROM SQLITE_SEQUENCE WHERE name='Workrecord'");
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }

        statement = new StringBuilder();        
        statement.append("DELETE FROM SQLITE_SEQUENCE WHERE name='Sprint'");
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }

        statement = new StringBuilder();        
        statement.append("DELETE FROM SQLITE_SEQUENCE WHERE name='Trackingitem'");
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }
        
        statement = new StringBuilder();        
        statement.append("DELETE FROM SQLITE_SEQUENCE WHERE name='Workitem'");
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }
        
        return result;
    }
    
}
