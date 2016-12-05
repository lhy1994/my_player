package com.liuhaoyuan.myplayer.utils;

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
}
