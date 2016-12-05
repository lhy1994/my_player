package com.liuhaoyuan.myplayer.domain.video;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by liuhaoyuan on 2016/7/30.
 */
public class NetVideoAddress implements Serializable{
    public ArrayList<AddressInfo> data;

    public class AddressInfo{
        public ArrayList<VideoAddress> addresses;
        public String source_site;
        public String total;

        @Override
        public String toString() {
            return "NetVideoAddress{" +
                    "addresses=" + addresses +
                    ", source_site='" + source_site + '\'' +
                    ", total='" + total + '\'' +
                    '}';
        }
    }

    public class VideoAddress implements Serializable{
        public String name;
        public String url;
        public String order;
        public String video_type;
        public String rurl;

        @Override
        public String toString() {
            return "VideoAddress{" +
                    "name='" + name + '\'' +
                    ", url='" + url + '\'' +
                    ", order='" + order + '\'' +
                    ", video_type='" + video_type + '\'' +
                    ", rurl='" + rurl + '\'' +
                    '}';
        }
    }
}
