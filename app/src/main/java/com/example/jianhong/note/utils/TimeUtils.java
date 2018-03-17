package com.example.jianhong.note.utils;

import android.content.Context;

import com.example.jianhong.note.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

    public static final long MINUTE_Millis = 60 * 1000;
    public static final long HOUR_Millis = 60 * MINUTE_Millis;
    public static final long HALF_HOUR_Millis = HOUR_Millis / 2;
    public static final long DAY_Millis = 24 * HOUR_Millis;
    public static final long MONTH_Millis = 30 * DAY_Millis;
    public static final long YEAR_Millis = 365 * DAY_Millis;
    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("MM-dd HH:mm");

    private TimeUtils() {
        throw new AssertionError();
    }

    @SuppressWarnings("Deprecated")
    public static String getConciseTime(long timeInMillis, long nowInMillis, Context context) {
        if (context == null)
            return "";
        long diff = nowInMillis - timeInMillis;
        if (diff >= YEAR_Millis) {
            int year = (int) (diff / YEAR_Millis);
            return context.getString(R.string.before_year, year);
        }
        if (diff >= MONTH_Millis) {
            int month = (int) (diff / MONTH_Millis);
            return context.getString(R.string.before_month, month);
        }

        if (diff >= DAY_Millis) {
            int day = (int) (diff / DAY_Millis);
            return context.getString(R.string.before_day, day);
        }

        if (diff >= HOUR_Millis) {
            int hour = (int) (diff / HOUR_Millis);
            return context.getString(R.string.before_hour, hour);
        }

        if (diff >= HALF_HOUR_Millis) {
            return context.getString(R.string.before_half_hour);
        }

        if (diff >= 3 * MINUTE_Millis) {
            int min = (int) (diff / MINUTE_Millis);
            return context.getString(R.string.before_minute, min);
        }

        return context.getString(R.string.just_now);
    }

    public static long getCurrentTimeInLong() {
        return System.currentTimeMillis();
    }

    /**
     * 描述给定时间戳是多久之前的时间
     * @param timeInMillis
     * @param context
     * @return
     */
    public static String getConciseTime(long timeInMillis, Context context) {
        return getConciseTime(timeInMillis, getCurrentTimeInLong(), context);
    }

    public static String getTime(long timeInMillis) {
        return DEFAULT_DATE_FORMAT.format(timeInMillis);
    }


}
