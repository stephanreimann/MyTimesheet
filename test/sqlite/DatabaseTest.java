/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sqlite;

import adapter.Log4jAdapter;
import java.io.*;
import java.nio.file.DirectoryNotEmptyException;
import java.sql.SQLException;
import java.util.ResourceBundle;
import org.junit.*;
import static org.junit.Assert.assertNotNull;
import org.mockito.Mockito;
import sqlite.Database.TableInfo;

/**
 *
 * @author adrest18
 */
public class DatabaseTest {

    public DatabaseTest() {
        
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
        String userDir = "user.dir";
        String sqLiteDir = "/sqlite/"; 
        String fileName = "TestFile.sqlite";
        String pathToSqLiteDatabase = System.getProperty(userDir).concat(sqLiteDir).concat(fileName);
    
        File file = new File(pathToSqLiteDatabase);
        file.delete();
    }

    @Test(expected = NullPointerException.class)
    public void T00_Calling_Ctor_With_ResourceBundleIsNull_Throws_NullPointerException() {
        //Arrange
        Log4jAdapter log4jAdapterMock = Mockito.mock(Log4jAdapter.class);
        
        //Act
        //Assert
        Database database = new Database(null, log4jAdapterMock);
    }
    
    @Test(expected=NullPointerException.class)
    public void T01_Calling_Ctor_With_Log4jAdapter_IsNull_Throws_NullPointerException() {
        //Arrange
        ResourceBundle resourceBundleMock = Mockito.mock(ResourceBundle.class);
        
        //Act
        //Assert
        Database database = new Database(resourceBundleMock, null);
    }
    
    @Test
    public void T02_Ctor_Called_Returns_Instance() {
        //Arrange
        ResourceBundle resourceBundleMock = Mockito.mock(ResourceBundle.class);
        Log4jAdapter log4jAdapterMock = Mockito.mock(Log4jAdapter.class);
        
        //Act
        Database database = new Database(resourceBundleMock, log4jAdapterMock);
        
        //Assert
        assertNotNull(database);
    }
    
    @Test
    public void T10_CreateDatabaseFile_Creates_The_DatabseFile() throws SQLException {
        //Arrange
        String userDir = "user.dir";
        String sqLiteDir = "/sqlite/"; 
        String fileName = "TestFile.sqlite";
        String pathToSqLiteDatabase = System.getProperty(userDir).concat(sqLiteDir).concat(fileName);

        ResourceBundle resourceBundleMock = Mockito.mock(ResourceBundle.class);
        Log4jAdapter log4jAdapterMock = Mockito.mock(Log4jAdapter.class);
        
        Database database = new Database(resourceBundleMock, log4jAdapterMock);

        //Act
        database.createDatabase(fileName);
        
        //Assert
        File file = new File(pathToSqLiteDatabase);
        Assert.assertTrue(file.exists());
    }

    @Test
    public void T11_CreateDatabaseFile_CalledTwice_DoesNotCreateTheDatabaseFileTwice() throws SQLException {
        //Arrange
        String userDir = "user.dir";
        String sqLiteDir = "/sqlite/"; 
        String fileName = "TestFile.sqlite";
        String pathToSqLiteDatabase = System.getProperty(userDir).concat(sqLiteDir).concat(fileName);
        
        ResourceBundle resourceBundleMock = Mockito.mock(ResourceBundle.class);
        Log4jAdapter log4jAdapterMock = Mockito.mock(Log4jAdapter.class);
        
        Database database = new Database(resourceBundleMock, log4jAdapterMock);
        database.createDatabase(fileName);
        File file1 = new File(pathToSqLiteDatabase);
        long lastModified = file1.lastModified();
        
        //Act
        database.createDatabase(fileName);
        
        //Assert
        File file2 = new File(pathToSqLiteDatabase);
        Assert.assertTrue(file1.exists());
        Assert.assertEquals(lastModified, file2.lastModified());
        
        file2.delete();
        Assert.assertFalse(file2.exists());
    }
    
