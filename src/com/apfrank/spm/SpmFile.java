package com.apfrank.spm;

import java.util.LinkedList;
import java.util.TreeMap;
import java.util.SortedMap;
import java.util.Date;

public class SpmFile {

    private String name;
    private LinkedList<String> lines;
    private TreeMap<Date,SpmCounts> historicalCounts;

    public SpmFile(String name) {
        setName(name);
        this.lines = new LinkedList<String>();
        this.historicalCounts = new TreeMap<Date,SpmCounts>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addLine(String line) {
        lines.add(line);
    }

    public void addCounts(Date date, SpmCounts counts) {
        historicalCounts.put(date, counts);
    }

    public String getName() {
        return name;
    }

    public Iterable<String> getLines() {
        return lines;
    }

    public SpmCounts getCounts() {
        Date lastDate = historicalCounts.lastKey();
        return historicalCounts.get(lastDate);
    }

    public SortedMap<Date,SpmCounts> getHistoricalCounts() {
        return historicalCounts;
    }

}
