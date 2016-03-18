package xyz.cyklo.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by Aashish Nehete on 16-Jan-16.
 */
public class Time {
    private Calendar startTime;
    private Calendar endTime;
    public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public final static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    public final static String defaultTime = "0000-00-00 00:00:00";

    public Time() {
        setStartTime(defaultTime);
        setEndTime(defaultTime);
    }

    public Date getEndTime() {
        return this.endTime.getTime();
    }

    public void setEndTime(String endTime) {
        this.endTime = setTimeFromString(endTime);
    }

    public Date getStartTime() {
        return this.startTime.getTime();
    }

    public void setStartTime(String startTime) {
        this.startTime = setTimeFromString(startTime);
    }

    public long getTimeDifference() {
        long diffInMillis = endTime.getTimeInMillis() - startTime.getTimeInMillis();
        return TimeUnit.MINUTES.convert(diffInMillis,TimeUnit.MILLISECONDS);
    }

    protected Calendar setTimeFromString(String endTime) {
        Calendar temp = new GregorianCalendar();
        try {
            temp.setTime(DATE_FORMAT.parse(endTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return temp;
    }
}
