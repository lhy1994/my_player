package com.liuhaoyuan.myplayer.domain.music;

import java.util.ArrayList;

/**
 * Created by hyliu on 2017/1/15.
 */

public class AliRanksInfo {
    public Body showapi_res_body;
    public class Body{
        public String allPages;
        public String currentPage;
        public ArrayList<XiaMiSongInfo> contentlist;
    }
}
