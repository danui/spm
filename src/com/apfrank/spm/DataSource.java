package com.apfrank.spm;

import java.util.Date;
import java.util.Iterator;

/**
 * DataSource is a source of 'DONE' and 'TODO' counts.
 */
public interface DataSource {
    public String getId();
    public String getName();
    public int getDoneCount(Date date);
    public int getTodoCount(Date date);
    public int getTotalCount(Date date);
    public Date getFirstDate();
    public Date getLastDate();
    
    /**
     * Get an iterator over dates of distinct data points.
     *
     * @return Iterator.
     */
    public Iterator<Date> getDates();
}
