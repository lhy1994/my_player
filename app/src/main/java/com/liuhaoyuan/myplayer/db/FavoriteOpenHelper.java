package com.liuhaoyuan.myplayer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hyliu on 2017/1/31.
 */

public class FavoriteOpenHelper extends SQLiteOpenHelper {
    public FavoriteOpenHelper(Context context) {
        super(context, "favorite.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table favorite_song(song_url varchar(50) primary key,song_id varchar(50),song_info text)");
        db.execSQL("create table favorite_singer(_id integer primary key autoincrement,singer_id varchar(50),singer_name varchar(50),singer_pic varchar(50))");
        db.execSQL("create table favorite_video(_id integer primary key autoincrement,video_id varchar(50),video_info text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
