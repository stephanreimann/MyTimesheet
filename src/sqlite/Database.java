/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sqlite;

import adapter.Log4jAdapter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.sql.*;
import java.util.*;
import org.apache.logging.log4j.*;
import org.sqlite.*;
import properties.TranslationStringProperty;

/**
 *
 * @author adrest18
 */
public class Database {
    
    private final Logger log;
    private final ResourceBundle resourceBundle;
    private final TranslationStringProperty logMessage;
    
    @Override
    public String toString() {
        return super.toString(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }
    
    public Database(ResourceBundle resourceBundle, Log4jAdapter log4jAdapter) {
        if(resourceBundle == null) throw new NullPointerException("rb");
        if(log4jAdapter == null) throw new NullPointerException("log4jAdapter");
        
        log = LogManager.getLogger(ConnectionFactory.class.getName());
        logMessage = new TranslationStringProperty(log4jAdapter);
        this.resourceBundle = resourceBundle;
    }
    
    public void createDatabase(String fileName) throws SQLException {
        try {
            String url = createUrlFromFileName(fileName);
            Connection connection = DriverManager.getConnection(url);
            
            if (connection != null) {
                DatabaseMetaData meta = connection.getMetaData();
                logMessage.translate("DriverName", new Object[] { meta.getDriverName() }, resourceBundle);
                logMessage.translate("DatabaseCreated", new Object[] { fileName }, resourceBundle);
                connection.close();
            }
        } catch (SQLException ex) {
            log.fatal(Database.class.getName(), ex.fillInStackTrace());
            throw ex;
        }
    }
    
    private String createUrlFromFileName(String fileName) {
        return "jdbc:sqlite:".concat(createFullQualifiedFileName(fileName));
    }
    
    private String createFullQualifiedFileName(String fileName) {
        return System.getProperty("user.dir").concat("/sqlite/").concat(fileName);
    }
    
    public Connection getConnection(String fileName) throws Exception {
        try {
            SQLiteJDBCLoader.initialize();
            SQLiteDataSource dataSource = new SQLiteDataSource();
            dataSource.setUrl(createUrlFromFileName(fileName));
            return dataSource.getConnection();
        } catch (Exception ex) {
            log.fatal(Database.class.getName(), ex.fillInStackTrace());
            throw ex;
        }
    }
            
    public void deleteDatabaseFile(String fileName) throws NoSuchFileException, DirectoryNotEmptyException, IOException, SQLException {
        Path path = FileSystems.getDefault().getPath(System.getProperty("user.dir"), "sqlite", fileName);
        
        try {
            Files.delete(path);
            logMessage.translate("DatabaseDeleted", new Object[] { path.getFileName() }, resourceBundle);
        } catch (IOException ex) {
            logMessage.translate("IOException", new Object[] { path.getFileName(), ex.toString() }, resourceBundle);
        }    
    }
    
    public boolean createAddressTableIfNotExists(String fileName) {
        String addressTableSchema = new StringBuilder()
            .append("CREATE TABLE IF NOT EXISTS Address (")
            .append("Id                         INTEGER NOT NULL UNIQUE,")
            .append("Streetname                 TEXT NOT NULL,")
            .append("Housenumber                INTEGER NOT NULL,")
            .append("Unitname                   TEXT,")
            .append("Unitnumber                 INTEGER,")
            .append("UnitLocation               TEXT,")
            .append("City                       TEXT NOT NULL,")
            .append("State                      TEXT NOT NULL,")
            .append("ZipCode                    INTEGER NOT NULL,")
            .append("Country                    TEXT NOT NULL,")
            .append("PRIMARY KEY(Id AUTOINCREMENT)")
            .append(");")
            .toString();

        return createTable(fileName, addressTableSchema);
    }
    
    public boolean createContractTableIfNotExists(String fileName) {
        String contractTableSchema = new StringBuilder()
            .append("CREATE TABLE IF NOT EXISTS Contract (")
            .append("Id                         INTEGER NOT NULL UNIQUE,")
            .append("Name                       TEXT NOT NULL,")
            .append("Workhours                  INTEGER NOT NULL,")
            .append("Maxworkhours               INTEGER NOT NULL,")
            .append("Vacationdays               INTEGER NOT NULL,")
            .append("Vacationreconciliationdate STRING NOT NULL,")
            .append("Breakfastofftimeend        INTEGER NOT NULL,")
            .append("Breakfastofftimestart      INTEGER NOT NULL,")
            .append("Lunchofftimeend            INTEGER NOT NULL,")
            .append("Lunchofftimestart          INTEGER NOT NULL,")
            .append("Earliestworktimestart      TIME NOT NULL,")
            .append("Latestworktimeend          TIME NOT NULL,")
            .append("PRIMARY KEY(Id AUTOINCREMENT)")
            .append(");")
            .toString();

        return createTable(fileName, contractTableSchema);
    }
    
    public boolean createHolydayTableIfNotExists(String fileName) {
        String userTableSchema = new StringBuilder()
            .append("CREATE TABLE IF NOT EXISTS Holyday (")
            .append("Id                         INTEGER NOT NULL UNIQUE,")
            .append("Date                       TEXT NOT NULL,")
            .append("Name                       TEXT NOT NULL,")
            .append("State                      TEXT NOT NULL,")
            .append("PRIMARY KEY(Id AUTOINCREMENT)")
            .append(");")
            .toString();

        return createTable(fileName, userTableSchema);
    }
 
    public boolean createProjectTableIfNotExists(String fileName) {
        String userTableSchema = new StringBuilder()
            .append("CREATE TABLE IF NOT EXISTS Project (")
            .append("Id                         INTEGER NOT NULL UNIQUE,")
            .append("Name                       TEXT NOT NULL,")
            .append("Costunit                   TEXT,")
            .append("IsWorktimeRelevant         TEXT NOT NULL DEFAULT 0,")
            .append("IsVacationRelevant         TEXT NOT NULL DEFAULT 0,")
            .append("IsComptimeRelevant         TEXT NOT NULL DEFAULT 0,")
            .append("Description                TEXT,")
            .append("PRIMARY KEY(Id AUTOINCREMENT)")
            .append(");")
            .toString();

        return createTable(fileName, userTableSchema);
    }

    public boolean createRoleTableIfNotExists(String fileName) {
        String roleTableSchema = new StringBuilder()
            .append("CREATE TABLE IF NOT EXISTS Role (")
            .append("Id                         INTEGER NOT NULL UNIQUE,")
            .append("Name                       TEXT NOT NULL,")
            .append("Description                TEXT,")
            .append("PRIMARY KEY(Id AUTOINCREMENT)")
            .append(");")
            .toString();

        return createTable(fileName, roleTableSchema);
    }
    
    public boolean createUserTableIfNotExists(String fileName) {
        String userTableSchema = new StringBuilder()
            .append("CREATE TABLE IF NOT EXISTS User (")
            .append("Id                         INTEGER NOT NULL UNIQUE,")
            .append("RoleId                     INTEGER NOT NULL,")
            .append("AddressId                  INTEGER NOT NULL,")
            .append("ContractId                 INTEGER NOT NULL,")
            .append("FirstName                  TEXT NOT NULL,")
            .append("LastName                   TEXT NOT NULL,")
            .append("Login                      TEXT NOT NULL,")
            .append("Password                   TEXT NOT NULL,")
            .append("VacationLeft               INTEGER NOT NULL,")
            .append("PRIMARY KEY(Id AUTOINCREMENT)")
            .append(");")
            .toString();

        return createTable(fileName, userTableSchema);
    }
    
    public boolean createWorklocationTableIfNotExists(String fileName) {
        String userTableSchema = new StringBuilder()
            .append("CREATE TABLE Worklocation (")
            .append("Id                         INTEGER NOT NULL UNIQUE,")
            .append("Name                       TEXT NOT NULL UNIQUE,")
            .append("Description                TEXT,")
            .append("PRIMARY KEY(Id AUTOINCREMENT)")
            .append(");")
            .toString();

        return createTable(fileName, userTableSchema);
    }
    
    public boolean createWorkrecordTableIfNotExists(String fileName) {
        String userTableSchema = new StringBuilder()
            .append("CREATE TABLE Workrecord (")
            .append("Id                         INTEGER NOT NULL UNIQUE,")
            .append("UserId                     INTEGER NOT NULL,")
            .append("ProjectId                  INTEGER NOT NULL,")
            .append("Date                       TEXT NOT NULL,")
            .append("StartTime                  TEXT NOT NULL,")
            .append("EndTime                    TEXT NOT NULL,")
            .append("WorkTime                   TEXT NOT NULL,")
            .append("OverTime                   TEXT NOT NULL,")
            .append("OverTimeCorrection         TEXT,")
            .append("WorklocationId             INTEGER NOT NULL,")
            .append("Description                TEXT,")
            .append("PRIMARY KEY(Id AUTOINCREMENT)")
            .append(");")
            .toString();

        return createTable(fileName, userTableSchema);
    }
     
    public boolean createSqliteSequenceTableIfNotExists(String fileName) {
        String userTableSchema = new StringBuilder()
            .append("CREATE TABLE sqlite_sequence(name,seq);")
            .toString();

        return createTable(fileName, userTableSchema);
    }
    
    public void fillDatabaseWithDummyInfos() {
        
    }
    
    private boolean createTable(String fileName, String tabelDefinition) {
        try {
            Connection connection;
            connection = getConnection(fileName);
            PreparedStatement dbStatement = connection.prepareStatement(tabelDefinition);
            dbStatement.execute();
            logMessage.translate("TableCreationSuccessful", new Object[] { tabelDefinition }, resourceBundle);
            connection.close();
            return true;
        } catch (SQLException ex) {
            logMessage.translate("TableCreationFailed", new Object[] { ex.getMessage() }, resourceBundle);
        } catch (Exception ex) {
            log.fatal(Database.class.getName(), ex.fillInStackTrace());
        }
        return false;
    }
    
    public class TableInfo {
        private String catalog;
        public String getCatalog() {
            return catalog;
        }
        public void setCatalog(String catalog) {
            this.catalog = catalog;
        }

        private String name;
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        private String schema;
        public String getSchema() {
            return schema;
        }
        public void setSchema(String schema) {
            this.schema = schema;
        }
    
        public List<ColumnInfo> columnInfos = new ArrayList<>();
    }
    
    public class ColumnInfo {
        private String colName;
        public String getColName() {
            return colName;
        }
        public void setColName(String colName) {
            this.colName = colName;
        }

        private String dataType;
        public String getDataType() {
            return dataType;
        }
        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        private String typeName;
        public String getTypeName() {
            return typeName;
        }
        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        private String colSize;
        public String getColSize() {
            return colSize;
        }
        public void setColSize(String colSize) {
            this.colSize = colSize;
        }

        private String remark;
        public String getRemark() {
            return remark;
        }
        public void setRemark(String remark) {
            this.remark = remark;
        }

        private String defaultValue;
        public String getDefaultValue() {
            return defaultValue;
        }
        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        private String position;
        public String getPosition() {
            return position;
        }
        public void setPosition(String position) {
            this.position = position;
        }

        private String autoIncrement;
        public String getAutoIncrement() {
            return autoIncrement;
        }
        public void setAutoIncrement(String autoIncrement) {
            this.autoIncrement = autoIncrement;
        }

        private String generatedCol;
        public String getGeneratedCol() {
            return generatedCol;
        }
        public void setGeneratedCol(String generatedCol) {
            this.generatedCol = generatedCol;
        }
    }
    
    //https://www.tutorialspoint.com/java-databasemetadata-getcolumns-method-with-example
    public TableInfo getTableInfo(String fileName, String tableName) {
        TableInfo tableInfo = new TableInfo();
        try {
            Connection connection;
            connection = getConnection(fileName);
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            ResultSet columns = databaseMetaData.getColumns(null, null, tableName, null);
            while (columns.next()) {
                if(tableInfo.getCatalog() == null || !tableInfo.getCatalog().equals(columns.getString("TABLE_CAT"))) {
                    tableInfo.setCatalog(columns.getString("TABLE_CAT"));
                }
                if(tableInfo.getSchema() == null || !tableInfo.getSchema().equals(columns.getString("TABLE_SCHEM"))) {
                    tableInfo.setSchema(columns.getString("TABLE_SCHEM"));
                }
                if(tableInfo.getName() == null || !tableInfo.getName().equals(columns.getString("TABLE_NAME"))) {
                    tableInfo.setName(columns.getString("TABLE_NAME"));
                }
                ColumnInfo columnInfo = new ColumnInfo();
                columnInfo.setColName(columns.getString("COLUMN_NAME"));
                columnInfo.setDataType(columns.getString("DATA_TYPE"));
                columnInfo.setTypeName(columns.getString("TYPE_NAME"));
                columnInfo.setColSize(columns.getString("COLUMN_SIZE"));
                columnInfo.setRemark(columns.getString("REMARKS"));
                columnInfo.setDefaultValue(columns.getString("COLUMN_DEF"));
                columnInfo.setPosition(columns.getString("ORDINAL_POSITION"));
                columnInfo.setAutoIncrement(columns.getString("IS_AUTOINCREMENT"));
                columnInfo.setGeneratedCol(columns.getString("IS_GENERATEDCOLUMN"));
                tableInfo.columnInfos.add(columnInfo);
            }            
            connection.close();
        } catch (SQLException ex) {
            logMessage.translate("SchemaRetrievalFailed", new Object[] { ex.getMessage() }, resourceBundle);
        } catch (Exception ex) {
            log.fatal(Database.class.getName(), ex.fillInStackTrace());
        }
        return tableInfo;
    }
    
    public boolean exportTableInformationToCSV(Connection conn, String tableName, String csvFilePath) {
        String query = "SELECT * FROM " + tableName;

        log.info("Export data of Table[" + tableName + "] started..." );

        try (               
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            FileWriter csvWriter = new FileWriter(csvFilePath)) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                csvWriter.append(meta.getColumnName(i));
                if (i < columnCount) csvWriter.append(",");
            }
            csvWriter.append("\n");

            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String value = rs.getString(i);
                    if (value != null) {
                        value = value.replace("\"", "\"\"");
                        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
                            value = "\"" + value + "\"";
                        }
                    }
                    csvWriter.append(value != null ? value : "");
                    if (i < columnCount) csvWriter.append(",");
                }
                csvWriter.append("\n");
            }
        } catch (SQLException | IOException ex) {
            log.fatal(Database.class.getName(), ex.fillInStackTrace());
            return false;
        }
        
        log.info("...Export data of Table[" + tableName + "] finished" );
        
        return true;
    }

    public boolean importTableInformationFromCSV(Connection conn, String tableName, String csvFilePath) {
        log.info("Import data into Table[" + tableName + "] started..." );

        try (
            BufferedReader csvReader = new BufferedReader(new FileReader(csvFilePath))) {

            String headerLine = csvReader.readLine();
            if (headerLine == null) return false;

            String[] columns = parseCSVRow(headerLine);
            String placeholders = String.join(",", Collections.nCopies(columns.length, "?"));
            String insertSQL = "INSERT INTO " + tableName + " (" + String.join(",", columns) + ") VALUES (" + placeholders + ")";

            try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                String row;
                while ((row = csvReader.readLine()) != null) {
                    String[] values = parseCSVRow(row);
                    for (int i = 0; i < values.length; i++) {
                        pstmt.setString(i + 1, values[i]);
                    }
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
        } catch (SQLException | IOException ex) {
            log.fatal(Database.class.getName(), ex.fillInStackTrace());
            return false;
        }

        log.info("...Import data into Table[" + tableName + "] finished" );
        
        return true;
    }

    private String[] parseCSVRow(String row) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < row.length(); i++) {
            char c = row.charAt(i);
            if (c == '\"') {
                if (inQuotes && i + 1 < row.length() && row.charAt(i + 1) == '\"') {
                    sb.append('\"'); // Escaped quote
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString());
        return tokens.toArray(String[]::new);
    }
    
}