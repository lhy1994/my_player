package com.liuhaoyuan.myplayer.domain.video;

import android.graphics.drawable.Drawable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by liuhaoyuan on 2016/7/28.
 */
public class NetVideoItem implements Serializable{
    public String name;
    public String[] performer;
    public String[] director;
    public String[] genre;
    public String score;
    public String vpic;
    public String pic;
    public String brief;
    public String playurl;
    public String trailerUrl;
    public String hd;
    public String totalvv;
    public String displaystatus;
    public String paid;
    public String releaseDate;
    public String releaseYear;
    public String programmeId;
    public ArrayList<ProgrammeSite> programmeSite;

    @Override
    public String toString() {
        return "NetVideoItem{" +
                "name='" + name + '\'' +
                ", performer=" + Arrays.toString(performer) +
                ", director=" + Arrays.toString(director) +
                ", genre=" + Arrays.toString(genre) +
                ", score='" + score + '\'' +
                ", vpic='" + vpic + '\'' +
                ", pic='" + pic + '\'' +
                ", brief='" + brief + '\'' +
                ", playurl='" + playurl + '\'' +
                ", trailerUrl='" + trailerUrl + '\'' +
                ", hd='" + hd + '\'' +
                ", totalvv='" + totalvv + '\'' +
                ", displaystatus='" + displaystatus + '\'' +
                ", paid='" + paid + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", releaseYear='" + releaseYear + '\'' +
                ", programmeId='" + programmeId + '\'' +
                ", programmeSite=" + programmeSite +
                '}';
    }

    public class ProgrammeSite implements Serializable{
        public String siteId;
        public String hd;
        public String displaystatus;
        public ArrayList<Episode> episode;

        @Override
        public String toString() {
            return "ProgrammeSite{" +
                    "siteId='" + siteId + '\'' +
                    ", hd='" + hd + '\'' +
                    ", displaystatus='" + displaystatus + '\'' +
                    ", episode=" + episode +
                    '}';
        }
    }

    public class Episode implements Serializable{
        public String name;
        public String url;

        @Override
        public String toString() {
            return "Episode{" +
                    "name='" + name + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }
    }
}
