package eu.kairat.apps.m3.tools.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

/**
 * Created by Boris.Kairat on 29.05.2015.
 */
public class GsonFactory {

    public static final Gson GSON = new GsonBuilder()
            .setExclusionStrategies(new GsonExclusionStrategy_Default())
            //.serializeNulls() <-- uncomment to serialize NULL fields as well
            //.setPrettyPrinting()
            .create();

    public static final Gson GSON_FOR_LOG = new GsonBuilder()
            //.setExclusionStrategies(new GsonExclusionStrategy_Default())
            //.serializeNulls() <-- uncomment to serialize NULL fields as well
            .setPrettyPrinting()
            .create();

    public static final Gson GSON_FOR_UNSECURE_USE = new GsonBuilder()
            //.setExclusionStrategies(new GsonExclusionStrategy_Default())
            //.serializeNulls() <-- uncomment to serialize NULL fields as well
            //.setPrettyPrinting()
            .create();

    private static final Gson GSON_FOR_PRETTIFIER = new GsonBuilder().setPrettyPrinting().create();
    private static final JsonParser JSON_PARSER = new JsonParser();

    public static final String prettifyJson(String uglyJson) {
        return GSON_FOR_PRETTIFIER.toJson(JSON_PARSER.parse(uglyJson));
    }
}
