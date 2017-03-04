package com.liuhaoyuan.myplayer.domain.music;

import java.util.ArrayList;

/**
 * Created by hyliu on 2017/1/12.
 */

public class XiaMiAlbumSongsInfo {
    public Body showapi_res_body;
    public class Body{
        public XiaMiArtistAlbumInfo.AlbumInfo album;
        public ArrayList<XiaMiSongInfo> list;
    }
}
