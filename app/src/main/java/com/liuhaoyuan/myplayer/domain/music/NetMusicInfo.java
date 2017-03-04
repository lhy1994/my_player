package com.liuhaoyuan.myplayer.domain.music;

import com.liuhaoyuan.myplayer.aidl.Song;

import java.util.ArrayList;

/**
 * qq音乐榜单信息
 */
public class NetMusicInfo {
    public NetMusicInfoBody showapi_res_body;
    public class NetMusicInfoBody{
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
