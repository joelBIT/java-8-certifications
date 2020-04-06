package joelbits.utils;

import joelbits.entities.ConvertedFile;
import joelbits.properties.DatabaseProperties;

import java.sql.*;
import java.util.Objects;

/**
 * Note that this class, as well as other classes, are created specifically as preparation for the OCP exam. Otherwise,
 * DataSource should be used instead of DriverManager, PreparedStatement instead of Statement, and so on.
 */
public final class DatabaseUtil {
    private final DatabaseProperties properties;
    private final Connection connection;
    private static DatabaseUtil INSTANCE;
    private static final String SCHEMA = "FILECONVERTER";
    private static final String TABLE = "FILES";

    private DatabaseUtil() throws Exception {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        System.setProperty("derby.system.home", System.getProperty("user.dir") + "/." + SCHEMA.toLowerCase());

        properties = new DatabaseProperties();
        connection = DriverManager.getConnection(properties.getURL());

        // Use a BufferedReader(FileReader( for retrieving sql queries from *.sql files??
        // Use StreamReaders/Writers for file conversion??

        if (!schemaExists(connection.getMetaData())) {
            try(Statement statement = connection.createStatement()) {
                statement.execute("CREATE SCHEMA " + SCHEMA);
            }
        }
        if (!tableExists(connection.getMetaData())) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE TABLE " + SCHEMA + "." + TABLE + "(ID INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), NAME VARCHAR(30), SIZE DECIMAL, FORMAT VARCHAR(10), CONVERTED TIMESTAMP)");
            }
        }
    }

    public static synchronized DatabaseUtil getInstance() throws Exception {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new DatabaseUtil();
        }
        return INSTANCE;
    }

    private boolean schemaExists(DatabaseMetaData metaData) throws SQLException {
        try (ResultSet res = metaData.getSchemas(null, SCHEMA)) {
            return res.next();
        }
    }

    private boolean tableExists(DatabaseMetaData metaData) throws SQLException {
        try (ResultSet res = metaData.getTables(null, SCHEMA, TABLE, new String[] {"TABLE"})) {
            return res.next();
        }
    }

    public synchronized void executeQuery(String query) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
        }
    }

    public synchronized void listAllFiles() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery("SELECT * FROM " + SCHEMA + "." + TABLE);
            System.out.println("ID  " + "  FILENAME  " + "  SIZE  " + "  FORMAT  " + "  CONVERTED");
            while (result.next()) {
                System.out.println(result.getInt(1) + " " + result.getString(2) + " " + result.getDouble(3) + " " + result.getString(4) + " " + result.getTimestamp(5));
            }
        }
    }

    public static synchronized String createInsertQuery(ConvertedFile file) {
        return "INSERT INTO FILECONVERTER.FILES(NAME, SIZE, FORMAT, CONVERTED) VALUES ('" +
                file.getFileName() + "', " +
                file.getSize() + ", '" +
                file.getFormat() + "', '" +
                file.getConversionDate() + "')";
    }
}
