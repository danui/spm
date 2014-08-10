package com.apfrank.spm;

import java.io.File;
import java.util.TreeMap;
import java.util.Date;
import java.util.Iterator;

public class TodoFile implements Comparable<TodoFile> {

    private Project project;
    private Path path;
    private TreeMap<Date,DataPoint> dataPointMap;
    
    public TodoFile(Project project, Path path) {
        this.project = project;
        this.path = path;
        this.dataPointMap = new TreeMap<Date,DataPoint>();
    }

    @Override
    public int compareTo(TodoFile other) {
        return this.getPath().compareTo(other.getPath());
    }
    
    @Override
    public String toString() {
        return getName();
    }
    
    public String getName() {
        return path.toString();
    }
    
    public Path getPath() {
        return path;
    }
    
    public void addDataPoint(DataPoint dataPoint) {
        // NOTE: If two datapoints have the same date, then the latest
        // dataPoint to be added will replace the other. This is possible
        // when there is a fork in the git tree. But it is not a big
        // issue because eventually they will merge.
        dataPointMap.put(dataPoint.getDate(), dataPoint);
    }
    
    public Iterator<DataPoint> getDataPointIterator() {
        return dataPointMap.values().iterator();
    }
}
