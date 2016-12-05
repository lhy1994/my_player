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

    public class ResultInfo{
        public String provider;
        public String code;
        public String desc;
    }
}
