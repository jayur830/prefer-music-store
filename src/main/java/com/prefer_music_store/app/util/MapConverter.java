package com.prefer_music_store.app.util;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class MapConverter {
    public static Map<String, Object> convertToHashMap(String[] keys, Object[] values) {
        return convert(new HashMap<>(), keys, values);
    }

    public static Map<String, Object> convertToTreeMap(String[] keys, Object[] values) {
        return convert(new TreeMap<>(), keys, values);
    }

    private static Map<String, Object> convert(
            Map<String, Object> object, String[] keys, Object[] values) {
        if (keys.length != values.length) return null;
        for (int i = 0; i < keys.length; ++i)
            object.put(keys[i], values[i]);
        return object;
    }
}
