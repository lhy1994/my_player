package com.liuhaoyuan.myplayer.domain.music;

import java.util.ArrayList;

/**
 * Created by hyliu on 2017/1/12.
 */

public class XiaMiRanksInfo {
    public Body showapi_res_body;

    public class Body {
        public Page pagebean;

        public class Page {
            public String allPages;
            public String currentPage;
            public ArrayList<XiaMiSongInfo> contentlist;
        }
    }
}
