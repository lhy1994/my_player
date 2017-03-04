package com.liuhaoyuan.myplayer.domain.video;

import java.util.ArrayList;

/**
 * Created by liuhaoyuan on 2016/7/28.
 */
public class NetVideoInfo {
    public ResultInfo e;
    public String cost;
    public String total;
    public ArrayList<NetVideoItem> data;

    @Override
    public String toString() {
        return "NetVideoInfo{" +
                "e=" + e +
                ", cost='" + cost + '\'' +
                ", total='" + total + '\'' +
                ", data=" + data +
                '}';
    }

    public class ResultInfo{
        @Override
        public String toString() {
            return "ResultInfo{" +
                    "provider='" + provider + '\'' +
                    ", code='" + code + '\'' +
                    ", desc='" + desc + '\'' +
                    '}';
        }

        public String provider;
        public String code;
        public String desc;

    }
}
