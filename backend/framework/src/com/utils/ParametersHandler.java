package com.utils;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.annotations.Param;
import com.annotations.Session;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.*;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.*;

public class ParametersHandler {
    public static Object convertToType(String value, Class<?> targetType) {
        if (value == null) {
            return getDefaultValue(targetType);
        }
        
        if (targetType == String.class) {
            return value;
        } else if (targetType == int.class || targetType == Integer.class) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return targetType == int.class ? 0 : null;
            }
        } else if (targetType == long.class || targetType == Long.class) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                return targetType == long.class ? 0L : null;
            }
        } else if (targetType == double.class || targetType == Double.class) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                return targetType == double.class ? 0.0 : null;
            }
        } else if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (targetType == float.class || targetType == Float.class) {
            try {
                return Float.parseFloat(value);
            } catch (NumberFormatException e) {
                return targetType == float.class ? 0.0f : null;
            }
        } else if (targetType == Date.class) {
            return parseDate(value);
        } else if (targetType == Date.class) {
            Date utilDate = parseDate(value);
            return utilDate != null ? new Date(utilDate.getTime()) : null;
        } else if (targetType == LocalDate.class) {
            return parseLocalDate(value);
        } else if (targetType == LocalDateTime.class) {
            return parseLocalDateTime(value);
        }
        
        return value;
    }

    private static Date parseDate(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        String[] dateFormats = {
            "yyyy-MM-dd",
            "dd/MM/yyyy",
            "MM/dd/yyyy",
            "yyyy-MM-dd HH:mm:ss",
            "dd-MM-yyyy",
            "yyyy/MM/dd"
        };
        
        for (String format : dateFormats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                sdf.setLenient(false);
                return sdf.parse(value);
            } catch (ParseException e) {
            }
        }
        
        return null;
    }

    private static LocalDate parseLocalDate(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e1) {
            String[] patterns = {"dd/MM/yyyy", "MM/dd/yyyy", "dd-MM-yyyy"};
            
            for (String pattern : patterns) {
                try {
                    DateTimeFormatter formatter = 
                        DateTimeFormatter.ofPattern(pattern);
                    return LocalDate.parse(value, formatter);
                } catch (DateTimeParseException e2) {
                }
            }
        }
        
        return null;
    }

    private static LocalDateTime parseLocalDateTime(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException e1) {
            String[] patterns = {
                "yyyy-MM-dd HH:mm:ss",
                "dd/MM/yyyy HH:mm:ss",
                "MM/dd/yyyy HH:mm:ss"
            };
            
            for (String pattern : patterns) {
                try {
                    DateTimeFormatter formatter = 
                        DateTimeFormatter.ofPattern(pattern);
                    return LocalDateTime.parse(value, formatter);
                } catch (DateTimeParseException e2) {
                }
            }
        }
        
        return null;
    }

    private static Object getDefaultValue(Class<?> type) {
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == double.class) return 0.0;
        if (type == float.class) return 0.0f;
        if (type == boolean.class) return false;
        if (type == byte.class) return (byte) 0;
        if (type == short.class) return (short) 0;
        if (type == char.class) return '\u0000';
        return null;
    }

    public static Object[] prepareMethodParameters(HttpServletRequest req, Method method) {
        try {

            Parameter[] parameters = method.getParameters();
            Object[] paramValues = new Object[parameters.length];

            boolean multipart =
                req.getContentType() != null &&
                req.getContentType().toLowerCase().startsWith("multipart/");

            for (int i = 0; i < parameters.length; i++) {

                Parameter param = parameters[i];

                if (param.isAnnotationPresent(Session.class)) {
                    if (Map.class.isAssignableFrom(param.getType())) {
                        Type genericType = param.getParameterizedType();
                        if (genericType instanceof ParameterizedType pt) {
                            Type[] types = pt.getActualTypeArguments();
                            if (types.length == 2
                                && types[0] == String.class
                                && types[1] == Object.class) {
                                paramValues[i] = new SessionMap(req.getSession());
                                continue;
                            }
                        }
                    }
                    throw new RuntimeException(
                        "@Session doit être utilisé sur un paramètre de type Map<String, Object>"
                    );
                }

                if (Map.class.isAssignableFrom(param.getType())
                    && param.getParameterizedType() instanceof ParameterizedType pType) {

                    Type[] args = pType.getActualTypeArguments();

                    if (args.length == 2
                            && args[0] == String.class
                            && args[1] == byte[].class) {

                        Map<String, byte[]> fileMap = new HashMap<>();

                        if (multipart) {

                            for (Part part : req.getParts()) {

                                if (part.getContentType() == null)
                                    continue;

                                try (InputStream is = part.getInputStream()) {

                                    byte[] content = is.readAllBytes();

                                    String original = part.getSubmittedFileName();
                                    String key = original;

                                    // si plusieurs fichiers même nom, on renomme
                                    int index = 0;
                                    while (fileMap.containsKey(key)) {
                                        key = original + index;
                                        index++;
                                    }

                                    fileMap.put(key, content);
                                }
                            }
                        }

                        paramValues[i] = fileMap;
                        continue;
                    }
                }

                if (Map.class.isAssignableFrom(param.getType())) {

                    Map<String, Object> mapParam = new HashMap<>();

                    Map<String, String[]> requestParams = req.getParameterMap();

                    for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
                        String key = entry.getKey();
                        String[] values = entry.getValue();

                        if (values != null && values.length == 1) {
                            mapParam.put(key, values[0]);
                        } else {
                            mapParam.put(key, values);
                        }
                    }

                    paramValues[i] = mapParam;
                    continue;
                }


                String paramName;

                if (param.isAnnotationPresent(Param.class)) {
                    paramName = param.getAnnotation(Param.class).value();
                } else {
                    paramName = param.getName();
                }

                String paramValue = req.getParameter(paramName);

                if (paramValue == null) {
                    Object attrValue = req.getAttribute(paramName);
                    if (attrValue != null) {
                        paramValue = attrValue.toString();
                    }
                }

                if (ObjectBinder.isSimpleType(param.getType())) {
                    paramValues[i] =
                        ParametersHandler.convertToType(paramValue, param.getType());
                    continue;
                }

                paramValues[i] = ObjectBinder.bind(param, req);
            }

            return paramValues;

        } catch (Exception e) {
            throw new RuntimeException("Error preparing method parameters", e);
        }
    }
}
