package com.liuhaoyuan.myplayer.utils;

import android.content.Context;

import com.liuhaoyuan.myplayer.R;

/**
 * Created by liuhaoyuan on 2016/7/22.
 */
public class TimeFormatUtils {
    public static String timeFormat(long ms){
        long hour = ms / (60 * 60 * 1000);
        long minute = (ms - hour * 60 * 60 * 1000) / (60 * 1000);
        long second = (ms - hour * 60 * 60 * 1000 - minute * 60 * 1000) / 1000;
        if (second >= 60) {
            second = second % 60;
            minute += second / 60;
        }
        if (minute >= 60) {
            minute = minute % 60;
            hour += minute / 60;
        }
        String sh = "";
        String sm = "";
        String ss = "";
        if (hour < 10) {
            sh = "0" + String.valueOf(hour);
        } else {
            sh = String.valueOf(hour);
        }
        if (minute < 10) {
            sm = "0" + String.valueOf(minute);
        } else {
            sm = String.valueOf(minute);
        }
        if (second < 10) {
            ss = "0" + String.valueOf(second);
        } else {
            ss = String.valueOf(second);
        }
        return sh+":"+sm+":"+ss;
    }

    public static String timeFormatNoHour(long ms){
        String s = timeFormat(ms);
        int i = s.indexOf(":");
        return s.substring(i+1);
    }

    public static final String makeShortTimeString(final Context context, long secs) {
        long hours, mins;

        hours = secs / 3600;
        secs %= 3600;
        mins = secs / 60;
        secs %= 60;

        final String durationFormat = context.getResources().getString(
                hours == 0 ? R.string.durationformatshort : R.string.durationformatlong);
        return String.format(durationFormat, hours, mins, secs);
    }
}
