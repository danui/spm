package com.apfrank.json;

import java.util.LinkedList;
import java.util.Iterator;

public class JsonArray implements JsonValue {
    
    private LinkedList<JsonValue> ary;

    public JsonArray() {
        ary = new LinkedList<JsonValue>();
    }
    
    public JsonArray append(JsonValue val) {
        ary.add(val);
        return this;
    }
    
    public JsonArray prepend(JsonValue val) {
        ary.addFirst(val);
        return this;
    }
    
    public String toJson() {
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        Iterator<JsonValue> iter = ary.iterator();
        sb.append("[");
        while (iter.hasNext()) {
            if (first) {
                first = false;
            } else {
                sb.append(",");
            }
            sb.append(iter.next().toJson());
        }
        sb.append("]");
        return sb.toString();
    }
}
