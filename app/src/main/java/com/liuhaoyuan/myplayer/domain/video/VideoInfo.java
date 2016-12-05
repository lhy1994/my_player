package com.liuhaoyuan.myplayer.domain.video;

import java.io.Serializable;

/**
 * Created by liuhaoyuan on 2016/7/21.
 */
public class VideoInfo implements Serializable{


    private String title;
    private String duration;
    private long size;
    private String data;

    @Override
    public String toString() {
        return "VideoInfo{" +
                "title='" + title + '\'' +
                ", duration='" + duration + '\'' +
                ", size=" + size +
                ", data='" + data + '\'' +
                '}';
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public String getDuration() {
        return duration;
    }

    public long getSize() {
        return size;
    }

    public String getData() {
        return data;
    }
}