    @Test
    public void T20_DeleteDatabaseFile_Deletes_The_ExistingDatabaseFile() throws DirectoryNotEmptyException, IOException, SQLException {
        //Arrange
        String userDir = "user.dir";
        String sqLiteDir = "/sqlite/"; 
        String fileName = "TestFile.sqlite";
        String pathToSqLiteDatabase = System.getProperty(userDir).concat(sqLiteDir).concat(fileName);

        ResourceBundle resourceBundleMock = Mockito.mock(ResourceBundle.class);
        Log4jAdapter log4jAdapterMock = Mockito.mock(Log4jAdapter.class);
        
        Database database = new Database(resourceBundleMock, log4jAdapterMock);
        database.createDatabase(fileName);
        
        //Act
        database.deleteDatabaseFile(fileName);
        
        //Assert
        File file = new File(pathToSqLiteDatabase);
        Assert.assertFalse(file.exists());  
    }
    
    @Test
    public void T21_DeleteDatabaseFile_OnANotExistingDatabaseFile_Does_Nothing() throws DirectoryNotEmptyException, IOException, SQLException {
        //Arrange
        String userDir = "user.dir";
        String sqLiteDir = "/sqlite/"; 
        String fileName = "TestFile.sqlite";
        String pathToSqLiteDatabase = System.getProperty(userDir).concat(sqLiteDir).concat(fileName);

        ResourceBundle resourceBundleMock = Mockito.mock(ResourceBundle.class);
        Log4jAdapter log4jAdapterMock = Mockito.mock(Log4jAdapter.class);
        
        Database database = new Database(resourceBundleMock, log4jAdapterMock);
        database.createDatabase(fileName);
        database.deleteDatabaseFile(fileName);
        
        //Act
        database.deleteDatabaseFile(fileName);
        
        //Assert
        File file = new File(pathToSqLiteDatabase);
        Assert.assertFalse(file.exists());  
    }

    @Test
    public void T30_CreateAddressTable_Creates_AddressTable() throws SQLException, IOException {
        //Arrange
        String userDir = "user.dir";
        String sqLiteDir = "/sqlite/"; 
        String fileName = "TestFile.sqlite";
        String pathToSqLiteDatabase = System.getProperty(userDir).concat(sqLiteDir).concat(fileName);

        ResourceBundle resourceBundleMock = Mockito.mock(ResourceBundle.class);
        Log4jAdapter log4jAdapterMock = Mockito.mock(Log4jAdapter.class);
        
        Database database = new Database(resourceBundleMock, log4jAdapterMock);
        database.deleteDatabaseFile(fileName);
        database.createDatabase(fileName);

        //Act
        boolean result = database.createAddressTableIfNotExists(fileName);
        
        TableInfo tableInfo = database.getTableInfo(fileName, "Address");
        
        //Assert
        Assert.assertTrue(result);
        Assert.assertEquals("Address", tableInfo.getName());
        
        Assert.assertEquals("Id", tableInfo.columnInfos.get(0).getColName());
        Assert.assertEquals("INTEGER", tableInfo.columnInfos.get(0).getTypeName());
        Assert.assertEquals("YES", tableInfo.columnInfos.get(0).getAutoIncrement());
        
        Assert.assertEquals("Streetname", tableInfo.columnInfos.get(1).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(1).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(1).getAutoIncrement());
        
        Assert.assertEquals("Housenumber", tableInfo.columnInfos.get(2).getColName());
        Assert.assertEquals("INTEGER", tableInfo.columnInfos.get(2).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(2).getAutoIncrement());
        
        Assert.assertEquals("Unitname", tableInfo.columnInfos.get(3).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(3).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(3).getAutoIncrement());
        
        Assert.assertEquals("Unitnumber", tableInfo.columnInfos.get(4).getColName());
        Assert.assertEquals("INTEGER", tableInfo.columnInfos.get(4).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(4).getAutoIncrement());
        
        Assert.assertEquals("UnitLocation", tableInfo.columnInfos.get(5).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(5).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(5).getAutoIncrement());
        
        Assert.assertEquals("City", tableInfo.columnInfos.get(6).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(6).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(6).getAutoIncrement());
        
        Assert.assertEquals("State", tableInfo.columnInfos.get(7).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(7).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(7).getAutoIncrement());
        
        Assert.assertEquals("ZipCode", tableInfo.columnInfos.get(8).getColName());
        Assert.assertEquals("INTEGER", tableInfo.columnInfos.get(8).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(8).getAutoIncrement());
        
        Assert.assertEquals("Country", tableInfo.columnInfos.get(9).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(9).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(9).getAutoIncrement());
    }

