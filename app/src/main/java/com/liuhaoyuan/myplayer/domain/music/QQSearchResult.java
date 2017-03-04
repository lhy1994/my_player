package com.liuhaoyuan.myplayer.domain.music;

import com.liuhaoyuan.myplayer.aidl.Song;

import java.util.ArrayList;

/**
 * qq音乐根据人名或歌名搜索返回的结果
 */

public class QQSearchResult {
    public ResultBody showapi_res_body;

   public class ResultBody{
        public PageBean pagebean;
    }

    public class PageBean{
        public String allPages;
        public ArrayList<Song> contentlist;
        public String currentPage;
    }
}
