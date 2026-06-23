/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sqlite;

import adapter.Log4jAdapter;
import java.time.*;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import model.Address;
import model.Contract;
import model.Project;
import model.Role;
import model.User;
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
 * @author stephan
 */
public class WorkrecordDAOTest {
    
    private final static String APP_LANGUAGE_RESOURCE_KEY = "Language";
    private final static String APP_LANGUAGE_DEFAULT_VALUE = "en";
    private final static String LANGUAGE_RESOURCE = "languages.bundle";
    private final static String DATABASE_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/sqlite/testdb.sqlite";
    private final static String LOG4J2_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/log4j2.xml";
    
    private static ResourceBundle bundle;
    private static ConnectionFactory connectionFactory;
    private static Connection connection;
    
    public WorkrecordDAOTest() throws FileNotFoundException { 
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
        WorkrecordDAO workrecordDAO = new WorkrecordDAO(null);
    }
    
    @Test()
    public void T01_Calling_Ctor_With_Valid_Connection_Returns_Initialized_Instance() {
        //Arrange
        //Act
        WorkrecordDAO workrecordDAO = new WorkrecordDAO(connection);
        
        //Assert
        Assert.assertNotNull(workrecordDAO);
    }
    
    @Test()
    public void T10_Calling_SelectAll_Returns_All_Found_Workrecords() throws SQLException {
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
        
        User user = new User(1L, role, address, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        UserDAO userDAO = new UserDAO(connection);
        userDAO.create(user);
        
        Project project = new Project(1L, "TestProject", "AAAA.BBBB.CCCC.DDDD", "True", "True", "True", "TestDescription");
        ProjectDAO projectDAO = new ProjectDAO(connection);
        projectDAO.create(project);
        
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);
        worklocationDAO.create(worklocation);
        
        Workrecord workrecord1 = new Workrecord(1L, user, project, LocalDate.now(), LocalTime.now(), LocalTime.now(), LocalTime.now(), "01:00:00", "00:00:00", 0, worklocation, "TestDescription");
        Workrecord workrecord2 = new Workrecord(2L, user, project, LocalDate.now(), LocalTime.now(), LocalTime.now(), LocalTime.now(), "01:00:00", "00:00:00", 0, worklocation, "TestDescription");

        WorkrecordDAO workrecordDAO = new WorkrecordDAO(connection);
        workrecordDAO.create(workrecord1);
        workrecordDAO.create(workrecord2);
        
        //Act
        List<Workrecord> workrecordList = workrecordDAO.selectAll();

        //Assert
        Assert.assertEquals(2, workrecordList.size());
        Assert.assertEquals(workrecord1, workrecordList.get(0));
        Assert.assertEquals(workrecord2, workrecordList.get(1));
    }
    
    @Test()
    public void T11_Calling_SelectAll_Returns_EmptyList_If_No_Workrecord_Found() throws SQLException {
        //Arrange
        WorkrecordDAO workrecordDAO = new WorkrecordDAO(connection);
        
        //Act
        List<Workrecord> workrecordList = workrecordDAO.selectAll();

        //Assert
        Assert.assertTrue(workrecordList.isEmpty());
    }
    
    @Test
    public void T12_Calling_SelectAll_Returns_All_Found_Workrecords_That_Match_The_QueryParameter() throws SQLException {
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
        
        Project project = new Project(1L, "TestProject", "AAAA.BBBB.CCCC.DDDD", "True", "True", "True", "TestDescription");
        ProjectDAO projectDAO = new ProjectDAO(connection);
        projectDAO.create(project);
        
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);
        worklocationDAO.create(worklocation);
        
        List<Workrecord> workrecords = new ArrayList<>();
        LocalDate date = LocalDate.of(2024, 01, 01);
        WorkrecordDAO workrecordDAO = new WorkrecordDAO(connection);
        for(int i = 1; i<=31; i++) {
            Workrecord workrecord = new Workrecord((long)i, user1, project, date.plusDays(i), LocalTime.now(), LocalTime.now(), LocalTime.now(), "01:00:00", "00:00:00", 0, worklocation, "TestDescription");
            workrecords.add(workrecord);
            workrecordDAO.create(workrecord);
        }
        for(int i = 32; i<=62; i++) {
            Workrecord workrecord = new Workrecord((long)i, user2, project, date.plusDays(i), LocalTime.now(), LocalTime.now(), LocalTime.now(), "01:00:00", "00:00:00", 0, worklocation, "TestDescription");
            workrecords.add(workrecord);
            workrecordDAO.create(workrecord);
        }
        
