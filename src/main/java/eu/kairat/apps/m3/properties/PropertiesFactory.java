package eu.kairat.apps.m3.properties;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PropertiesFactory {

    private static PropertiesFactory singleton;

    private final Map<PropertiesTypes, PropertiesImpl> cache = new HashMap<>();

    private PropertiesFactory() {
    }

    public static PropertiesFactory getInstance() {
        if (singleton == null) {
            synchronized (PropertiesFactory.class) {
                if (singleton == null) {
                    singleton = new PropertiesFactory();
                }
            }
        }
        return singleton;
    }

    public synchronized PropertiesImpl provideProperties(PropertiesTypes type) throws Exception {

        PropertiesImpl properties = cache.get(type);

        if (null == properties) {
            try {
                properties = new PropertiesImpl(type);
                cache.put(type, properties);
            } catch (IOException e) {
                throw new Exception("Could not instanciate a properties file of type " + type + ".", e);
            }
        }
        return properties;
    }
}
