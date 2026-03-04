package com.cousin.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ApiConfig {
    private static final String CONFIG_FILE = "config.properties";
    private static Properties props = null;

    static {
        props = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            props.load(fis);
        } catch (IOException e) {
            System.err.println("Impossible de charger le fichier de configuration: " + CONFIG_FILE);
        }
    }

    public static String getToken() {
        return props.getProperty("api.token", "");
    }

    public static String getApiUrl() {
        return props.getProperty("api.url", "");
    }
}
