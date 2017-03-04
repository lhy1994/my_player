package com.liuhaoyuan.myplayer.api;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.google.gson.Gson;
import com.liuhaoyuan.myplayer.domain.music.AliRanksInfo;
import com.liuhaoyuan.myplayer.domain.music.NetLyricInfo;
import com.liuhaoyuan.myplayer.domain.music.NetMusicInfo;
import com.liuhaoyuan.myplayer.domain.music.QQSearchResult;
import com.liuhaoyuan.myplayer.aidl.Song;
import com.liuhaoyuan.myplayer.domain.music.XiaMiAlbumSongsInfo;
import com.liuhaoyuan.myplayer.domain.music.XiaMiArtistAlbumInfo;
import com.liuhaoyuan.myplayer.domain.music.XiaMiArtistInfo;
import com.liuhaoyuan.myplayer.domain.music.XiaMiRanksInfo;
import com.liuhaoyuan.myplayer.domain.music.XiaMiSongInfo;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;


public class MusicApi {
    private final String SIGN = "27ed84878a794201a8b325c301b031d4";
    private final String APPID = "22447";
    private final String[] QQ_TOPID = new String[]{"3", "5", "6", "16", "17", "18", "19", "23", "26"};
    private final String[] XIAMI_TOPID = new String[]{"101", "102", "103", "104", "1", "2", "3", "4", "5", "6"};
    private final String[] ALI_TOPID = new String[]{"real-time", "week", "year"};
    private ArrayList<DataObserver> observers = new ArrayList<>();
    private static MusicApi instance;

    private MusicApi() {

    }

    public synchronized static MusicApi getInstance() {
        if (instance == null) {
            instance = new MusicApi();
        }
        return instance;
    }

    public void registerObserver(DataObserver observer) {
        observers.add(observer);
    }

