package com.apfrank.spm;

import java.util.Date;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.LinkedList;

public class AggregatedSource implements DataSource {
    private String id;
    private String name;
    
    private class Stats {
        public int doneCount;
        public int todoCount;
        public int totalCount;
    }
    
    private TreeMap<Date,Stats> statMap;
    private LinkedList<DataSource> sources;
    private TreeSet<Date> dates;
    
    public AggregatedSource(String id, String name) {
        this.id = id;
        this.name = name;
        this.statMap = new TreeMap<Date,Stats>();
        this.sources = new LinkedList<DataSource>();
        this.dates = new TreeSet<Date>();
    }
    
    public void addSource(DataSource source) {
        sources.add(source);
        Iterator<Date> iter = source.getDates();
        while (iter.hasNext()) {
            dates.add(iter.next());
        }
        statMap.clear();
    }
    
    @Override // DataSource
    public String getId() {
        return id;
    }
    
    @Override // DataSource
    public String getName() {
        return name;
    }
    
    @Override // DataSource
    public int getDoneCount(Date date) {
        return lookupStats(date).doneCount;
    }
    
    @Override // DataSource
    public int getTodoCount(Date date) {
        return lookupStats(date).todoCount;
    }
    
    @Override // DataSource
    public int getTotalCount(Date date) {
        return lookupStats(date).totalCount;
    }

    private Stats lookupStats(Date date) {
        if (statMap.containsKey(date)) {
            return statMap.get(date);
        }
        Stats stats = new Stats();
        Iterator<DataSource> iter = sources.iterator();
        while (iter.hasNext()) {
            DataSource source = iter.next();
            stats.todoCount += source.getTodoCount(date);
            stats.doneCount += source.getDoneCount(date);
            stats.totalCount += source.getTotalCount(date);
        }
        statMap.put(date, stats);
        return stats;
    }
    
    @Override // DataSource
    public Date getFirstDate() {
        return dates.first();
    }
    
    @Override // DataSource
    public Date getLastDate() {
        return dates.last();
    }
    
    @Override // DataSource
    public Iterator<Date> getDates() {
        return dates.iterator();
    }
    
}
