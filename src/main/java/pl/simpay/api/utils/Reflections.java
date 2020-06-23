package pl.simpay.api.utils;

import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Reflections {
    @SneakyThrows public static Map<String, String> serializeObject(Object o) {
        HashMap<String, String> map = new HashMap<>();

        for (Field field : o.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object val = field.get(o);
            if (val == null) continue;
            map.put(field.getName(), String.valueOf(val));
        }

        return map;
    }
}
