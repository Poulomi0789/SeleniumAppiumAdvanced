package utils;

import java.io.FileInputStream;
import java.util.Properties;

public class ConfigReader {
    private static Properties prop;

    static {
        try {
            String env = System.getProperty("env", "qa"); // Defaults to QA
            FileInputStream fis = new FileInputStream("src/test/resources/config/" + env + ".properties");
            prop = new Properties();
            prop.load(fis);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static String getProperty(String key) { return prop.getProperty(key); }
}