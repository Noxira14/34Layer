package com.ibracodeko.layerthirtyfour.models;

public class ScanRequest {
    private String target;
    private int time;
    private String methods;
    private String key;

    public ScanRequest() {}

    public ScanRequest(String target, int time, String methods) {
        this.target = target;
        this.time = time;
        this.methods = methods;
        this.key = "qwertyuiop"; // Default API key
    }
    
    public ScanRequest(String target, int time, String methods, String key) {
        this.target = target;
        this.time = time;
        this.methods = methods;
        this.key = key;
    }

    // Getters and Setters
    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getMethods() {
        return methods;
    }

    public void setMethods(String methods) {
        this.methods = methods;
    }
    
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}