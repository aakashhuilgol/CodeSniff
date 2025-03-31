package org.mining.util.gitmetrics;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;

public class JSONReflectUtil {

    public static void reflect(JSONObject jsonObject) {
        try {
            Field changeMap = jsonObject.getClass().getDeclaredField("map");
            changeMap.setAccessible(true);
            changeMap.set(jsonObject, new LinkedHashMap<>());
            changeMap.setAccessible(false);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
