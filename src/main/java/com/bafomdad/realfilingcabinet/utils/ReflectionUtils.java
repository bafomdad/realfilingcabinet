package com.bafomdad.realfilingcabinet.utils;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Created by bafomdad on 12/18/2018.
 */
public final class ReflectionUtils {

    public static <T> T getValue(Class<?> clazz, Object instance, String... names) {

        try {
            return (T)getField(clazz, names).get(instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Field getField(Class<?> clazz, String... names) {

        for (String name : names) {
            Field f = getFieldInternal(clazz, name);
            if (f != null)
                return f;
        }
        throw new IllegalArgumentException("Could not find any of fields " + Arrays.toString(names) + " on class " + clazz);
    }

    private static Field getFieldInternal(Class<?> clazz, String name) {

        try {
            Field f = clazz.getDeclaredField(name);
            f.setAccessible(true);
            return f;

        } catch (NoSuchFieldException | SecurityException e) {
            return null;
        }
    }
}
