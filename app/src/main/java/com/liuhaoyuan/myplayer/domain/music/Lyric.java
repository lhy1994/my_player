package com.liuhaoyuan.myplayer.domain.music;

/**
 * 歌词实体类
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