    public void unregisterObserver(DataObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(int dataType, Object data) {
        for (int i = 0; i < observers.size(); i++) {
            if (observers.get(i).getDataType() == dataType) {
                synchronized (observers.get(i)) {
                    observers.get(i).onComplete(data);
                }
            }
        }
    }

    public String[] getQQTopIds() {
        return this.QQ_TOPID;
    }

    public String[] getXiaMiTopIds() {
        return XIAMI_TOPID;
    }

    public String[] getAliTopIds() {
        return ALI_TOPID;
    }

    private void addSign(RequestParams params) {
        params.addParameter("showapi_sign", SIGN);
        params.addParameter("showapi_appid", APPID);
    }

    public void getQQRanks(String topId) {
        RequestParams params = new RequestParams("http://route.showapi.com/213-4");
        addSign(params);
        params.addParameter("topid", topId);
        x.http().get(params, new DataCallBack(DataObserver.TYPE_QQ_RANKS));
    }

    private void parseQQRanks(String result) {
        Gson gson = new Gson();
        NetMusicInfo netMusicInfo = gson.fromJson(result, NetMusicInfo.class);
        ArrayList<Song> songlist = netMusicInfo.showapi_res_body.pagebean.songlist;
        if (songlist != null) {
            notifyObservers(DataObserver.TYPE_QQ_RANKS, songlist);
        }
    }

    public void getLyric(String songId) {
        RequestParams params = new RequestParams("https://route.showapi.com/213-2");
        addSign(params);
        params.addParameter("musicid", songId);
        x.http().get(params, new DataCallBack(DataObserver.TYPE_LYRIC));
    }

    private void parseLyric(String result) {
        Gson gson = new Gson();
        NetLyricInfo netLyricInfo = gson.fromJson(result, NetLyricInfo.class);
        String lyric = netLyricInfo.showapi_res_body.lyric;
        notifyObservers(DataObserver.TYPE_LYRIC, lyric);
    }

    public void getSong(String name, String page) {
        RequestParams params = new RequestParams("http://route.showapi.com/213-1");
        addSign(params);
        params.addParameter("keyword", name);
        params.addParameter("page", page);
        x.http().get(params, new DataCallBack(DataObserver.TYPE_SONG));
    }

    private void parseSong(String result) {
        Gson gson = new Gson();
        QQSearchResult qqSearchResult = gson.fromJson(result, QQSearchResult.class);
        ArrayList<Song> list = qqSearchResult.showapi_res_body.pagebean.contentlist;
        notifyObservers(DataObserver.TYPE_SONG, list);
    }

    public void getXiaMiRanks(String xiaMiTopId, String page) {
        RequestParams params = new RequestParams("http://route.showapi.com/1143-7");
        addSign(params);
        params.addParameter("typeId", xiaMiTopId);
        params.addParameter("page", page);
        x.http().get(params, new DataCallBack(DataObserver.TYPE_XIAMI_RANKS));
    }

    private void parseXiaRanks(String result) {
        Gson gson = new Gson();
        XiaMiRanksInfo xiaMiRanksInfo = gson.fromJson(result, XiaMiRanksInfo.class);
        ArrayList<XiaMiSongInfo> list = xiaMiRanksInfo.showapi_res_body.pagebean.contentlist;
        notifyObservers(DataObserver.TYPE_XIAMI_RANKS, list);
    }

    public void getAliRanks(String aliTopId, String page) {
        RequestParams params = new RequestParams("http://route.showapi.com/1143-2");
        addSign(params);
        params.addParameter("song_type", aliTopId);
        params.addParameter("page", page);
        x.http().get(params, new DataCallBack(DataObserver.TYPE_ALI_RANKS));
    }

    private void parseAliRanks(String result) {
        Gson gson = new Gson();
        AliRanksInfo aliRanksInfo = gson.fromJson(result, AliRanksInfo.class);
        ArrayList<XiaMiSongInfo> list = aliRanksInfo.showapi_res_body.contentlist;
        notifyObservers(DataObserver.TYPE_ALI_RANKS, list);
    }

    public void getXiaMiArtist(String artistId) {
        RequestParams params = new RequestParams("http://route.showapi.com/1143-3");
        addSign(params);
        params.addParameter("artistId", artistId);
        x.http().get(params, new DataCallBack(DataObserver.TYPE_XIAMI_ARTIST));
    }

    private void parseXiaMiArtist(String result) {
        Gson gson = new Gson();
        XiaMiArtistInfo artistInfo = gson.fromJson(result, XiaMiArtistInfo.class);
        XiaMiArtistInfo.Artist artist = artistInfo.showapi_res_body.artist;
        notifyObservers(DataObserver.TYPE_XIAMI_ARTIST, artist);
    }

    public void getXiaMiArtistAlbum(String artistId, String page) {
        RequestParams params = new RequestParams("http://route.showapi.com/1143-4");
        addSign(params);
        params.addParameter("artistId", artistId);
        params.addParameter("page", page);
        x.http().get(params, new DataCallBack(DataObserver.TYPE_XIAMI_ARTIST_ALBUM));
    }

    private void parseXiaMiArtistAlbum(String result) {
        Gson gson = new Gson();
        XiaMiArtistAlbumInfo artistAlbumInfo = gson.fromJson(result, XiaMiArtistAlbumInfo.class);
        ArrayList<XiaMiArtistAlbumInfo.AlbumInfo> contentlist = artistAlbumInfo.showapi_res_body.pagebean.contentlist;
        notifyObservers(DataObserver.TYPE_XIAMI_ARTIST_ALBUM, contentlist);
    }

    public void getXiaMiArtistSongs(String artistId, String page) {
        RequestParams params = new RequestParams("http://route.showapi.com/1143-5");
        addSign(params);
        params.addParameter("artistId", artistId);
        params.addParameter("page", page);
        x.http().get(params, new DataCallBack(DataObserver.TYPE_XIAMI_ARTIST_SONGS));
    }

    private void parseXiaMiArtistSongs(String result) {
        Gson gson = new Gson();
        XiaMiRanksInfo xiaMiRanksInfo = gson.fromJson(result, XiaMiRanksInfo.class);
        ArrayList<XiaMiSongInfo> list = xiaMiRanksInfo.showapi_res_body.pagebean.contentlist;
        notifyObservers(DataObserver.TYPE_XIAMI_ARTIST_SONGS, list);
    }

    public void getXiaMiAlbumSongs(String albummId) {
        RequestParams params = new RequestParams("http://route.showapi.com/1143-6");
        addSign(params);
        params.addParameter("albumId", albummId);
        x.http().get(params, new DataCallBack(DataObserver.TYPE_XIAMI_ALBUM_SONGS));
    }

    private void parseXiaMiAlbumSongs(String result) {
        Gson gson = new Gson();
        XiaMiAlbumSongsInfo xiaMiAlbumSongsInfo = gson.fromJson(result, XiaMiAlbumSongsInfo.class);
        XiaMiAlbumSongsInfo.Body body = xiaMiAlbumSongsInfo.showapi_res_body;
        notifyObservers(DataObserver.TYPE_XIAMI_ALBUM_SONGS, body);
    }

    class DataCallBack implements Callback.CommonCallback<String> {
        private int dataType;

        public DataCallBack(int dataType) {
            this.dataType = dataType;
        }

        @Override
        public void onSuccess(String result) {
            switch (dataType) {
                case DataObserver.TYPE_QQ_RANKS:
                    parseQQRanks(result);
                    break;
                case DataObserver.TYPE_SONG:
                    parseSong(result);
                    break;
                case DataObserver.TYPE_LYRIC:
                    parseLyric(result);
                    break;
                case DataObserver.TYPE_XIAMI_RANKS:
                    parseXiaRanks(result);
                    break;
                case DataObserver.TYPE_ALI_RANKS:
                    parseAliRanks(result);
                    break;
                case DataObserver.TYPE_XIAMI_ARTIST:
                    parseXiaMiArtist(result);
                    break;
                case DataObserver.TYPE_XIAMI_ARTIST_ALBUM:
                    parseXiaMiArtistAlbum(result);
                    break;
                case DataObserver.TYPE_XIAMI_ARTIST_SONGS:
                    parseXiaMiArtistSongs(result);
                    break;
                case DataObserver.TYPE_XIAMI_ALBUM_SONGS:
                    parseXiaMiAlbumSongs(result);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onError(Throwable ex, boolean isOnCallback) {
            notifyObservers(dataType, null);
            Log.e("api", "error......");
        }

        @Override
        public void onCancelled(CancelledException cex) {

        }

        @Override
        public void onFinished() {

        }
    }
}
