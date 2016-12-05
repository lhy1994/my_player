package com.liuhaoyuan.myplayer.domain;

/**
 * Created by liuhaoyuan on 2016/7/27.
 */
public class NetLyricInfo {
    public String showapi_res_code;
    public String showapi_res_error;
    public lyricBody showapi_res_body;
    public class lyricBody{
        public String ret_code;
        public String lyric;
        public String lyric_txt;
    }
}
