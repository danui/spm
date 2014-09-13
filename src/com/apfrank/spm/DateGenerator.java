package com.apfrank.spm;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

/**
 * A date generator.
 */
public class DateGenerator implements Iterator<Date> {

    private static final long SECOND = 1000;
    private static final long MINUTE = 60 * SECOND;
    private static final long HOUR = 60 * MINUTE;
    private static final long DAY = 24 * HOUR;
    
    private Date nextDate;
    private long lastTime;
    private long interval;
    private long offset;
    
    public static DateGenerator createNatural(Date first, Date last) {
        long duration = last.getTime() - first.getTime();
        long intervals[] = {1*HOUR, 2*HOUR, 4*HOUR, 8*HOUR, 12*HOUR,
                            1*DAY, 2*DAY, 7*DAY};
        int i = 0;
        while (duration / intervals[i] > 48) {
            if (i == intervals.length - 1) {
                break;
            }
            i += 1;
        }
        long interval = intervals[i];
        
        if (interval >= 1*DAY) {
            Calendar cal = Calendar.getInstance();
            long offset = cal.get(Calendar.ZONE_OFFSET);
            offset += 8 * HOUR;
            return new DateGenerator(first, last, interval, offset);
        }
        return new DateGenerator(first, last, interval, 0); 
    }
    
    
    /**
     * Create a DateGenerator that samples daily at the specified hourOfDay.
     */
    public static DateGenerator createDaily(Date first, Date last, long hourOfDay) {
        if (!(0 <= hourOfDay && hourOfDay <= 23)) {
            throw new RuntimeException("hourOfDay should be 0-23, it is " + hourOfDay);
        }
        long interval = DAY;
        Calendar cal = Calendar.getInstance();
        long offset = cal.get(Calendar.ZONE_OFFSET);
        offset += hourOfDay * HOUR;
        return new DateGenerator(first, last, interval, offset);
    }
    
    /**
     * Create a DateGenerator that samples every hour.
     */
    public static DateGenerator createHourly(Date first, Date last) {
        return new DateGenerator(first, last, HOUR, 0);
    }

    /**
     * @param first First date.
     * @param last Last date.
     * @param interval Intervals in milliseconds.
     * @param offset Offset alignment.
     */
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
    
    @Override
    public void remove() {
        throw new RuntimeException("Not implemented");
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
