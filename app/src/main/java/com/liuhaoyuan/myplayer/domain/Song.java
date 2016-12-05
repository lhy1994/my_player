package com.liuhaoyuan.myplayer.domain;

import java.io.Serializable;

/**
 * Created by liuhaoyuan on 2016/7/27.
 */
public class Song implements Serializable{
    public String albumid;
    public String seconds;
    public String albumpic_big;
    public String albumpic_small;
    public String downUrl;
    public String url;
    public String singerid;
    public String singername;
    public String songid;
    public String songname;

    @Override
    public String toString() {
        return "Song{" +
                "albumid='" + albumid + '\'' +
                ", seconds='" + seconds + '\'' +
                ", albumpic_big='" + albumpic_big + '\'' +
                ", albumpic_small='" + albumpic_small + '\'' +
                ", downUrl='" + downUrl + '\'' +
                ", url='" + url + '\'' +
                ", singerid='" + singerid + '\'' +
                ", singername='" + singername + '\'' +
                ", songid='" + songid + '\'' +
                ", songname='" + songname + '\'' +
                '}';
    }
}
