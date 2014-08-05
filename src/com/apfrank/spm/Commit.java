package com.apfrank.spm;

import java.util.Date;
import java.util.TreeSet;
import java.util.Iterator;

public class Commit {

    private String hash;
    private Date date;

    /**
     * Set of paths associated with this Commit.
     */
    private TreeSet<Path> pathSet;

    public Commit(String hash, Date date) {
        this.hash = hash;
        this.date = date;
        this.pathSet = new TreeSet<Path>();
    }

    public String getHash() {
        return hash;
    }

    public Date getDate() {
        return date;
    }

    /**
     * Add a Path to a Commit if it is not already associated with
     * the commit.
     */
    public void addPath(Path path) {
        pathSet.add(path);
    }

    /**
     * @return Iterator over the set of paths associated with this commit.
     */
    public Iterator<Path> getPathIterator() {
        return pathSet.iterator();
    }

}