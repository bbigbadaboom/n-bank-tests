package API.Configs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Config INSTANCE = new Config();
    private final Properties properties = new Properties();
    private Config() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("empty config");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
    public static String getProperties(String key) {
        return INSTANCE.properties.getProperty(key);
    }
}
