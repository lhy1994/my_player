package com.liuhaoyuan.myplayer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.liuhaoyuan.myplayer.aidl.Song;
import com.liuhaoyuan.myplayer.domain.music.Singer;
import com.liuhaoyuan.myplayer.domain.video.YouKuShowDetail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by hyliu on 2017/1/31.
 */

public class FavoriteDbManager {
    private FavoriteOpenHelper mHelper;
    private static FavoriteDbManager instance;
    private final String SONG_TABLE_NAME = "favorite_song";
    private final String SINGER_TABLE_NAME = "favorite_singer";
    private final String VIDEO_TABLE_NAME = "favorite_video";
    private final String SONG_ID = "song_id";
    private final String SONG_URL = "song_url";
    private final String SONG_INFO = "song_info";
    private final String SINGER_ID = "singer_id";
    private final String SINGER_NAME = "singer_name";
    private final String SINGER_PIC = "singer_pic";
    private final String VIDEO_ID = "video_id";
    private final String VIDEO_INFO = "video_info";

    private FavoriteDbManager(Context context) {
        mHelper = new FavoriteOpenHelper(context);
    }

    public static synchronized FavoriteDbManager getInstance(Context context) {
        if (instance == null) {
            instance = new FavoriteDbManager(context);
        }
        return instance;
    }

    public void insertSong(Song song) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        byte[] bytes = Converter.songToBytes(song);
        if (bytes==null){
            return;
        }
        ContentValues values = new ContentValues();
        values.put(SONG_URL,song.url);
        values.put(SONG_ID,song.songid);
        values.put(SONG_INFO,bytes);
        database.replace(SONG_TABLE_NAME,null,values);
        database.close();
    }

    public void deleteSong(String url) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        database.delete(SONG_TABLE_NAME, SONG_URL + "=?", new String[]{url});
        database.close();
    }

    public ArrayList<Song> queryAllSong() {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        Cursor cursor = database.query(SONG_TABLE_NAME, null, null, null, null, null, null);
        ArrayList<Song> list = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                byte[] bytes = cursor.getBlob(cursor.getColumnIndex(SONG_INFO));
                Song song = Converter.bytesToSong(bytes);
                list.add(song);
            }
            cursor.close();
        }
        database.close();
        return list;
    }

    public Song querySongByUrl(String url){
        SQLiteDatabase database = mHelper.getWritableDatabase();
        Cursor cursor = database.query(SONG_TABLE_NAME, null, SONG_URL + "=?", new String[]{url}, null, null, null);
        Song song=null;
        if (cursor!=null){
            if (cursor.getColumnCount()>0 && cursor.moveToFirst()){
                byte[] bytes = cursor.getBlob(cursor.getColumnIndex(SONG_INFO));
                song = Converter.bytesToSong(bytes);
            }
            cursor.close();
        }
        database.close();
        return song;
    }

    public boolean songFavorited(String url){
        SQLiteDatabase database = mHelper.getWritableDatabase();
        Cursor cursor = database.query(SONG_TABLE_NAME, null, SONG_URL + "=?", new String[]{url}, null, null, null);
        boolean favorited=false;
        if (cursor!=null){
            if (cursor.getCount()>0 ){
                favorited=true;
            }
            cursor.close();
        }
        database.close();
        return favorited;
    }

    public void insertSinger(String singerID, String singerName, String singerPic) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SINGER_ID, singerID);
        values.put(SINGER_NAME, singerName);
        values.put(SINGER_PIC, singerPic);
        Cursor cursor = database.query(SINGER_TABLE_NAME, null, SINGER_ID + "=?", new String[]{singerID}, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            database.update(SINGER_TABLE_NAME, values, SINGER_ID + "=?", new String[]{singerID});
            cursor.close();
        } else {
            database.insert(SINGER_TABLE_NAME, null, values);
        }
        database.close();
    }

    public void deleteSinger(String SINGER_ID) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        database.delete(SINGER_TABLE_NAME, SINGER_ID + "=?", new String[]{SINGER_ID});
        database.close();
    }

    public ArrayList<Singer> queryAllSinger() {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        Cursor cursor = database.query(SINGER_TABLE_NAME, null, null, null, null, null, null);
        ArrayList<Singer> list = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Singer singer = new Singer();
                singer.singerId = cursor.getString(cursor.getColumnIndex(SINGER_ID));
                singer.singerName = cursor.getString(cursor.getColumnIndex(SINGER_NAME));
                singer.singerPic = cursor.getString(cursor.getColumnIndex(SINGER_PIC));
                list.add(singer);
            }
            cursor.close();
        }
        database.close();
        return list;
    }

    public void deleteVideo(String videoId) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        database.delete(VIDEO_TABLE_NAME, VIDEO_ID + "=?", new String[]{videoId});
        database.close();
    }

    public ArrayList<YouKuShowDetail> queryAllVideo() {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        Cursor cursor = database.query(VIDEO_TABLE_NAME, null, null, null, null, null, null);
        ArrayList<YouKuShowDetail> list = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                byte[] bytes = cursor.getBlob(cursor.getColumnIndex(VIDEO_INFO));
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                ObjectInputStream objectInputStream = null;
                try {
                    objectInputStream = new ObjectInputStream(byteArrayInputStream);
                    YouKuShowDetail videoItem = (YouKuShowDetail) objectInputStream.readObject();
                    list.add(videoItem);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    if (objectInputStream!=null){
                        try {
                            objectInputStream.close();
                            byteArrayInputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            cursor.close();
        }
        database.close();
        return list;
    }

    public void insertVideo(YouKuShowDetail videoItem) {
        byte[] bytes = videoInfoToByteArray(videoItem);
        if (bytes == null) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(VIDEO_ID, videoItem.id);
        values.put(VIDEO_INFO, bytes);

        SQLiteDatabase database = mHelper.getWritableDatabase();
        Cursor cursor = database.query(VIDEO_TABLE_NAME, null, VIDEO_ID + "=?", new String[]{videoItem.id}, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            database.update(VIDEO_TABLE_NAME, values, VIDEO_ID + "=?", new String[]{videoItem.id});
            cursor.close();
        } else {
            database.insert(VIDEO_TABLE_NAME, null, values);
        }
        database.close();
    }

    private byte[] videoInfoToByteArray(YouKuShowDetail videoItem) {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(arrayOutputStream);
            objectOutputStream.writeObject(videoItem);
            objectOutputStream.flush();
            byte[] bytes = arrayOutputStream.toByteArray();
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                    arrayOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
