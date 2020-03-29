package joelbits.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseProperties {
    private final Properties properties = new Properties();
    private static final String CONFIGURATION_FILE = "database.properties";

    public DatabaseProperties() throws Exception {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIGURATION_FILE)) {
            properties.load(inputStream);
        } catch (IOException e) {
            System.out.println("Could not find properties file " + CONFIGURATION_FILE);
        }
    }

    public String getURL() {
        return properties.getProperty("url");
    }

    public String getUsername() {
        return properties.getProperty("user");
    }

    public String getPassword() {
        return properties.getProperty("password");
    }
}
