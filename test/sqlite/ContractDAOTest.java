/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sqlite;

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
public class ContractDAOTest {
    
    private final static String APP_LANGUAGE_RESOURCE_KEY = "Language";
    private final static String APP_LANGUAGE_DEFAULT_VALUE = "en";
    private final static String LANGUAGE_RESOURCE = "languages.bundle";
    private final static String DATABASE_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/sqlite/testdb.sqlite";
    private final static String LOG4J2_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/log4j2.xml";

    private static ResourceBundle bundle;
    private static ConnectionFactory connectionFactory;
    private static Connection connection;
    
    public ContractDAOTest() throws FileNotFoundException { 
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
        ContractDAO contractDAO = new ContractDAO(null);
    }
    
    @Test()
    public void T01_Calling_Ctor_With_Valid_Connection_Returns_Initialized_Instance() {
        //Arrange
        //Act
        ContractDAO contractDAO = new ContractDAO(connection);
        
        //Assert
        Assert.assertNotNull(contractDAO);
    }

    @Test()
    public void T10_Calling_SelectAll_Returns_All_Found_Contracts() throws SQLException {
        //Arrange
        Contract contract1 = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        Contract contract2 = new Contract(2L, "8 hours contract", 8L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));

        ContractDAO contractDAO = new ContractDAO(connection);
        contractDAO.create(contract1);
        contractDAO.create(contract2);
        
        //Act
        List<Contract> contractList = contractDAO.selectAll();

