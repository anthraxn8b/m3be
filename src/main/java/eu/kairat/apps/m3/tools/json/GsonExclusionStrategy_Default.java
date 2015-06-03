package eu.kairat.apps.m3.tools.json;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import eu.kairat.apps.m3.model.User;

public class GsonExclusionStrategy_Default implements ExclusionStrategy {

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return (f.getDeclaringClass() == User.class && f.getName().equals("password"));
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }
}
