package com.apfrank.spm;

import java.util.Date;
import java.util.Iterator;

/**
 * A date generator.
 */
public class DateGenerator implements Iterator<Date> {

    private Date nextDate;
    private long lastTime;
    private long interval;
    private long offset;
    
    
    /**
     * Create a DateGenerator that samples daily at the specified hourOfDay.
     */
    public static DateGenerator createDaily(Date first, Date last, long hourOfDay) {
        if (!(0 <= hourOfDay && hourOfDay <= 23)) {
            throw new RuntimeException("hourOfDay should be 0-23, it is " + hourOfDay);
        }
        long interval = 1000 * 60 * 60 * 24;
        Calendar cal = Calendar.getInstance();
        long offset = cal.get(Calendar.ZONE_OFFSET);
        offset += hourOfDay * 1000 * 60 * 60;
        return new DateGenerator(first, last, interval, offset);
    }
    
    /**
     * Create a DateGenerator that samples every hour.
     */
    public static DateGenerator createHourly(Date first, Date last) {
        return new DateGenerator(first, last, 1000*60*60, 0);
    }
    
    public DateGenerator(Date first, Date last, long interval, long offset) {
        if (interval <= 0) {
            throw new RuntimeException("invalid interval: " + interval);
        }
        this.nextDate = new Date(first.getTime());
        this.lastTime = last.getTime();
        this.interval = interval;
        this.offset = offset % interval;
    }

    @Override
    public boolean hasNext() {
        return (nextDate != null);
    }
    
    @Override
    public Date next() {
        if (!hasNext()) {
            return null;
        }
        Date date = nextDate;
        long currentTime = date.getTime();
        if (currentTime < lastTime) {
            long nextTime = getNextTime(currentTime);
            if (nextTime > lastTime) {
                nextTime = lastTime;
            }
            nextDate = new Date(nextTime);
        } else {
            nextDate = null;
        }
        return date;
    }
    
    private long getNextTime(long currentTime) {
        long currentOffset = currentTime % interval;
        if (currentOffset == offset) {
            return currentTime + interval;
        } else if (currentOffset < offset) {
            return currentTime + offset - currentOffset;
        } else {
            return currentTime + interval - currentOffset + offset;
        }
    }
}
