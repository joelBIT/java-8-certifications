package joelbits.utils;

import joelbits.properties.DatabaseProperties;

import java.sql.*;
import java.util.Objects;

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

        if (!schemaExists(connection.getMetaData())) {
            try(Statement statement = connection.createStatement()) {
                statement.execute("CREATE SCHEMA " + SCHEMA);
            }
        }
        if (!tableExists(connection.getMetaData())) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE TABLE " + SCHEMA + "." + TABLE + "(ID INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), FILENAME VARCHAR(30))");
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
}
