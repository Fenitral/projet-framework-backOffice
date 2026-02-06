package com.utils;

import java.lang.reflect.*;
import java.util.*;

public class JsonSerializer {

    public static String toJson(Object obj) {
        try {
            return serialize(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(
                "Erreur de sérialisation JSON", e
            );
        }
    }

    private static String serialize(Object obj)
            throws IllegalAccessException {

        if (obj == null) return "null";

        // Types simples
        if (isPrimitive(obj)) {
            return formatPrimitive(obj);
        }

        // Map
        if (obj instanceof Map<?, ?> map) {
            return mapToJson(map);
        }

        // Collection (List, Set)
        if (obj instanceof Collection<?> col) {
            return collectionToJson(col);
        }

        // Tableau
        if (obj.getClass().isArray()) {
            return arrayToJson(obj);
        }

        // Objet classique
        return objectToJson(obj);
    }

    private static boolean isPrimitive(Object obj) {
        return obj instanceof String
            || obj instanceof Number
            || obj instanceof Boolean;
    }

    private static String formatPrimitive(Object obj) {
        if (obj instanceof String)
            return "\"" + escape(obj.toString()) + "\"";
        return obj.toString();
    }

    private static String mapToJson(Map<?, ?> map)
            throws IllegalAccessException {

        StringBuilder sb = new StringBuilder("{");
        boolean first = true;

        for (Map.Entry<?, ?> e : map.entrySet()) {
            if (!first) sb.append(",");
            first = false;

            sb.append("\"").append(e.getKey()).append("\":");
            sb.append(toJson(e.getValue()));
        }

        sb.append("}");
        return sb.toString();
    }

    private static String collectionToJson(Collection<?> col)
            throws IllegalAccessException {

        StringBuilder sb = new StringBuilder("[");
        boolean first = true;

        for (Object o : col) {
            if (!first) sb.append(",");
            first = false;
            sb.append(toJson(o));
        }

        sb.append("]");
        return sb.toString();
    }

    private static String arrayToJson(Object array)
            throws IllegalAccessException {

        StringBuilder sb = new StringBuilder("[");
        int len = Array.getLength(array);

        for (int i = 0; i < len; i++) {
            if (i > 0) sb.append(",");
            sb.append(toJson(Array.get(array, i)));
        }

        sb.append("]");
        return sb.toString();
    }

    private static String objectToJson(Object obj)
            throws IllegalAccessException {

        StringBuilder sb = new StringBuilder("{");
        boolean first = true;

        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            Object value = field.get(obj);
            if (value == null) continue;

            if (!first) sb.append(",");
            first = false;

            sb.append("\"").append(field.getName()).append("\":");
            sb.append(toJson(value));
        }

        sb.append("}");
        return sb.toString();
    }

    private static String escape(String s) {
        return s.replace("\"", "\\\"");
    }
}