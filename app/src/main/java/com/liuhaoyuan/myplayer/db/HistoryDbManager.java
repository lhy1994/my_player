package com.liuhaoyuan.myplayer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.liuhaoyuan.myplayer.aidl.Song;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by liuhaoyuan on 17/4/8.
 */

public class HistoryDbManager {
    private static HistoryDbManager instance;
    private HistoryOpenHelper mHelper;
    private static final String MUSIC_HISTORY_TABLE_NAME="music_history";
    private static final String MUSIC_HISTORY_SONG_ID="song_id";
    private static final String MUSIC_HISTORY_SONG_INFO="song_info";
    private static final String MUSIC_HISTORY_SONG_TIME="history_time";
    private static final String MUSIC_HISTORY_SONG_URL="song_url";
    private static final String MUSIC_HISTORY_COUNT="count";
    public synchronized static HistoryDbManager getInstance(Context context){
        if (instance==null){
            instance=new HistoryDbManager(context);
        }
        return instance;
    }
    private HistoryDbManager(Context context){
        this.mHelper=new HistoryOpenHelper(context);
    };

    public ArrayList<Song> getAllMusicHistory(){
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ArrayList<Song> list=null;
        Cursor cursor = db.query(true, MUSIC_HISTORY_TABLE_NAME, null, null, null, null, null, MUSIC_HISTORY_SONG_TIME, null);
        if (cursor!=null){
            list=new ArrayList<>();
            while (cursor.moveToNext()){
                byte[] bytes = cursor.getBlob(cursor.getColumnIndex(MUSIC_HISTORY_SONG_INFO));
                Song song=Converter.bytesToSong(bytes);
                if (song!=null){
                    list.add(song);
                }
            }
            cursor.close();
        }
        db.close();
        return list;
    }
    public ArrayList<Song> getAllMusicHistoryByCount(){
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ArrayList<Song> list=null;
        Cursor cursor = db.query(true, MUSIC_HISTORY_TABLE_NAME, null, null, null, null, null, MUSIC_HISTORY_COUNT, null);
        if (cursor!=null){
            list=new ArrayList<>();
            while (cursor.moveToNext()){
                byte[] bytes = cursor.getBlob(cursor.getColumnIndex(MUSIC_HISTORY_SONG_INFO));
                Song song=Converter.bytesToSong(bytes);
                if (song!=null){
                    list.add(song);
                }
            }
            cursor.close();
        }
        db.close();
        return list;
    }

    public void addMusicHistory(Song song){
        SQLiteDatabase db = mHelper.getWritableDatabase();
        byte[] bytes = Converter.songToBytes(song);
        if (bytes==null){
            return;
        }
        Cursor cursor = db.query(MUSIC_HISTORY_TABLE_NAME, null, MUSIC_HISTORY_SONG_URL + "=?", new String[]{song.url}, null, null, null);
        int count=0;
        if (cursor!=null){
            if (cursor.moveToNext()){
                count=cursor.getInt(cursor.getColumnIndex(MUSIC_HISTORY_COUNT));
            }
            cursor.close();
        }
        count++;
        ContentValues values=new ContentValues();
        values.put(MUSIC_HISTORY_SONG_URL,song.url);
        values.put(MUSIC_HISTORY_SONG_ID,song.songid);
        values.put(MUSIC_HISTORY_SONG_INFO,bytes);
        values.put(MUSIC_HISTORY_SONG_TIME, System.currentTimeMillis());
        values.put(MUSIC_HISTORY_COUNT,count);
        db.replace(MUSIC_HISTORY_TABLE_NAME,null,values);
        db.close();
    }

    public void deleteMusicHistory(String url){
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(MUSIC_HISTORY_TABLE_NAME,MUSIC_HISTORY_SONG_URL+"=?",new String[]{url} );
        db.close();
    }
}
