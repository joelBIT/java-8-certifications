package joelbits.properties;

import java.io.InputStream;
import java.util.Properties;

public class DatabaseProperties {
    private final Properties properties = new Properties();
    private static final String CONFIGURATION_FILE = "database.properties";
    private static final String USER = "user";
    private static final String PASSWORD = "password";
    private static final String DATABASE = "database";
    private static final String CREATE_DATABASE = ";create=true";

    public DatabaseProperties() throws Exception {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIGURATION_FILE)) {
            properties.load(inputStream);
        }
    }

    public String getDatabaseName() {
        return properties.getProperty(DATABASE);
    }

    public String getUsername() {
        return properties.getProperty(USER);
    }

    public String getPassword() {
        return properties.getProperty(PASSWORD);
    }

    public String getURL() {
        return getDatabaseName() + ";" + USER + "=" + getUsername() + ";" + PASSWORD + "=" + getPassword() + CREATE_DATABASE;
    }
}
