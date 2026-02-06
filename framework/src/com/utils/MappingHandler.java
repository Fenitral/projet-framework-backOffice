package com.utils;

import java.lang.reflect.Method;

public class MappingHandler {
    private Class<?> classe;
    private Method methode;
    private UrlPattern urlPattern;
    private String httpMethod;

    public MappingHandler(Class<?> classe, Method methode, UrlPattern urlPattern, String httpMethod) {
        this.classe = classe;
        this.methode = methode;
        this.urlPattern = urlPattern;
        this.httpMethod = httpMethod;
    }

    public MappingHandler(Method methode, String httpMethod) {
        this.methode = methode;
        this.httpMethod = httpMethod;
    }

    public MappingHandler(Class<?> classe, Method methode, UrlPattern urlPattern) {
        this.classe = classe;
        this.methode = methode;
        this.urlPattern = urlPattern;
    }

    public MappingHandler() {
    }

    public MappingHandler(Class<?> classe, Method methode) {
        this.classe = classe;
        this.methode = methode;
    }

    public Class<?> getClasse() {
        return classe;
    }

    public void setClasse(Class<?> classe) {
        this.classe = classe;
    }

    public Method getMethode() {
        return methode;
    }

    public void setMethode(Method methode) {
        this.methode = methode;
    }

    public UrlPattern getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(UrlPattern urlPattern) {
        this.urlPattern = urlPattern;
    }
    
    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

}
