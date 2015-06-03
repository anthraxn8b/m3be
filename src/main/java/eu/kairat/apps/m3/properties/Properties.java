package eu.kairat.apps.m3.properties;

/**
 * Created by Boris.Kairat on 29.05.2015.
 */
public interface Properties {

    String getString(Enum<?> enumValue);

    Boolean getBoolean(Enum<?> enumValue);
}
