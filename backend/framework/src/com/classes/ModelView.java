package com.classes;

import java.util.HashMap;
import java.util.Map;

public class ModelView {
    private String view;
    private Map<String, Object> data;

    public ModelView() {
        this.data = new HashMap<String, Object>();
    }

    public ModelView(String view) {
        this.view = view;
        this.data = new HashMap<String, Object>();
    }

    public ModelView(Map<String, Object> data) {
        this.data = data;
    }

    public ModelView(String view, Map<String, Object> data) {
        this.view = view;
        this.data = data;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public void addData(String key, Object value) {
        if (this.data != null) {
            this.data.put(key, value);
        }
    }

}   
