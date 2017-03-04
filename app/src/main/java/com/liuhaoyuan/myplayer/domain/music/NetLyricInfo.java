package com.liuhaoyuan.myplayer.domain.music;

/**
 * qq音乐根据歌曲id搜歌词返沪的结果
 */
public class NetLyricInfo {
    public lyricBody showapi_res_body;
    public class lyricBody{
        public String lyric;
        public String lyric_txt;
    }
}