        //Act
        LocalDate startDate = LocalDate.of(2024, 01, 10);
        LocalDate endDate = LocalDate.of(2024, 01, 15);
        List<Workrecord> workrecordList = workrecordDAO.selectAll(user1, startDate, endDate);

        //Assert
        Assert.assertEquals(6, workrecordList.size());
        for(int i = 0; i < workrecordList.size(); i++) {
            Assert.assertEquals(workrecords.get(i+8), workrecordList.get(i));
        }
    }
    
    @Test()
    public void T20_Calling_SelectWorkrecordFromId_Returns_The_Stored_Workrecord() throws SQLException {
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
        
        User user = new User(1L, role, address, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        UserDAO userDAO = new UserDAO(connection);
        userDAO.create(user);
        
        Project project = new Project(1L, "TestProject", "AAAA.BBBB.CCCC.DDDD", "True", "True", "True", "TestDescription");
        ProjectDAO projectDAO = new ProjectDAO(connection);
        projectDAO.create(project);
        
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);
        worklocationDAO.create(worklocation);
        
        Workrecord workrecord1 = new Workrecord(1L, user, project, LocalDate.now(), LocalTime.now(), LocalTime.now(), LocalTime.now(), "01:00:00", "00:00:00", 0, worklocation, "TestDescription");
        Workrecord workrecord2 = new Workrecord(2L, user, project, LocalDate.now(), LocalTime.now(), LocalTime.now(), LocalTime.now(), "01:00:00", "00:00:00", 0, worklocation, "TestDescription");

        WorkrecordDAO workrecordDAO = new WorkrecordDAO(connection);
        workrecordDAO.create(workrecord1);
        workrecordDAO.create(workrecord2);

        //Act
        Optional<Workrecord> workrecord = workrecordDAO.selectWorkrecordFromId(1L);

        //Assert
        Assert.assertEquals(workrecord1, workrecord.get());
    }

    @Test()
    public void T21_Calling_SelectWorkrecordFromId_Returns_Null_If_Workrecord_NotFound() throws SQLException {
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
        
        User user = new User(1L, role, address, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        UserDAO userDAO = new UserDAO(connection);
        userDAO.create(user);
        
        Project project = new Project(1L, "TestProject", "AAAA.BBBB.CCCC.DDDD", "True", "True", "True", "TestDescription");
        ProjectDAO projectDAO = new ProjectDAO(connection);
        projectDAO.create(project);
        
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);
        worklocationDAO.create(worklocation);
        
        Workrecord workrecord = new Workrecord(1L, user, project, LocalDate.now(), LocalTime.now(), LocalTime.now(), LocalTime.now(), "01:00:00", "00:00:00", 0, worklocation, "TestDescription");

        WorkrecordDAO workrecordDAO = new WorkrecordDAO(connection);
        workrecordDAO.create(workrecord);
        
        //Act
        Optional<Workrecord> result = workrecordDAO.selectWorkrecordFromId(2L);

        //Assert
        Assert.assertTrue(result.isEmpty());
    }

    @Test(expected = NullPointerException.class)
    public void T30_Calling_Create_Workrecord_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        WorkrecordDAO workrecordDAO = new WorkrecordDAO(connection);

        //Act
        //Assert
        boolean result = workrecordDAO.create(null);
    }

    @Test()
    public void T31_Calling_Create_Stores_Workrecord() throws SQLException {
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
        
        User user = new User(1L, role, address, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        UserDAO userDAO = new UserDAO(connection);
        userDAO.create(user);
        
        Project project = new Project(1L, "TestProject", "AAAA.BBBB.CCCC.DDDD", "True", "True", "True", "TestDescription");
        ProjectDAO projectDAO = new ProjectDAO(connection);
        projectDAO.create(project);
        
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);
        worklocationDAO.create(worklocation);
        
        Workrecord workrecord = new Workrecord(1L, user, project, LocalDate.now(), LocalTime.now(), LocalTime.now(), LocalTime.now(), "01:00:00", "00:00:00", 0, worklocation, "TestDescription");

        WorkrecordDAO workrecordDAO = new WorkrecordDAO(connection);
        
        //Act
        boolean result = workrecordDAO.create(workrecord);
        
        //Assert
        Assert.assertTrue(result);
    }
    
    @Test(expected = SQLException.class)
    public void T32_Calling_Create_Workrecord_AllreadyExists_Throws_SQLException() throws SQLException {
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
        
        User user = new User(1L, role, address, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        UserDAO userDAO = new UserDAO(connection);
        userDAO.create(user);
        
        Project project = new Project(1L, "TestProject", "AAAA.BBBB.CCCC.DDDD", "True", "True", "True", "TestDescription");
        ProjectDAO projectDAO = new ProjectDAO(connection);
        projectDAO.create(project);
        
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);
        worklocationDAO.create(worklocation);

        Workrecord workrecord = new Workrecord(1L, user, project, LocalDate.now(), LocalTime.now(), LocalTime.now(), LocalTime.now(), "01:00:00", "00:00:00", 0, worklocation, "TestDescription");

        WorkrecordDAO workrecordDAO = new WorkrecordDAO(connection);
        workrecordDAO.create(workrecord);
        
        //Act
        boolean result = workrecordDAO.create(workrecord);        

        //Assert
        Assert.assertFalse(result);
    }

    @Test(expected = NullPointerException.class)
    public void T40_Calling_Update_OriginalWorkrecord_IsNull_Throws_NullPointerException() throws SQLException {
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
        
        User user = new User(1L, role, address, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        UserDAO userDAO = new UserDAO(connection);
        userDAO.create(user);
        
        Project project = new Project(1L, "TestProject", "AAAA.BBBB.CCCC.DDDD", "True", "True", "True", "TestDescription");
        ProjectDAO projectDAO = new ProjectDAO(connection);
        projectDAO.create(project);
        
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);
        worklocationDAO.create(worklocation);

        Workrecord modifiedWorkrecord = new Workrecord(1L, user, project, LocalDate.now(), LocalTime.now(), LocalTime.now(), LocalTime.now(), "01:00:00", "00:00:00", 0, worklocation, "TestDescription");

        WorkrecordDAO workrecordDAO = new WorkrecordDAO(connection);

        //Act
        boolean result = workrecordDAO.update(null, modifiedWorkrecord);

        //Assert
        Assert.assertFalse(result);
    }

    @Test(expected = NullPointerException.class)
    public void T41_Calling_Update_ModifiedWorkrecord_IsNull_Throws_NullPointerException() throws SQLException {
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
        
        User user = new User(1L, role, address, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        UserDAO userDAO = new UserDAO(connection);
        userDAO.create(user);
        
        Project project = new Project(1L, "TestProject", "AAAA.BBBB.CCCC.DDDD", "True", "True", "True", "TestDescription");
        ProjectDAO projectDAO = new ProjectDAO(connection);
        projectDAO.create(project);
        
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);
        worklocationDAO.create(worklocation);

        Workrecord originalWorkrecord = new Workrecord(1L, user, project, LocalDate.now(), LocalTime.now(), LocalTime.now(), LocalTime.now(), "01:00:00", "00:00:00", 0, worklocation, "TestDescription");

        WorkrecordDAO workrecordDAO = new WorkrecordDAO(connection);

        //Act
        boolean result = workrecordDAO.update(originalWorkrecord, null);

        //Assert
        Assert.assertFalse(result);
    }

    @Test()
    public void T42_Calling_Update_Updates_OriginalWorkrecord() throws SQLException {
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
        
        User user = new User(1L, role, address, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        UserDAO userDAO = new UserDAO(connection);
        userDAO.create(user);
        
        Project project = new Project(1L, "TestProject", "AAAA.BBBB.CCCC.DDDD", "True", "True", "True", "TestDescription");
        ProjectDAO projectDAO = new ProjectDAO(connection);
        projectDAO.create(project);
        
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);
        worklocationDAO.create(worklocation);

        Workrecord originalWorkrecord = new Workrecord(1L, user, project, LocalDate.now(), LocalTime.now(), LocalTime.now(), LocalTime.now(), "01:00:00", "00:00:00", 0, worklocation, "TestDescription");
        Workrecord modifiedWorkrecord = new Workrecord(1L, user, project, LocalDate.now().plusDays(1), LocalTime.now(), LocalTime.now(), LocalTime.now(), "01:00:00", "00:00:00", 0, worklocation, "TestDescription");

        WorkrecordDAO workrecordDAO = new WorkrecordDAO(connection);
        workrecordDAO.create(originalWorkrecord);
        
        //Act
        boolean result = workrecordDAO.update(originalWorkrecord, modifiedWorkrecord);
        Optional<Workrecord> userResult = workrecordDAO.selectWorkrecordFromId(modifiedWorkrecord.getId());
        
        //Assert
        Assert.assertTrue(result);
        Assert.assertEquals(modifiedWorkrecord, userResult.get());
    }

    @Test()
    public void T43_Calling_Update_OriginalWorkrecord_DoesNotExists_Returns_False() throws SQLException {
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
        
        User user = new User(1L, role, address, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        UserDAO userDAO = new UserDAO(connection);
        userDAO.create(user);
        
        Project project = new Project(1L, "TestProject", "AAAA.BBBB.CCCC.DDDD", "True", "True", "True", "TestDescription");
        ProjectDAO projectDAO = new ProjectDAO(connection);
        projectDAO.create(project);
        
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);
        worklocationDAO.create(worklocation);

        Workrecord originalWorkrecord = new Workrecord(1L, user, project, LocalDate.now(), LocalTime.now(), LocalTime.now(), LocalTime.now(), "01:00:00", "00:00:00", 0, worklocation, "TestDescription");
        Workrecord modifiedWorkrecord = new Workrecord(1L, user, project, LocalDate.now().plusDays(1), LocalTime.now(), LocalTime.now(), LocalTime.now(), "01:00:00", "00:00:00", 0, worklocation, "TestDescription");

        WorkrecordDAO workrecordDAO = new WorkrecordDAO(connection);
        
        //Act
        boolean result = workrecordDAO.update(originalWorkrecord, modifiedWorkrecord);
        
        //Assert
        Assert.assertFalse(result);
    }
    
    @Test()
    public void T44_Calling_Update_On_Different_Workrecords_DoenNotChange_Workrecord() throws SQLException {
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
        
        User user = new User(1L, role, address, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        UserDAO userDAO = new UserDAO(connection);
        userDAO.create(user);
        
        Project project = new Project(1L, "TestProject", "AAAA.BBBB.CCCC.DDDD", "True", "True", "True", "TestDescription");
        ProjectDAO projectDAO = new ProjectDAO(connection);
        projectDAO.create(project);
        
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);
        worklocationDAO.create(worklocation);

        Workrecord originalWorkrecord = new Workrecord(1L, user, project, LocalDate.now(), LocalTime.now(), LocalTime.now(), LocalTime.now(), "01:00:00", "00:00:00", 0, worklocation, "TestDescription");
        Workrecord modifiedWorkrecord = new Workrecord(2L, user, project, LocalDate.now(), LocalTime.now(), LocalTime.now(), LocalTime.now(), "01:00:00", "00:00:00", 0, worklocation, "TestDescription");

        WorkrecordDAO workrecordDAO = new WorkrecordDAO(connection);
        workrecordDAO.create(originalWorkrecord);
        
        //Act
        boolean result = workrecordDAO.update(originalWorkrecord, modifiedWorkrecord);
        Optional<Workrecord> workrecordResult = workrecordDAO.selectWorkrecordFromId(originalWorkrecord.getId());

        //Assert
        Assert.assertFalse(result);
        Assert.assertEquals(originalWorkrecord, workrecordResult.get());
    }

    @Test(expected = NullPointerException.class)
    public void T50_Calling_Delete_Workrecord_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        WorkrecordDAO workrecordDAO = new WorkrecordDAO(connection);

        //Act
        //Assert
        boolean result = workrecordDAO.delete(null);
    }
    
    @Test()
    public void T51_Calling_Delete_Workrecord_DoesNotExists_Returns_False() throws SQLException {
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
        
        User user = new User(1L, role, address, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        UserDAO userDAO = new UserDAO(connection);
        userDAO.create(user);
        
        Project project = new Project(1L, "TestProject", "AAAA.BBBB.CCCC.DDDD", "True", "True", "True", "TestDescription");
        ProjectDAO projectDAO = new ProjectDAO(connection);
        projectDAO.create(project);
        
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);
        worklocationDAO.create(worklocation);

        Workrecord workrecord = new Workrecord(1L, user, project, LocalDate.now(), LocalTime.now(), LocalTime.now(), LocalTime.now(), "01:00:00", "00:00:00", 0, worklocation, "TestDescription");

        WorkrecordDAO workrecordDAO = new WorkrecordDAO(connection);

        //Act
        boolean result = workrecordDAO.delete(workrecord);

        //Assert
        Assert.assertFalse(result);
    }

    @Test()
    public void T52_Calling_Delete_Workrecord_Exists_Deletes_Workrecord() throws SQLException {
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
        
        User user = new User(1L, role, address, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        UserDAO userDAO = new UserDAO(connection);
        userDAO.create(user);
        
        Project project = new Project(1L, "TestProject", "AAAA.BBBB.CCCC.DDDD", "True", "True", "True", "TestDescription");
        ProjectDAO projectDAO = new ProjectDAO(connection);
        projectDAO.create(project);
        
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);
        worklocationDAO.create(worklocation);

        Workrecord workrecord = new Workrecord(1L, user, project, LocalDate.now(), LocalTime.now(), LocalTime.now(), LocalTime.now(), "01:00:00", "00:00:00", 0, worklocation, "TestDescription");

        WorkrecordDAO workrecordDAO = new WorkrecordDAO(connection);
        workrecordDAO.create(workrecord);
        
        //Act
        boolean result = workrecordDAO.delete(workrecord);
        Optional<Workrecord> workrecordResult = workrecordDAO.selectWorkrecordFromId(workrecord.getId());

        //Assert
        Assert.assertTrue(result);
        Assert.assertTrue(workrecordResult.isEmpty());
    }
   
    @Test()
    public void T60_Calling_GetNextId_On_WorkrecordTable_Containing_One_Record_Returns_2() throws SQLException {
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
        
        User user = new User(1L, role, address, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        UserDAO userDAO = new UserDAO(connection);
        userDAO.create(user);
        
        Project project = new Project(1L, "TestProject", "AAAA.BBBB.CCCC.DDDD", "True", "True", "True", "TestDescription");
        ProjectDAO projectDAO = new ProjectDAO(connection);
        projectDAO.create(project);
        
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);
        worklocationDAO.create(worklocation);

        Workrecord workrecord = new Workrecord(1L, user, project, LocalDate.now(), LocalTime.now(), LocalTime.now(), LocalTime.now(), "01:00:00", "00:00:00", 0, worklocation, "TestDescription");

        WorkrecordDAO workrecordDAO = new WorkrecordDAO(connection);
        workrecordDAO.create(workrecord);
        
        //Act
        long receivedId = workrecordDAO.getNextId();
        
        //Assert
        Assert.assertEquals(2L, receivedId);
    }
    
    @Test()
    public void T61_Calling_GetNextId_Twice_On_WorkrecordTable_Returns_SameId() throws SQLException {
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
        
        User user = new User(1L, role, address, contract, "Stephan", "Reimann", "stephan", "password", 30L);
        UserDAO userDAO = new UserDAO(connection);
        userDAO.create(user);
        
        Project project = new Project(1L, "TestProject", "AAAA.BBBB.CCCC.DDDD", "True", "True", "True", "TestDescription");
        ProjectDAO projectDAO = new ProjectDAO(connection);
        projectDAO.create(project);
        
        Worklocation worklocation = new Worklocation(1L, "Homeoffice", "Worklocation is Homeoffice");
        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);
        worklocationDAO.create(worklocation);

        Workrecord workrecord = new Workrecord(1L, user, project, LocalDate.now(), LocalTime.now(), LocalTime.now(), LocalTime.now(), "01:00:00", "00:00:00", 0, worklocation, "TestDescription");

        WorkrecordDAO workrecordDAO = new WorkrecordDAO(connection);
        workrecordDAO.create(workrecord);
        
        //Act
        long receivedIdFirstCall = workrecordDAO.getNextId();
        long receivedIdSecondCall = workrecordDAO.getNextId();
        
        //Assert
        Assert.assertEquals(receivedIdSecondCall, receivedIdFirstCall);
    }

    @Test
    public void T62_Calling_GetNextId_On_Truncated_Sqlite_Sequence_Table_Returns_0() throws SQLException {
        //Arrange
        WorkrecordDAO workrecordDAO = new WorkrecordDAO(connection);
        
        //Act
        long resultId = workrecordDAO.getNextId();

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

        return result;
    }
    
}
