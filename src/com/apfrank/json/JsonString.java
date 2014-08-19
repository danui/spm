package com.apfrank.json;

public class JsonString implements JsonValue, Comparable<JsonString> {
    private String str;
    public JsonString(String str) {
        this.str = str;
    }
    
    @Override
    public String toJson() {
        return "\"" + this.str.replaceAll("\"", "\\\"") + "\"";
    }
    
    /**
     * Get actual string.
     */
    public String getString() {
        return str;
    }
    
    @Override
    public int compareTo(JsonString other) {
        return this.str.compareTo(other.getString());
    }
}
