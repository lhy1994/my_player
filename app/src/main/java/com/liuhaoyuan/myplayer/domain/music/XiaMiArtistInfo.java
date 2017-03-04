package com.liuhaoyuan.myplayer.domain.music;

/**
 * Created by hyliu on 2017/1/12.
 */

public class XiaMiArtistInfo {
    public Body showapi_res_body;
    public class Body{
        public String ret_code;
        public Artist artist;
    }
    public class Artist{
        public String logo;
        public String area;
        public String style;
        public String profile;

        @Override
        public String toString() {
            return "Artist{" +
                    "logo='" + logo + '\'' +
                    ", area='" + area + '\'' +
                    ", style='" + style + '\'' +
                    ", profile='" + profile + '\'' +
                    '}';
        }
    }
}
