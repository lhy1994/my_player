package com.liuhaoyuan.myplayer.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.liuhaoyuan.myplayer.APP;
import com.liuhaoyuan.myplayer.activity.MusicPlayActivity;
import com.liuhaoyuan.myplayer.R;
import com.liuhaoyuan.myplayer.aidl.IMusicPlayService;
import com.liuhaoyuan.myplayer.aidl.Song;
import com.liuhaoyuan.myplayer.utils.ConstantValues;
import com.liuhaoyuan.myplayer.utils.MusicUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by liuhaoyuan on 2016/7/24.
 */
public class MusicPlayService extends Service {

    public static final int REPEAT_MODE_NORMAL = 1;
    public static final int REPEAT_MODE_SINGLE = 2;
    public static final int REPEAT_MODE_RANDOM = 3;
    public static final int REPEAT_MODE_ALL = 4;

    private int mode = REPEAT_MODE_NORMAL;
    private boolean isCompletion = false;

    private boolean isRemotePlay;
    private ArrayList<Song> songList;
    private int songListSize;
    private Song currentSong;
    private int currentPosition;

    private MediaPlayer mediaPlayer;
    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            isCompletion = false;
            play();
            Intent intent = new Intent();
            intent.setAction("prepared");
            sendBroadcast(intent);
        }
    };
    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            isCompletion = true;
            next();
        }
    };
    private MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
            Toast.makeText(MusicPlayService.this, "音乐播放出错", Toast.LENGTH_SHORT).show();
            return false;
        }
    };
    private SharedPreferences preferences;
    private boolean mPlaylistChanged;

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = getSharedPreferences(ConstantValues.MUSIC_CONFIG, MODE_PRIVATE);
        mode = preferences.getInt(ConstantValues.MUSIC_MODE, REPEAT_MODE_NORMAL);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        isRemotePlay = intent.getBooleanExtra("is_remote_play", false);

        mPlaylistChanged = intent.getBooleanExtra(ConstantValues.PLAYLIST_CHANGED, true);
        if (mPlaylistChanged){
            Bundle bundle = intent.getExtras();
            songList = (ArrayList<Song>) bundle.getSerializable(ConstantValues.PLAYLIST);
            if (songList != null) {
                songListSize = songList.size();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
//        isRemotePlay = intent.getBooleanExtra("is_remote_play", false);
//
//        Bundle bundle = intent.getExtras();
//        songList = (ArrayList<Song>) bundle.getSerializable("song_list");
//        if (songList != null) {
//            songListSize = songList.size();
//        }
        return iBinder;
    }

    private IMusicPlayService.Stub iBinder = new IMusicPlayService.Stub() {
        MusicPlayService service = MusicPlayService.this;

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void openAudio(int position) throws RemoteException {
            service.openAudio(position);
        }

        @Override
        public void play() throws RemoteException {
            service.play();
        }

        @Override
        public void pause() throws RemoteException {
            service.pause();
        }

        @Override
        public void seekTo(int position) throws RemoteException {
            service.seekTo(position);
        }

        @Override
        public void setPlayMode(int mode) throws RemoteException {
            service.setMode(mode);
        }

        @Override
        public int getPlayMode() throws RemoteException {
            return service.getMode();
        }

        @Override
        public void previous() throws RemoteException {
            service.previous();
        }

        @Override
        public void next() throws RemoteException {
            service.next();
        }

        @Override
        public String getTitle() throws RemoteException {
            return service.getTitle();
        }

        @Override
        public String getArtist() throws RemoteException {
            return service.getArtist();
        }

        @Override
        public String getUrl() throws RemoteException {
            return service.getUrl();
        }

        @Override
        public int getDuration() throws RemoteException {
            return service.getDuration();
        }


        @Override
        public int getCurrentPosition() throws RemoteException {
            return service.getCurrentPosition();
        }

        @Override
        public int getCurrentProgress() throws RemoteException {
            return service.getCurrentProgress();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return service.isplaying();
        }

        @Override
        public List<Song> getSongList() throws RemoteException {
            return songList;
        }

    };

    private void openAudio(int position) {
        currentPosition = position;
        currentSong = songList.get(currentPosition);

        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer = null;
        }

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(mOnPreparedListener);
        mediaPlayer.setOnCompletionListener(mOnCompletionListener);
        mediaPlayer.setOnErrorListener(mOnErrorListener);

        try {
            mediaPlayer.setDataSource(this, Uri.parse(currentSong.url));
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void play() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
        MusicUtils.HAS_PLAYLIST=true;

        Intent intent = new Intent(this, MusicPlayActivity.class);
        intent.putExtra(ConstantValues.PLAYLIST_CHANGED,false);
//        intent.putExtra("from_notifation", true);
//        intent.putExtra("is_remote_play", isRemotePlay);

//        Bundle bundle = new Bundle();
//        bundle.putSerializable("song_list", songList);
//        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification.Builder builder = new Notification.Builder(this).setContentText("正在播放 " + getTitle());
        builder.setContentTitle("Myplayer");
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.login);
        builder.setWhen(System.currentTimeMillis());
        builder.setOngoing(true);
        Notification notification = builder.getNotification();

        startForeground(1, notification);
    }

    private void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
        stopForeground(true);
    }

    private void seekTo(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
    }

    private void setMode(int mode) {
        this.mode = mode;
        preferences.edit().putInt(ConstantValues.MUSIC_MODE, mode).apply();
    }

    private int getMode() {
        return mode;
    }

    private void previous() {
        setPreviousPosition();
        openAudio(currentPosition);
    }

    private void setPreviousPosition() {
        if (mode == REPEAT_MODE_NORMAL) {
            currentPosition--;
            if (currentPosition < 0) {
                currentPosition = 0;
            }
        } else if (mode == REPEAT_MODE_SINGLE) {
            if (!isCompletion) {
                currentPosition--;
                if (currentPosition < 0) {
                    currentPosition = 0;
                }
            }
        } else if (mode == REPEAT_MODE_RANDOM) {
            int max = songListSize - 1;
            int min = 0;
            Random random = new Random();
            currentPosition = random.nextInt(max) % (max - min + 1) + min;
        } else if (mode == REPEAT_MODE_ALL) {
            currentPosition--;
            if (currentPosition < 0) {
                currentPosition = songListSize - 1;
            }
        }
    }

    private void next() {
        setNextPosition();
        if ((mode != REPEAT_MODE_NORMAL) || (mode == REPEAT_MODE_NORMAL && currentPosition != songListSize - 1) || (mode == REPEAT_MODE_NORMAL && currentPosition == songListSize - 1 && !isCompletion)) {
            openAudio(currentPosition);
        }
    }

    private void setNextPosition() {
        if (mode == REPEAT_MODE_NORMAL) {
            currentPosition++;
            if (currentPosition >= songListSize) {
                currentPosition = songListSize - 1;
            }
        } else if (mode == REPEAT_MODE_ALL) {
            currentPosition++;
            if (currentPosition >= songListSize) {
                currentPosition = 0;
            }
        } else if (mode == REPEAT_MODE_RANDOM) {
            int max = songListSize - 1;
            int min = 0;
            Random random = new Random();
            currentPosition = random.nextInt(max) % (max - min + 1) + min;
        } else if (mode == REPEAT_MODE_SINGLE) {
            if (!isCompletion) {
                currentPosition++;
                if (currentPosition >= songListSize) {
                    currentPosition = songListSize - 1;
                }
            }
        }
    }

    private String getUrl() {
        if (currentSong != null) {
            return currentSong.url;
        } else {
            return "";
        }
    }

    private String getTitle() {
//        if (isRemotePlay){
//            return songList.get(currentPosition).songname;
//        }else {
//            return currentLocalMusic.getTitle();
//        }
        return currentSong.songname;
    }

    private String getArtist() {
//        if (isRemotePlay){
//            return songList.get(currentPosition).singername;
//        }else {
//            return currentLocalMusic.getArtitst();
//        }
        return currentSong.singername;
    }

    private int getDuration() {

        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    private int getCurrentPosition() {
        return currentPosition;
    }

    private int getCurrentProgress() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    private boolean isplaying() {
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        }
        return false;
    }
}
