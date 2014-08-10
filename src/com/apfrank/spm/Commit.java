package com.apfrank.spm;

import java.util.Date;
import java.util.TreeMap;
import java.util.Iterator;

import org.eclipse.jgit.revwalk.RevCommit;

/**
 * A small wrapper around RevCommit that associates it with DataPoints.
 */
public class Commit {
    private RevCommit revCommit;
    private String hash;
    private Date date;
    private TreeMap<Path,DataPoint> dataMap;

    public Commit(RevCommit revCommit) {
        this.revCommit = revCommit;
        this.hash = revCommit.getName();
        this.date = revCommit.getAuthorIdent().getWhen();
        this.dataMap = new TreeMap<Path,DataPoint>();
    }

    public RevCommit getRevCommit() {
        return revCommit;
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
    public Iterator<DataPoint> getDataPointIterator() {
        return dataMap.values().iterator();
    }
}