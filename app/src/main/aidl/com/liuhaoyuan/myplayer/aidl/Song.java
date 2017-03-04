package com.liuhaoyuan.myplayer.aidl;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by liuhaoyuan on 2016/7/27.
 */
public class Song implements Parcelable,Serializable{
    public String albumid;
    public String seconds;
    public String albumpic_big;
    public String albumpic_small;
    public String albumname;
    public String downUrl;
    @SerializedName(value = "url",alternate = {"m4a"})
    public String url;
    public String singerid;
    public String singername;
    public String songid;
    public String songname;

    public Song(){

    }

    protected Song(Parcel in) {
        albumid = in.readString();
        seconds = in.readString();
        albumpic_big = in.readString();
        albumpic_small = in.readString();
        albumname = in.readString();
        downUrl = in.readString();
        url = in.readString();
        singerid = in.readString();
        singername = in.readString();
        songid = in.readString();
        songname = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(albumid);
        dest.writeString(seconds);
        dest.writeString(albumpic_big);
        dest.writeString(albumpic_small);
        dest.writeString(albumname);
        dest.writeString(downUrl);
        dest.writeString(url);
        dest.writeString(singerid);
        dest.writeString(singername);
        dest.writeString(songid);
        dest.writeString(songname);
    }
}
