package utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyReader {
    private static Properties properties;

    public static void loadProperties(String filePath) {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(filePath)) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load properties file: " + e.getMessage());
        }
    }

    public static String getProperty(String key) {
        if (properties == null) {
            throw new RuntimeException("Properties not loaded. Call loadProperties first.");
        }
        return properties.getProperty(key);
    }

    public static Properties getAllProperties() {
        if (properties == null) {
            throw new RuntimeException("Properties not loaded. Call loadProperties first.");
        }
        return properties;
    }
}