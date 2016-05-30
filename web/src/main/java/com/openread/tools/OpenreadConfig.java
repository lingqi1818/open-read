package com.openread.tools;

import java.io.IOException;
import java.util.Properties;

public class OpenreadConfig {

    private static Properties props = new Properties();
    static {
        try {
            props.load(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("openread.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProp(String key) {
        return props.getProperty(key);
    }

}