    @Test
    public void T31_CreateContractTable_Creates_ContractTable() throws SQLException, IOException {
        //Arrange
        String userDir = "user.dir";
        String sqLiteDir = "/sqlite/"; 
        String fileName = "TestFile.sqlite";
        String pathToSqLiteDatabase = System.getProperty(userDir).concat(sqLiteDir).concat(fileName);

        ResourceBundle resourceBundleMock = Mockito.mock(ResourceBundle.class);
        Log4jAdapter log4jAdapterMock = Mockito.mock(Log4jAdapter.class);
        
        Database database = new Database(resourceBundleMock, log4jAdapterMock);
        database.deleteDatabaseFile(fileName);
        database.createDatabase(fileName);

        //Act
        boolean result = database.createContractTableIfNotExists(fileName);
        
        TableInfo tableInfo = database.getTableInfo(fileName, "Contract");
        
        //Assert
        Assert.assertTrue(result);
        Assert.assertEquals("Contract", tableInfo.getName());
        
        Assert.assertEquals("Id", tableInfo.columnInfos.get(0).getColName());
        Assert.assertEquals("INTEGER", tableInfo.columnInfos.get(0).getTypeName());
        Assert.assertEquals("YES", tableInfo.columnInfos.get(0).getAutoIncrement());
        
        Assert.assertEquals("Name", tableInfo.columnInfos.get(1).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(1).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(1).getAutoIncrement());
        
        Assert.assertEquals("Workhours", tableInfo.columnInfos.get(2).getColName());
        Assert.assertEquals("INTEGER", tableInfo.columnInfos.get(2).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(2).getAutoIncrement());
        
        Assert.assertEquals("Maxworkhours", tableInfo.columnInfos.get(3).getColName());
        Assert.assertEquals("INTEGER", tableInfo.columnInfos.get(3).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(3).getAutoIncrement());
        
        Assert.assertEquals("Vacationdays", tableInfo.columnInfos.get(4).getColName());
        Assert.assertEquals("INTEGER", tableInfo.columnInfos.get(4).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(4).getAutoIncrement());
        
        Assert.assertEquals("Vacationreconciliationdate", tableInfo.columnInfos.get(5).getColName());
        Assert.assertEquals("STRING", tableInfo.columnInfos.get(5).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(5).getAutoIncrement());
        
        Assert.assertEquals("Breakfastofftimeend", tableInfo.columnInfos.get(6).getColName());
        Assert.assertEquals("INTEGER", tableInfo.columnInfos.get(6).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(6).getAutoIncrement());
        
        Assert.assertEquals("Breakfastofftimestart", tableInfo.columnInfos.get(7).getColName());
        Assert.assertEquals("INTEGER", tableInfo.columnInfos.get(7).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(7).getAutoIncrement());
        
        Assert.assertEquals("Lunchofftimeend", tableInfo.columnInfos.get(8).getColName());
        Assert.assertEquals("INTEGER", tableInfo.columnInfos.get(8).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(8).getAutoIncrement());
        
        Assert.assertEquals("Lunchofftimestart", tableInfo.columnInfos.get(9).getColName());
        Assert.assertEquals("INTEGER", tableInfo.columnInfos.get(9).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(9).getAutoIncrement());

        Assert.assertEquals("Earliestworktimestart", tableInfo.columnInfos.get(10).getColName());
        Assert.assertEquals("TIME", tableInfo.columnInfos.get(10).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(10).getAutoIncrement());

        Assert.assertEquals("Latestworktimeend", tableInfo.columnInfos.get(11).getColName());
        Assert.assertEquals("TIME", tableInfo.columnInfos.get(11).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(11).getAutoIncrement());
    }

