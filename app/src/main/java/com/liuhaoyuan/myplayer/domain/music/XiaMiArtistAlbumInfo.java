package com.liuhaoyuan.myplayer.domain.music;

import java.util.ArrayList;

/**
 * Created by hyliu on 2017/1/12.
 */

public class XiaMiArtistAlbumInfo {
    public Body showapi_res_body;
    public class Body{
        public Page pagebean;
    }
    public class Page{
        public String allPages;
        public ArrayList<AlbumInfo> contentlist;
        public String currentPage;
    }

    public static class AlbumInfo{
        public String pubTime;
        public String albumName;
        public String albumScore;
        public String albumId;
        public String company;
        public String albumStyle;
        public String language;
        public String artistName;
        public String albumLogo;

        @Override
        public String toString() {
            return "AlbumInfo{" +
                    "pubTime='" + pubTime + '\'' +
                    ", albumName='" + albumName + '\'' +
                    ", albumScore='" + albumScore + '\'' +
                    ", albumId='" + albumId + '\'' +
                    ", company='" + company + '\'' +
                    ", albumStyle='" + albumStyle + '\'' +
                    ", language='" + language + '\'' +
                    ", artistName='" + artistName + '\'' +
                    ", albumLogo='" + albumLogo + '\'' +
                    '}';
        }
    }
}
