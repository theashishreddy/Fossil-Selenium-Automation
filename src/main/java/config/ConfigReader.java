package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigReader {
    private static final Properties PROPERTIES = new Properties();
    static {
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) throw new IllegalStateException("config.properties not found");
            PROPERTIES.load(input);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
    private ConfigReader() {}
    public static String get(String key) {
        return System.getProperty(key, PROPERTIES.getProperty(key));
    }
    public static int getInt(String key) { return Integer.parseInt(get(key)); }
}
