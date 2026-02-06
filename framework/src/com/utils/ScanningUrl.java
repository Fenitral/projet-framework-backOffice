package com.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.annotations.ControllerAnnotation;
import com.annotations.HandleUrl;
import com.annotations.PostMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.ServletContext;
import java.net.MalformedURLException;

import com.annotations.GetMapping;

public class ScanningUrl{
    public static List<Class<?>> getClasses(String packageName) throws IOException, ClassNotFoundException {
        String path = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(path);
        if (resource == null) return new ArrayList<>();

        File directory = new File(resource.getFile());
        return findClasses(directory, packageName);
    }

    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) return classes;

        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().replaceAll("\\.class$", "");
                classes.add(Class.forName(className)); 
            }
        }
        return classes;
    }

    public static Map<String, List<MappingHandler>> scanUrlMappings(String packageName) throws Exception {
        Map<String, List<MappingHandler>> urlMappings = new HashMap<>();
        List<Class<?>> controllers = getClasses(packageName);
        for (Class<?> clazz : controllers) {
            if (clazz.isAnnotationPresent(ControllerAnnotation.class)) {
                for (Method method : clazz.getDeclaredMethods()) {
                    String url = "";
                    String tempMethode = "";
                    if(method.isAnnotationPresent(PostMapping.class)){
                        url = method.getAnnotation(PostMapping.class).value();
                        tempMethode = "POST";
                    }
                    else if(method.isAnnotationPresent(GetMapping.class)){
                        url = method.getAnnotation(GetMapping.class).value();
                        tempMethode = "GET";
                    }
                    else if(method.isAnnotationPresent(HandleUrl.class)){
                        url = method.getAnnotation(HandleUrl.class).value();
                        tempMethode = "ALL";
                    }
                    urlMappings.computeIfAbsent(url, k -> new ArrayList<>()).add(new MappingHandler(clazz, method, new UrlPattern(url), tempMethode));
                }
            }
        }
        return urlMappings;
    }

    public static MappingHandler findMatchingPattern(String path, Map<String, List<MappingHandler>> urlMappings, HttpServletRequest req) {
        for (List<MappingHandler> handler : urlMappings.values()) {
            for(MappingHandler h : handler){
                if (h.getUrlPattern().matches(path)) {       
                    Map<String, String> params = h.getUrlPattern().extractParams(path);
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        req.setAttribute(entry.getKey(), entry.getValue());
                    }
                    return h;
                }
            }
        }
        return null;
    }

    public static String findExistingIndex(ServletContext ctx) {
        List<String> INDEX_FILES = List.of("/index.html", "/index.htm", "/index.jsp");
        for (String index : INDEX_FILES) {
            try {
                if (ctx.getResource(index) != null) {
                    return index;
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException("Invalid path in INDEX_FILES: " + index, e);
            }
        }
        return null;
    }
}