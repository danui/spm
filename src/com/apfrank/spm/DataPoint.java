package com.apfrank.spm;

import java.util.Date;

/**
 * A DataPoint exists in at the intersection of time and space.
 * Time in this case is specified by 'date' and space by 'path'.
 */
public class DataPoint {

    private Date date;
    private Path path;
    private int[] count;
    private int totalCount;

    public DataPoint(Date date, Path path) {
        this.date = date;
        this.path = path;

        count = new int[Symbols.NUM_SYMBOLS];
        for (int i = 0; i < Symbols.NUM_SYMBOLS; ++i) {
            count[i] = 0;
        }
        totalCount = 0;
    }

    public Date getDate() {
        return date;
    }
    
    public Path getPath() {
        return path;
    }

    public int getCount(int symbolCode) {
        return count[symbolCode];
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void increment(int symbolCode) {
        count[symbolCode] += 1;
        totalCount += 1;
    }
}
