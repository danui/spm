package com.apfrank.spm;

import java.util.Date;
import java.util.HashMap;

/**
 * A DataPoint exists in at the intersection of time and space.
 * Time in this case is specified by 'date' and space by 'path'.
 */
public class DataPoint {

    private Date date;
    private Path path;
    private HashMap<String,Integer> countMap;
    private int totalCount;

    public DataPoint(Date date, Path path) {
        this.date = date;
        this.path = path;
        this.countMap = new HashMap<String,Integer>();
        this.totalCount = 0;
    }

    public Date getDate() {
        return date;
    }
    
    public Path getPath() {
        return path;
    }

    public int getCount(String symbolName) {
        Integer count = countMap.get(symbolName);
        if (count != null) {
            return count.intValue();
        }
        return 0;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void increment(String symbolName) {
        Integer count = countMap.get(symbolName);
        if (count != null) {
            count = count + 1;
        } else {
            count = new Integer(1);
        }
        countMap.put(symbolName, count);
        totalCount += 1;
    }
}