    @Test
    public void T32_CreateHolydayTable_Creates_HolydayTable() throws IOException, SQLException {
        //Arrange
        String userDir = "user.dir";
        String sqLiteDir = "/sqlite/"; 
        String fileName = "TestFile.sqlite";
        String pathToSqLiteDatabase = System.getProperty(userDir).concat(sqLiteDir).concat(fileName);

        ResourceBundle resourceBundleMock = Mockito.mock(ResourceBundle.class);
        Log4jAdapter log4jAdapterMock = Mockito.mock(Log4jAdapter.class);
        
        Database database = new Database(resourceBundleMock, log4jAdapterMock);
        database.deleteDatabaseFile(fileName);
        database.createDatabase(fileName);

        //Act
        boolean result = database.createHolydayTableIfNotExists(fileName);
        
        TableInfo tableInfo = database.getTableInfo(fileName, "Holyday");
        
        //Assert
        Assert.assertTrue(result);
        Assert.assertEquals("Holyday", tableInfo.getName());
        
        Assert.assertEquals("Id", tableInfo.columnInfos.get(0).getColName());
        Assert.assertEquals("INTEGER", tableInfo.columnInfos.get(0).getTypeName());
        Assert.assertEquals("YES", tableInfo.columnInfos.get(0).getAutoIncrement());
        
        Assert.assertEquals("Date", tableInfo.columnInfos.get(1).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(1).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(1).getAutoIncrement());
        
        Assert.assertEquals("Name", tableInfo.columnInfos.get(2).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(2).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(2).getAutoIncrement());

        Assert.assertEquals("State", tableInfo.columnInfos.get(3).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(3).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(3).getAutoIncrement());
    }
    
    @Test
    public void T33_CreateProjectTable_Creates_ProjectTable() throws IOException, SQLException {
        //Arrange
        String userDir = "user.dir";
        String sqLiteDir = "/sqlite/"; 
        String fileName = "TestFile.sqlite";
        String pathToSqLiteDatabase = System.getProperty(userDir).concat(sqLiteDir).concat(fileName);

        ResourceBundle resourceBundleMock = Mockito.mock(ResourceBundle.class);
        Log4jAdapter log4jAdapterMock = Mockito.mock(Log4jAdapter.class);
        
        Database database = new Database(resourceBundleMock, log4jAdapterMock);
        database.deleteDatabaseFile(fileName);
        database.createDatabase(fileName);

        //Act
        boolean result = database.createProjectTableIfNotExists(fileName);
        
        TableInfo tableInfo = database.getTableInfo(fileName, "Project");
        
        //Assert
        Assert.assertTrue(result);
        Assert.assertEquals("Project", tableInfo.getName());
        
        Assert.assertEquals("Id", tableInfo.columnInfos.get(0).getColName());
        Assert.assertEquals("INTEGER", tableInfo.columnInfos.get(0).getTypeName());
        Assert.assertEquals("YES", tableInfo.columnInfos.get(0).getAutoIncrement());

        Assert.assertEquals("Name", tableInfo.columnInfos.get(1).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(1).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(1).getAutoIncrement());

        Assert.assertEquals("Costunit", tableInfo.columnInfos.get(2).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(2).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(2).getAutoIncrement());

        Assert.assertEquals("IsWorktimeRelevant", tableInfo.columnInfos.get(3).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(3).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(3).getAutoIncrement());

        Assert.assertEquals("IsVacationRelevant", tableInfo.columnInfos.get(4).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(4).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(4).getAutoIncrement());

        Assert.assertEquals("IsComptimeRelevant", tableInfo.columnInfos.get(5).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(5).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(5).getAutoIncrement());

        Assert.assertEquals("Description", tableInfo.columnInfos.get(6).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(6).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(6).getAutoIncrement());
    }
    
