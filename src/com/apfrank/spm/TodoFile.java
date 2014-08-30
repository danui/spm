package com.apfrank.spm;

import java.io.File;
import java.util.TreeMap;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.security.MessageDigest;

public class TodoFile implements Comparable<TodoFile>, DataSource {

    private Project project;
    private Path path;
    private TreeMap<Date,DataPoint> dataPointMap;
    private String id;
    
    public TodoFile(Project project, Path path) {
        this.project = project;
        this.path = path;
        this.dataPointMap = new TreeMap<Date,DataPoint>();
        this.id = null;
    }

    @Override
    public int compareTo(TodoFile other) {
        return this.getPath().compareTo(other.getPath());
    }
    
    @Override
    public String toString() {
        return path.toString();
    }
    
    /**
     * @return Id generated from name.
     */
    @Override // DataSource
    public String getId() {
        if (id == null) {
            id = HashTool.getMd5(this.getName());
        }
        return id;
    }
    
    @Override // DataSource
    public String getName() {
        return path.getLastName();
    }
    
    public Path getPath() {
        return path;
    }
    
    public File getFile() {
        return path.getFile(project.getRepositoryDir());
    }
    
    @Override // DataSource
    public Date getFirstDate() {
        return dataPointMap.firstKey();
    }
    
    @Override // DataSource
    public Date getLastDate() {
        return dataPointMap.lastKey();
    }

    @Override // DataSource
    public int getDoneCount(Date date) {
        DataPoint dataPoint = getDataPointAtOrBefore(date);
        if (dataPoint != null) {
            return dataPoint.getCount("DONE");
        }
        return 0;
    }
    
    @Override // DataSource
    public int getTodoCount(Date date) {
        DataPoint dataPoint = getDataPointAtOrBefore(date);
        if (dataPoint != null) {
            return dataPoint.getCount("TODO");
        }
        return 0;
    }

    @Override // DataSource
    public int getTotalCount(Date date) {
        DataPoint dataPoint = getDataPointAtOrBefore(date);
        if (dataPoint != null) {
            return dataPoint.getCount("DONE")
                +  dataPoint.getCount("TODO");
            // NOTE: This is different from dataPoint.getTotalCount() because
            // the SymbolFilter may recognise more than just done and todo.
        }
        return 0;
    }
    
    @Override // DataSource
    public Iterator<Date> getDates() {
        return dataPointMap.keySet().iterator();
    }
    
    public DataPoint getLastDataPoint() {
        return dataPointMap.get(getLastDate());
    }
    
    public DataPoint getDataPointAtOrBefore(Date date) {
        Map.Entry<Date,DataPoint> entry = dataPointMap.floorEntry(date);
        if (entry == null) {
            return null;
        }
        return entry.getValue();
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
