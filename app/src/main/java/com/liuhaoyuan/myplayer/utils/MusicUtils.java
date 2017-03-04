package com.liuhaoyuan.myplayer.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.liuhaoyuan.myplayer.activity.MusicPlayActivity;
import com.liuhaoyuan.myplayer.aidl.Song;

import java.util.ArrayList;

/**
 * Created by hyliu on 2017/1/17.
 */

public class MusicUtils {
    public static boolean HAS_PLAYLIST=false;
    public static void playMusic(Context context,int position, boolean playlistChanged, ArrayList<Song> songList){
        Intent intent = new Intent(context, MusicPlayActivity.class);
        intent.putExtra(ConstantValues.PLAYLIST_CHANGED, playlistChanged);

        if (playlistChanged){
            intent.putExtra(ConstantValues.MUSIC_CURRENT_POSITION, position);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ConstantValues.PLAYLIST, songList);
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }
}
