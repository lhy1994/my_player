package com.liuhaoyuan.myplayer.api;

/**
 * Created by hyliu on 2017/1/11.
 */

public abstract class DataObserver<T> {
    public static final int TYPE_QQ_RANKS=1;
    public static final int TYPE_SONG=2;
    public static final int TYPE_LYRIC=3;
    public static final int TYPE_XIAMI_RANKS=4;
    public static final int TYPE_ALI_RANKS=5;
    public static final int TYPE_XIAMI_ARTIST=6;
    public static final int TYPE_XIAMI_ARTIST_ALBUM=7;
    public static final int TYPE_XIAMI_ARTIST_SONGS=8;
    public static final int TYPE_XIAMI_ALBUM_SONGS=9;
    public static final int TYPE_LOCAL_MUSIC=10;
    private int dataType;

    public int getDataType() {
        return dataType;
    }

    public DataObserver(int dataType){
        this.dataType=dataType;
    }

    public abstract void onComplete(T data);
}
