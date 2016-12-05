package com.liuhaoyuan.myplayer.domain;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by liuhaoyuan on 2016/7/26.
 */
public class NetMusicInfo {
    public String showapi_res_code;
    public String showapi_res_error;
    public NetMusicInfoBody showapi_res_body;
    public class NetMusicInfoBody{
        public String ret_code;
        public PageBean pagebean;
    }
    public class PageBean {
        public ArrayList<Song> songlist;
        public String total_song_num;
        public String ret_code;
        public String color;
        public String currentPage;

        @Override
        public String toString() {
            return "PageBean{" +
                    "songlist=" + songlist +
                    ", total_song_num='" + total_song_num + '\'' +
                    ", ret_code='" + ret_code + '\'' +
                    ", color='" + color + '\'' +
                    ", currentPage='" + currentPage + '\'' +
                    '}';
        }
    }
}
