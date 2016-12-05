package com.liuhaoyuan.myplayer.domain;

/**
 * Created by liuhaoyuan on 2016/7/25.
 */
public class Lyric {
    public String text;
    public long timePoint;
    public long duration;

    @Override
    public String toString() {
        return "Lyric{" +
                "text='" + text + '\'' +
                ", timePoint=" + timePoint +
                ", duration=" + duration +
                '}';
    }
}