    @Test
    public void T34_CreateRoleTable_Creates_RoleTable() throws SQLException, IOException {
        //Arrange
        String userDir = "user.dir";
        String sqLiteDir = "/sqlite/"; 
        String fileName = "TestFile.sqlite";
        String pathToSqLiteDatabase = System.getProperty(userDir).concat(sqLiteDir).concat(fileName);

        ResourceBundle resourceBundleMock = Mockito.mock(ResourceBundle.class);
        Log4jAdapter log4jAdapterMock = Mockito.mock(Log4jAdapter.class);
        
        Database database = new Database(resourceBundleMock, log4jAdapterMock);
        database.deleteDatabaseFile(fileName);
        database.createDatabase(fileName);

        //Act
        boolean result = database.createRoleTableIfNotExists(fileName);
        
        TableInfo tableInfo = database.getTableInfo(fileName, "Role");
        
        //Assert
        Assert.assertTrue(result);
        Assert.assertEquals("Role", tableInfo.getName());
        
        Assert.assertEquals("Id", tableInfo.columnInfos.get(0).getColName());
        Assert.assertEquals("INTEGER", tableInfo.columnInfos.get(0).getTypeName());
        Assert.assertEquals("YES", tableInfo.columnInfos.get(0).getAutoIncrement());
        
        Assert.assertEquals("Name", tableInfo.columnInfos.get(1).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(1).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(1).getAutoIncrement());
        
        Assert.assertEquals("Description", tableInfo.columnInfos.get(2).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(2).getTypeName());        
        Assert.assertEquals("NO", tableInfo.columnInfos.get(2).getAutoIncrement());
    }

    @Test
    public void T35_CreateUserTable_Creates_UserTable() throws SQLException, IOException {
        //Arrange
        String userDir = "user.dir";
        String sqLiteDir = "/sqlite/"; 
        String fileName = "TestFile.sqlite";
        String pathToSqLiteDatabase = System.getProperty(userDir).concat(sqLiteDir).concat(fileName);

        ResourceBundle resourceBundleMock = Mockito.mock(ResourceBundle.class);
        Log4jAdapter log4jAdapterMock = Mockito.mock(Log4jAdapter.class);
        
        Database database = new Database(resourceBundleMock, log4jAdapterMock);
        database.deleteDatabaseFile(fileName);
        database.createDatabase(fileName);

        //Act
        boolean result = database.createUserTableIfNotExists(fileName);
        
        TableInfo tableInfo = database.getTableInfo(fileName, "User");
        
        //Assert
        Assert.assertTrue(result);
        Assert.assertEquals("User", tableInfo.getName());
        
        Assert.assertEquals("Id", tableInfo.columnInfos.get(0).getColName());
        Assert.assertEquals("INTEGER", tableInfo.columnInfos.get(0).getTypeName());
        Assert.assertEquals("YES", tableInfo.columnInfos.get(0).getAutoIncrement());
        
        Assert.assertEquals("RoleId", tableInfo.columnInfos.get(1).getColName());
        Assert.assertEquals("INTEGER", tableInfo.columnInfos.get(1).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(1).getAutoIncrement());
        
        Assert.assertEquals("AddressId", tableInfo.columnInfos.get(2).getColName());
        Assert.assertEquals("INTEGER", tableInfo.columnInfos.get(2).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(2).getAutoIncrement());
        
        Assert.assertEquals("ContractId", tableInfo.columnInfos.get(3).getColName());
        Assert.assertEquals("INTEGER", tableInfo.columnInfos.get(3).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(3).getAutoIncrement());
        
        Assert.assertEquals("FirstName", tableInfo.columnInfos.get(4).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(4).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(4).getAutoIncrement());

        Assert.assertEquals("LastName", tableInfo.columnInfos.get(5).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(5).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(5).getAutoIncrement());
        
        Assert.assertEquals("Login", tableInfo.columnInfos.get(6).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(6).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(6).getAutoIncrement());
        
        Assert.assertEquals("Password", tableInfo.columnInfos.get(7).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(7).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(7).getAutoIncrement());
        
        Assert.assertEquals("VacationLeft", tableInfo.columnInfos.get(8).getColName());
        Assert.assertEquals("INTEGER", tableInfo.columnInfos.get(8).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(8).getAutoIncrement());
    }

