package com.apfrank.json;

public class JsonBoolean implements JsonValue {
    
    private Boolean val;

    public JsonBoolean(Boolean val) {
        this.val = val;
    }
    
    public String toJson() {
        return val.toString();
    }
}
