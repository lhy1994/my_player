package com.liuhaoyuan.myplayer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by liuhaoyuan on 17/4/8.
 */

public class HistoryOpenHelper extends SQLiteOpenHelper {
    public HistoryOpenHelper(Context context) {
        super(context, "history.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table music_history(song_url varchar(20) primary key,song_id varchar(20) ,song_info text,history_time integer,count integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