    @Test
    public void T36_CreateWorklocationTable_Creates_WorklocationTable() throws IOException, SQLException {
        //Arrange
        String userDir = "user.dir";
        String sqLiteDir = "/sqlite/"; 
        String fileName = "TestFile.sqlite";
        String pathToSqLiteDatabase = System.getProperty(userDir).concat(sqLiteDir).concat(fileName);

        ResourceBundle resourceBundleMock = Mockito.mock(ResourceBundle.class);
        Log4jAdapter log4jAdapterMock = Mockito.mock(Log4jAdapter.class);
        
        Database database = new Database(resourceBundleMock, log4jAdapterMock);
        database.deleteDatabaseFile(fileName);
        database.createDatabase(fileName);

        //Act
        boolean result = database.createWorklocationTableIfNotExists(fileName);
        
        TableInfo tableInfo = database.getTableInfo(fileName, "Worklocation");
        
        //Assert
        Assert.assertTrue(result);
        Assert.assertEquals("Worklocation", tableInfo.getName());
        
        Assert.assertEquals("Id", tableInfo.columnInfos.get(0).getColName());
        Assert.assertEquals("INTEGER", tableInfo.columnInfos.get(0).getTypeName());
        Assert.assertEquals("YES", tableInfo.columnInfos.get(0).getAutoIncrement());

        Assert.assertEquals("Name", tableInfo.columnInfos.get(1).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(1).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(1).getAutoIncrement());

        Assert.assertEquals("Description", tableInfo.columnInfos.get(2).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(2).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(2).getAutoIncrement());
    }
    
    @Test
    public void T37_CreateWorkrecordTable_Creates_WorkrecordTable() throws IOException, SQLException {
        //Arrange
        String userDir = "user.dir";
        String sqLiteDir = "/sqlite/"; 
        String fileName = "TestFile.sqlite";
        String pathToSqLiteDatabase = System.getProperty(userDir).concat(sqLiteDir).concat(fileName);

        ResourceBundle resourceBundleMock = Mockito.mock(ResourceBundle.class);
        Log4jAdapter log4jAdapterMock = Mockito.mock(Log4jAdapter.class);
        
        Database database = new Database(resourceBundleMock, log4jAdapterMock);
        database.deleteDatabaseFile(fileName);
        database.createDatabase(fileName);

        //Act
        boolean result = database.createWorkrecordTableIfNotExists(fileName);
        
        TableInfo tableInfo = database.getTableInfo(fileName, "Workrecord");
        
        //Assert
        Assert.assertTrue(result);
        Assert.assertEquals("Workrecord", tableInfo.getName());
        
        Assert.assertEquals("Id", tableInfo.columnInfos.get(0).getColName());
        Assert.assertEquals("INTEGER", tableInfo.columnInfos.get(0).getTypeName());
        Assert.assertEquals("YES", tableInfo.columnInfos.get(0).getAutoIncrement());

        Assert.assertEquals("UserId", tableInfo.columnInfos.get(1).getColName());
        Assert.assertEquals("INTEGER", tableInfo.columnInfos.get(1).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(1).getAutoIncrement());

        Assert.assertEquals("ProjectId", tableInfo.columnInfos.get(2).getColName());
        Assert.assertEquals("INTEGER", tableInfo.columnInfos.get(2).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(2).getAutoIncrement());

        Assert.assertEquals("Date", tableInfo.columnInfos.get(3).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(3).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(3).getAutoIncrement());

        Assert.assertEquals("StartTime", tableInfo.columnInfos.get(4).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(4).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(4).getAutoIncrement());

        Assert.assertEquals("EndTime", tableInfo.columnInfos.get(5).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(5).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(5).getAutoIncrement());

        Assert.assertEquals("WorkTime", tableInfo.columnInfos.get(6).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(6).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(6).getAutoIncrement());

        Assert.assertEquals("OverTime", tableInfo.columnInfos.get(7).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(7).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(7).getAutoIncrement());

        Assert.assertEquals("OverTimeCorrection", tableInfo.columnInfos.get(8).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(8).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(8).getAutoIncrement());

        Assert.assertEquals("WorklocationId", tableInfo.columnInfos.get(9).getColName());
        Assert.assertEquals("INTEGER", tableInfo.columnInfos.get(9).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(9).getAutoIncrement());

        Assert.assertEquals("Description", tableInfo.columnInfos.get(10).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(10).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(10).getAutoIncrement());
    }
    
