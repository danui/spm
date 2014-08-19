package com.apfrank.json;

import java.util.TreeMap;
import java.util.Iterator;

public class JsonObject implements JsonValue {
    
    private TreeMap<JsonString,JsonValue> map;

    public JsonObject() {
        map = new TreeMap<JsonString,JsonValue>();
    }
    
    public JsonObject put(JsonString key, JsonValue val) {
        map.put(key, val);
        return this;
    }
    
    public JsonObject put(String strKey, JsonValue val) {
        map.put(new JsonString(strKey), val) ;
        return this;
    }
    
    public JsonObject put(String strKey, String val) {
        map.put(new JsonString(strKey), new JsonString(val)) ;
        return this;
    }
    
    public String toJson() {
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        Iterator<JsonString> iter = map.keySet().iterator();
        sb.append("{");
        while (iter.hasNext()) {
            if (first) {
                first = false;
            } else {
                sb.append(",");
            }
            JsonString key = iter.next();
            JsonValue val = map.get(key);
            sb.append(key.toJson()).append(":").append(val.toJson());
        }
        sb.append("}");
        return sb.toString();
    }
}
