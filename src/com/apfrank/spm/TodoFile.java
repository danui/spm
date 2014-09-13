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
    private String id;
    private TreeMap<Date,Commit> commits;
    private TreeMap<Commit,DataPoint> points;

    private TreeMap<Date,DataPoint> dataPointMap;

    
    public TodoFile(Project project, Path path) {
        this.project = project;
        this.path = path;
        // TODO: delete dataPointMap.
        dataPointMap = new TreeMap<Date,DataPoint>();
        id = HashTool.getMd5(path.toString());
        commits = new TreeMap<Date,Commit>();
        Iterator<Commit> iter = project.getCommits(path).iterator();
        while (iter.hasNext()) {
            Commit i = iter.next();
            commits.put(i.getDate(), i);
        }
        points = new TreeMap<Commit,DataPoint>();
    }
        
    @Override
    public int compareTo(TodoFile other) {
        return this.getPath().compareTo(other.getPath());
    }
    
    @Override
    public String toString() {
        return path.toString();
    }
    
    @Override // DataSource
    public String getId() {
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
        return commits.firstKey();
    }
    
    @Override // DataSource
    public Date getLastDate() {
        return commits.lastKey();
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
        return commits.keySet().iterator();
    }
    
    public Commit getLastCommit() {
        if (commits.isEmpty()) {
            return null;
        }
        return commits.lastEntry().getValue();
    }
    
    public DataPoint getDataPoint(Commit commit) {
        if (points.containsKey(commit)) {
            return points.get(commit);
        } else {
            DataPoint p = project.createDataPoint(path, commit);
            points.put(commit, p);
            return p;
        }
    }
    
    public DataPoint getLastDataPoint() {
        return getDataPoint(getLastCommit());
    }
    
    public DataPoint getDataPointAtOrBefore(Date date) {
        Map.Entry<Date,Commit> e = commits.floorEntry(date);
        if (e == null) {
            return null;
        }
        return getDataPoint(e.getValue());
    }

    // TODO: DELETE
    public void addDataPoint(DataPoint dataPoint) {
        // NOTE: If two datapoints have the same date, then the latest
        // dataPoint to be added will replace the other. This is possible
        // when there is a fork in the git tree. But it is not a big
        // issue because eventually they will merge.
        dataPointMap.put(dataPoint.getDate(), dataPoint);
    }
    
    public Iterator<DataPoint> getDataPointIteratorW() {
        return dataPointMap.values().iterator();
    }
}