    @Test
    public void T38_CreateTrackingItemTable_Creates_TrackingItemTable() throws SQLException, IOException {
        //Arrange
        String userDir = "user.dir";
        String sqLiteDir = "/sqlite/"; 
        String fileName = "TestFile.sqlite";
        String pathToSqLiteDatabase = System.getProperty(userDir).concat(sqLiteDir).concat(fileName);

        ResourceBundle resourceBundleMock = Mockito.mock(ResourceBundle.class);
        Log4jAdapter log4jAdapterMock = Mockito.mock(Log4jAdapter.class);
        
        Database database = new Database(resourceBundleMock, log4jAdapterMock);
        database.deleteDatabaseFile(fileName);
        database.createDatabase(fileName);

        //Act
        boolean result = database.createTrackingItemTableIfNotExists(fileName);
        
        TableInfo tableInfo = database.getTableInfo(fileName, "TrackingItem");
        
        //Assert
        Assert.assertTrue(result);
        Assert.assertEquals("TrackingItem", tableInfo.getName());
        
        Assert.assertEquals("Id", tableInfo.columnInfos.get(0).getColName());
        Assert.assertEquals("INTEGER", tableInfo.columnInfos.get(0).getTypeName());
        Assert.assertEquals("YES", tableInfo.columnInfos.get(0).getAutoIncrement());
        
        Assert.assertEquals("Name", tableInfo.columnInfos.get(1).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(1).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(1).getAutoIncrement());

        Assert.assertEquals("Shortcut", tableInfo.columnInfos.get(2).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(2).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(2).getAutoIncrement());
        
        Assert.assertEquals("Description", tableInfo.columnInfos.get(3).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(3).getTypeName());        
        Assert.assertEquals("NO", tableInfo.columnInfos.get(3).getAutoIncrement());
    }

