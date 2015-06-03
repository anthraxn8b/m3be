package eu.kairat.apps.m3.properties;

import java.io.IOException;

class PropertiesImpl implements Properties {

    private java.util.Properties properties = new java.util.Properties();

    PropertiesImpl(PropertiesTypes type) throws IOException {
        properties.load(PropertiesImpl.class.getResourceAsStream(type.name().toLowerCase() + ".properties"));
    }

    public String getString(Enum<?> enumValue) {
        return properties.getProperty(enumValue.name());
    }

    public Boolean getBoolean(Enum<?> enumValue) {
        return Boolean.valueOf(getString(enumValue));
    }
}
