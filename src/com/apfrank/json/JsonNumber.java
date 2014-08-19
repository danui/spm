package com.apfrank.json;

public class JsonNumber implements JsonValue {
    
    private Number val;

    public JsonNumber(Number val) {
        this.val = val;
    }
    
    public JsonNumber(int val) {
        this.val = new Integer(val);
    }
    
    public JsonNumber(double val) {
        this.val = new Double(val);
    }
    
    public JsonNumber(long val) {
        this.val = new Long(val);
    }
    
    public JsonNumber(float val) {
        this.val = new Float(val);
    }
    
    public String toJson() {
        return val.toString();
    }
}