    @Test
    public void T39_CreateSprintTable_Creates_SprintTable() throws SQLException, IOException {
        //Arrange
        String userDir = "user.dir";
        String sqLiteDir = "/sqlite/"; 
        String fileName = "TestFile.sqlite";
        String pathToSqLiteDatabase = System.getProperty(userDir).concat(sqLiteDir).concat(fileName);

        ResourceBundle resourceBundleMock = Mockito.mock(ResourceBundle.class);
        Log4jAdapter log4jAdapterMock = Mockito.mock(Log4jAdapter.class);
        
        Database database = new Database(resourceBundleMock, log4jAdapterMock);
        database.deleteDatabaseFile(fileName);
        database.createDatabase(fileName);

        //Act
        boolean result = database.createSprintTableIfNotExists(fileName);
        
        TableInfo tableInfo = database.getTableInfo(fileName, "Sprint");
        
        //Assert
        Assert.assertTrue(result);
        Assert.assertEquals("Sprint", tableInfo.getName());
        
        Assert.assertEquals("Id", tableInfo.columnInfos.get(0).getColName());
        Assert.assertEquals("INTEGER", tableInfo.columnInfos.get(0).getTypeName());
        Assert.assertEquals("YES", tableInfo.columnInfos.get(0).getAutoIncrement());
        
        Assert.assertEquals("StartDate", tableInfo.columnInfos.get(1).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(1).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(1).getAutoIncrement());

        Assert.assertEquals("EndDate", tableInfo.columnInfos.get(2).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(2).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(2).getAutoIncrement());
        
        Assert.assertEquals("NumberOfSprintDays", tableInfo.columnInfos.get(3).getColName());
        Assert.assertEquals("INTEGER", tableInfo.columnInfos.get(3).getTypeName());        
        Assert.assertEquals("NO", tableInfo.columnInfos.get(3).getAutoIncrement());
    }

    @Test
    public void T40_CreateWorkItemTable_Creates_WorkItemTable() throws SQLException, IOException {
        //Arrange
        String userDir = "user.dir";
        String sqLiteDir = "/sqlite/"; 
        String fileName = "TestFile.sqlite";
        String pathToSqLiteDatabase = System.getProperty(userDir).concat(sqLiteDir).concat(fileName);

        ResourceBundle resourceBundleMock = Mockito.mock(ResourceBundle.class);
        Log4jAdapter log4jAdapterMock = Mockito.mock(Log4jAdapter.class);
        
        Database database = new Database(resourceBundleMock, log4jAdapterMock);
        database.deleteDatabaseFile(fileName);
        database.createDatabase(fileName);

        //Act
        boolean result = database.createWorkItemTableIfNotExists(fileName);
        
        TableInfo tableInfo = database.getTableInfo(fileName, "WorkItem");
        
        //Assert
        Assert.assertTrue(result);
        Assert.assertEquals("WorkItem", tableInfo.getName());
        
        Assert.assertEquals("Id", tableInfo.columnInfos.get(0).getColName());
        Assert.assertEquals("INTEGER", tableInfo.columnInfos.get(0).getTypeName());
        Assert.assertEquals("YES", tableInfo.columnInfos.get(0).getAutoIncrement());
        
        Assert.assertEquals("WorkrecordId", tableInfo.columnInfos.get(1).getColName());
        Assert.assertEquals("INTEGER", tableInfo.columnInfos.get(1).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(1).getAutoIncrement());

        Assert.assertEquals("SprintId", tableInfo.columnInfos.get(2).getColName());
        Assert.assertEquals("INTEGER", tableInfo.columnInfos.get(2).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(2).getAutoIncrement());

        Assert.assertEquals("TrackingItemId", tableInfo.columnInfos.get(3).getColName());
        Assert.assertEquals("INTEGER", tableInfo.columnInfos.get(3).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(3).getAutoIncrement());

        Assert.assertEquals("Starttime", tableInfo.columnInfos.get(4).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(4).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(4).getAutoIncrement());

        Assert.assertEquals("Endtime", tableInfo.columnInfos.get(5).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(5).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(5).getAutoIncrement());
        
        Assert.assertEquals("Description", tableInfo.columnInfos.get(6).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(6).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(6).getAutoIncrement());

        Assert.assertEquals("Shortcut", tableInfo.columnInfos.get(7).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(7).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(7).getAutoIncrement());

        Assert.assertEquals("Name", tableInfo.columnInfos.get(8).getColName());
        Assert.assertEquals("TEXT", tableInfo.columnInfos.get(8).getTypeName());
        Assert.assertEquals("NO", tableInfo.columnInfos.get(8).getAutoIncrement());
    }

}