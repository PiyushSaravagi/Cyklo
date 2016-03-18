package xyz.cyklo.api;

import android.util.Log;

import java.util.Calendar;

/**
 * Created by Aashish Nehete on 20-Jan-16.
 */
public class Amount extends Time {
    private Time t;
    private int amount;
    public int minutes;

    public Amount(Time t) {
        this.t = t;
    }

    public int getAmount() {
        //Setting end time as now if none set
        if (t.getEndTime().equals(setTimeFromString(Time.defaultTime).getTime())) {
            Calendar now = Calendar.getInstance();
            String timeNow = DATE_FORMAT.format(now.getTime());
            t.setEndTime(timeNow);
        }


        minutes = (int) t.getTimeDifference();
        Log.i("CYKLO.AMOUNT", "Start Time: ".concat(t.getStartTime().toString()));
        Log.i("CYKLO.AMOUNT", "End Time: ".concat(t.getEndTime().toString()));
        Log.i("CYKLO.AMOUNT", "Minutes: ".concat(String.valueOf(minutes)));
        Log.i("CYKLO.AMOUNT", "Diff: ".concat(String.valueOf(t.getTimeDifference())));
        //Rate Card
        amount = (minutes / 30) * 10 + 10;
        return amount;
    }
}
