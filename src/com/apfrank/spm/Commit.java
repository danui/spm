package com.apfrank.spm;

import java.util.Date;
import java.util.TreeMap;
import java.util.Iterator;

public class Commit {

    private String hash;
    private Date date;
    private TreeMap<Path,DataPoint> dataMap;

    public Commit(String hash, Date date) {
        this.hash = hash;
        this.date = date;
        this.dataMap = new TreeMap<Path,DataPoint>();
    }
    
    public String getHash() {
        return hash;
    }

    public Date getDate() {
        return date;
    }

    public void addDataPoint(DataPoint dataPoint) {
        dataMap.put(dataPoint.getPath(), dataPoint);
    }

    public DataPoint getDataPoint(Path path) {
        return dataMap.get(path);
    }

    /**
     * @return Iterator over data points associated with this commit.
     */
    public Iterator<DataPoint> getIterator() {
        return dataMap.values().iterator();
    }
}