        //Assert
        Assert.assertEquals(2, contractList.size());
        Assert.assertEquals(contract1, contractList.get(0));
        Assert.assertEquals(contract2, contractList.get(1));
    }
    
    //@Test()
    public void T11_Calling_SelectAll_Returns_EmptyList_If_No_Contracts_Found() throws SQLException {
        //Arrange
        ContractDAO contractDAO = new ContractDAO(connection);
        
        //Act
        List<Contract> contractList = contractDAO.selectAll();

        //Assert
        Assert.assertTrue(contractList.isEmpty());
    }

    @Test()
    public void T20_Calling_SelectContractFromId_Returns_The_Stored_Contract() throws SQLException {
        //Arrange
        Contract contract1 = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        Contract contract2 = new Contract(2L, "8 hours contract", 8L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));

        ContractDAO contractDAO = new ContractDAO(connection);
        contractDAO.create(contract1);
        contractDAO.create(contract2);
        
        //Act
        Contract result = contractDAO.selectContractFromId(1L);

        //Assert
        Assert.assertEquals(contract1, result);
    }

    @Test()
    public void T21_Calling_SelectContractFromId_Returns_Null_If_Contract_NotFound() throws SQLException {
        //Arrange
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));

        ContractDAO contractDAO = new ContractDAO(connection);
        contractDAO.create(contract);
        
        //Act
        Contract result = contractDAO.selectContractFromId(2L);

        //Assert
        Assert.assertNull(result);
    }

    @Test(expected = NullPointerException.class)
    public void T30_Calling_Create_Contract_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        ContractDAO contractDAO = new ContractDAO(connection);

        //Act
        boolean result = contractDAO.create(null);

        //Assert
        Assert.assertFalse(result);
    }

    @Test()
    public void T31_Calling_Create_Stores_Contract() throws SQLException {
        //Arrange
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));

        ContractDAO contractDAO = new ContractDAO(connection);
        
        //Act
        boolean result = contractDAO.create(contract);
        
        //Assert
        Assert.assertTrue(result);
    }
    
    @Test(expected = SQLException.class)
    public void T32_Calling_Create_Contract_AllreadyExists_Throws_SQLException() throws SQLException {
        //Arrange
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));

        ContractDAO contractDAO = new ContractDAO(connection);
        contractDAO.create(contract);

        //Act
        boolean result = contractDAO.create(contract);        

        //Assert
        Assert.assertFalse(result);
    }

    @Test(expected = NullPointerException.class)
    public void T40_Calling_Update_OriginalContract_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        Contract modifiedContract = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));

        ContractDAO contractDAO = new ContractDAO(connection);

        //Act
        boolean result = contractDAO.update(null, modifiedContract);

        //Assert
        Assert.assertFalse(result);
    }

    @Test(expected = NullPointerException.class)
    public void T41_Calling_Update_ModifiedContract_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        Contract originalContract = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));

        ContractDAO contractDAO = new ContractDAO(connection);

        //Act
        boolean result = contractDAO.update(originalContract, null);

        //Assert
        Assert.assertFalse(result);
    }

    @Test()
    public void T42_Calling_Update_Updates_OriginalContract() throws SQLException {
        //Arrange
        Contract originalContract = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        Contract modifiedContract = new Contract(1L, "8 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));

        ContractDAO contractDAO = new ContractDAO(connection);
        contractDAO.create(originalContract);
        
        //Act
        boolean result = contractDAO.update(originalContract, modifiedContract);
        Contract contractResult = contractDAO.selectContractFromId(modifiedContract.getId());
        
        //Assert
        Assert.assertTrue(result);
        Assert.assertEquals(modifiedContract, contractResult);
    }

    @Test()
    public void T43_Calling_Update_OriginalContract_DoesNotExists_Returns_False() throws SQLException {
        //Arrange
        Contract originalContract = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        Contract modifiedContract = new Contract(1L, "8 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));

        ContractDAO contractDAO = new ContractDAO(connection);
        
        //Act
        boolean result = contractDAO.update(originalContract, modifiedContract);
        
        //Assert
        Assert.assertFalse(result);
    }
    
    @Test()
    public void T44_Calling_Update_On_Different_Contracts_DoenNotChange_Contract() throws SQLException {
        //Arrange
        Contract originalContract = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        Contract modifiedContract = new Contract(2L, "8 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));

        ContractDAO contractDAO = new ContractDAO(connection);
        contractDAO.create(originalContract);

        //Act
        boolean result = contractDAO.update(originalContract, modifiedContract);
        Contract contractResult = contractDAO.selectContractFromId(originalContract.getId());

        //Assert
        Assert.assertFalse(result);
        Assert.assertEquals(originalContract, contractResult);
    }

    @Test(expected = NullPointerException.class)
    public void T50_Calling_Delete_Contract_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        ContractDAO contractDAO = new ContractDAO(connection);

        //Act
        //Assert
        boolean result = contractDAO.delete(null);
    }
    
    @Test()
    public void T51_Calling_Delete_Contract_DoesNotExists_Returns_False() throws SQLException {
        //Arrange
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));

        ContractDAO contractDAO = new ContractDAO(connection);

        //Act
        boolean result = contractDAO.delete(contract);

        //Assert
        Assert.assertFalse(result);
    }

    @Test()
    public void T52_Calling_Delete_Contract_Exists_Deletes_Contract() throws SQLException {
        //Arrange
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));

        ContractDAO contractDAO = new ContractDAO(connection);
        contractDAO.create(contract);
        
        //Act
        boolean result = contractDAO.delete(contract);
        Contract contractResult = contractDAO.selectContractFromId(contract.getId());

        //Assert
        Assert.assertTrue(result);
        Assert.assertNull(contractResult);
    }

    @Test()
    public void T60_Calling_GetNextId_On_ContractTable_Containing_One_Record_Returns_2() throws SQLException {
        //Arrange
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));

        ContractDAO contractDAO = new ContractDAO(connection);
        contractDAO.create(contract);
        
        //Act
        long receivedId = contractDAO.getNextId();
        
        //Assert
        Assert.assertEquals(2L, receivedId);
    }
    
    @Test()
    public void T61_Calling_GetNextId_Twice_On_AddressTable_Returns_SameId() throws SQLException {
        //Arrange
        Contract contract = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));

        ContractDAO contractDAO = new ContractDAO(connection);
        contractDAO.create(contract);
        
        //Act
        long receivedIdFirstCall = contractDAO.getNextId();
        long receivedIdSecondCall = contractDAO.getNextId();
        
        //Assert
        Assert.assertEquals(receivedIdSecondCall, receivedIdFirstCall);
    }

    @Test
    public void T62_Calling_GetNextId_On_Truncated_Sqlite_Sequence_Table_Returns_0() throws SQLException {
        //Arrange
        ContractDAO contractDAO = new ContractDAO(connection);
        
        //Act
        long resultId = contractDAO.getNextId();

        //Assert
        Assert.assertEquals(0L, resultId);
    }

    private synchronized boolean truncateTable() {
        boolean result = false;
        StringBuilder statement = new StringBuilder();
        statement.append("DELETE FROM contract ");
        
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }
        
        statement = new StringBuilder();        
        //statement.append("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name='Address'");
        statement.append("DELETE FROM SQLITE_SEQUENCE WHERE name='Contract'");
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }
        return result;
    }
    
}
