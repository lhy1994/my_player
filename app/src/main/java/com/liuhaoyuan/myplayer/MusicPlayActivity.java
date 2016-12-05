package com.liuhaoyuan.myplayer;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.liuhaoyuan.myplayer.aidl.IMusicPlayService;
import com.liuhaoyuan.myplayer.domain.NetLyricInfo;
import com.liuhaoyuan.myplayer.domain.Song;
import com.liuhaoyuan.myplayer.service.MusicPlayService;
import com.liuhaoyuan.myplayer.utils.LyricUtils;
import com.liuhaoyuan.myplayer.utils.TimeFormatUtils;
import com.liuhaoyuan.myplayer.view.LyicTextView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by liuhaoyuan on 2016/7/24.
 */
public class MusicPlayActivity extends AppCompatActivity {

    private ImageButton back;
    private TextView title;
    private TextView artist;
    private SeekBar volume;
    private TextView startTime;
    private TextView duration;
    private SeekBar progress;
    private ImageButton mode;
    private ImageButton previous;
    private ImageButton play;
    private ImageButton next;
    private ImageButton heart;
    private MusicListener mListener;
    private int currentPosition;
    private AudioManager audioManager;
    private int currentVolume;
    private int maxVolume;
    private boolean isPlaying = true;
    private int playMode;
    private boolean isDestoryed;

    private final String SECRET = "27ed84878a794201a8b325c301b031d4";
    private final String APPID = "22447";

    private static final int UPDATE = 1;
    private static final int SHOW_LYRICS = 2;
    private static final int INIT_SONGLIST_FINISH = 3;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE:
                    if (musicService != null) {
                        try {
                            if (isPlaying && !isDestoryed) {
                                startTime.setText(TimeFormatUtils.timeFormat(musicService.getCurrentProgress()));
                                progress.setProgress(musicService.getCurrentProgress());
                            }

                            mHandler.sendEmptyMessageDelayed(UPDATE, 1000);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case SHOW_LYRICS:
                    try {

                        if (isPlaying && !isDestoryed) {
                            int progress = musicService.getCurrentProgress();
                            lyicTextView.showLyric(progress);
                        }
                        mHandler.removeMessages(SHOW_LYRICS);
                        mHandler.sendEmptyMessage(SHOW_LYRICS);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case INIT_SONGLIST_FINISH:
//                    initLyric();
                    bindMusicService();
                    break;
                default:
                    break;
            }
        }
    };


    private PreparedReceiver mReceiver;
    private boolean isFromNotification;
    private LyicTextView lyicTextView;

    private boolean isRemotePlay = false;
    private ArrayList<Song> songList;
    private ImageView musicBackground;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);

        isDestoryed = false;
        initView();
        initData();
    }


    private void initView() {
        back = (ImageButton) findViewById(R.id.btn_music_back);
        title = (TextView) findViewById(R.id.tv_music_play_title);
        artist = (TextView) findViewById(R.id.tv_music_play_artist);
        volume = (SeekBar) findViewById(R.id.sb_music_play_volume);
        startTime = (TextView) findViewById(R.id.tv_music_play_start_time);
        duration = (TextView) findViewById(R.id.tv_music_play_duration);
        progress = (SeekBar) findViewById(R.id.sb_music_play);
        mode = (ImageButton) findViewById(R.id.btn_music_play_mode);
        previous = (ImageButton) findViewById(R.id.btn_music_play_pre);
        play = (ImageButton) findViewById(R.id.btn_music_play_pause);
        next = (ImageButton) findViewById(R.id.btn_music_play_next);
        heart = (ImageButton) findViewById(R.id.btn_music_play_heart);
        lyicTextView = (LyicTextView) findViewById(R.id.lyric);
        musicBackground = (ImageView) findViewById(R.id.iv_background);

    }


    private void initData() {
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume.setMax(maxVolume);
        volume.setProgress(currentVolume);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("prepared");
        mReceiver = new PreparedReceiver();
        registerReceiver(mReceiver, intentFilter);

        initSongList();
    }

    private void initSongList() {
        Intent intent = getIntent();
        isFromNotification = intent.getBooleanExtra("from_notifation", false);
        if (!isFromNotification) {
            currentPosition = intent.getIntExtra("position", 0);
        }

        isRemotePlay = intent.getBooleanExtra("is_remote_play", false);
        if (isRemotePlay) {
            Bundle bundle = intent.getExtras();
            songList = (ArrayList<Song>) bundle.getSerializable("song_list");
            mHandler.sendEmptyMessage(INIT_SONGLIST_FINISH);
        } else {
            getLocalMusicList();
        }
    }

    private void getLocalMusicList() {
        songList = new ArrayList<>();
        Thread thread = new Thread() {
            @Override
            public void run() {
                ContentResolver resolver = getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] projecttion = new String[]{
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.ARTIST
                };
                Cursor cursor = resolver.query(uri, projecttion, null, null, null);
                while (cursor.moveToNext()) {
                    Song song = new Song();
                    song.songname = cursor.getString(0);
                    song.url = cursor.getString(1);
                    song.singername = cursor.getString(2);
                    songList.add(song);
                }
                mHandler.sendEmptyMessage(INIT_SONGLIST_FINISH);
            }
        };
        thread.start();
    }

    class PreparedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            initListener();
            getInfo();
            initLyric();
        }
    }


    private void bindMusicService() {
        Intent intent = new Intent();
        intent.setAction("com.liuhaoyuan.myplayer.BIND_MUSIC_SERVICE");
        intent.setPackage(getPackageName());

        intent.putExtra("is_remote_play", isRemotePlay);

        Bundle bundle = new Bundle();
        bundle.putSerializable("song_list", songList);
        intent.putExtras(bundle);

        startService(intent);
        bindService(intent, mConn, Context.BIND_AUTO_CREATE);
    }

    private IMusicPlayService musicService;
    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            musicService = IMusicPlayService.Stub.asInterface(iBinder);
            if (musicService != null) {
                try {
                    if ((!isFromNotification) && ((TextUtils.isEmpty(musicService.getUrl())) || (currentPosition != musicService.getCurrentPosition() || !songList.get(currentPosition).url.equals(musicService.getUrl())))) {
                        musicService.openAudio(currentPosition);
                    } else {
                        Intent intent = new Intent();
                        intent.setAction("prepared");
                        sendBroadcast(intent);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicService = null;
        }
    };

    private void initListener() {
        mListener = new MusicListener();
        back.setOnClickListener(mListener);
        play.setOnClickListener(mListener);
        previous.setOnClickListener(mListener);
        next.setOnClickListener(mListener);
        mode.setOnClickListener(mListener);

        volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                currentVolume = i;
                seekBar.setProgress(i);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    seekBar.setProgress(i);
                    try {
                        musicService.seekTo(i);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                int progress = seekBar.getProgress();
            }
        });
    }

    private void getInfo() {
        try {
            currentPosition = musicService.getCurrentPosition();
            title.setText(musicService.getTitle());
            artist.setText(musicService.getArtist());
            if (musicService.isPlaying()) {
                play.setBackgroundResource(R.drawable.pause);
            } else {
                play.setBackgroundResource(R.drawable.play);
            }

            playMode = musicService.getPlayMode();
            if (playMode == MusicPlayService.REPEAT_MODE_NORMAL) {
                mode.setBackgroundResource(R.drawable.mode_normal);
            } else if (playMode == MusicPlayService.REPEAT_MODE_RANDOM) {
                mode.setBackgroundResource(R.drawable.mode_random);
            } else if (playMode == MusicPlayService.REPEAT_MODE_ALL) {
                mode.setBackgroundResource(R.drawable.mode_all);
            } else if (playMode == MusicPlayService.REPEAT_MODE_SINGLE) {
                mode.setBackgroundResource(R.drawable.mode_single);
            }

            int dur = musicService.getDuration();
            this.duration.setText(TimeFormatUtils.timeFormat((long) dur));
            progress.setMax(dur);

            mHandler.sendEmptyMessageDelayed(UPDATE, 1000);

            mHandler.sendEmptyMessage(SHOW_LYRICS);

            String imageUrl = songList.get(currentPosition).albumpic_big;
            if (!TextUtils.isEmpty(imageUrl)) {
                x.image().bind(musicBackground, imageUrl);
            } else {
                musicBackground.setImageResource(R.drawable.skin_bg3);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void initLyric() {
        if (songList != null && isRemotePlay) {
            String musicid = songList.get(currentPosition).songid;
            RequestParams requestParams = new RequestParams("https://route.showapi.com/213-2?" + "&showapi_sign=" + SECRET + "&showapi_appid=" + APPID + "&musicid=" + musicid);
            Log.e("test", "https://route.showapi.com/213-2?" + "&showapi_sign=" + SECRET + "&showapi_appid=" + APPID + "&musicid=" + musicid);
            x.http().get(requestParams, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    parseLyric(result);
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Log.e("test", "no lyric find");
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
        }
    }

    private void parseLyric(String result) {
        Gson gson = new Gson();
        NetLyricInfo netLyricInfo = gson.fromJson(result, NetLyricInfo.class);
        String lyric = netLyricInfo.showapi_res_body.lyric;

        LyricUtils lyricUtils = new LyricUtils();
        lyricUtils.readLyricString(lyric);
        lyicTextView.setLyrics(lyricUtils.getLyrics());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        mReceiver = null;
        unbindService(mConn);
        isDestoryed = true;
    }

    class MusicListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_music_back:
                    finish();
                    break;
                case R.id.btn_music_play_pause:
                    try {
                        if (musicService.isPlaying()) {
                            isPlaying = false;
                            play.setBackgroundResource(R.drawable.play);
                            musicService.pause();
                        } else {
                            isPlaying = true;
                            play.setBackgroundResource(R.drawable.pause);
                            musicService.play();
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.btn_music_play_mode:
                    try {
                        if (playMode == MusicPlayService.REPEAT_MODE_NORMAL) {
                            playMode = MusicPlayService.REPEAT_MODE_SINGLE;
                            musicService.setPlayMode(MusicPlayService.REPEAT_MODE_SINGLE);
                            mode.setBackgroundResource(R.drawable.mode_single);
                        } else if (playMode == MusicPlayService.REPEAT_MODE_SINGLE) {
                            playMode = MusicPlayService.REPEAT_MODE_ALL;
                            musicService.setPlayMode(MusicPlayService.REPEAT_MODE_ALL);
                            mode.setBackgroundResource(R.drawable.mode_all);
                        } else if (playMode == MusicPlayService.REPEAT_MODE_ALL) {
                            playMode = MusicPlayService.REPEAT_MODE_RANDOM;
                            musicService.setPlayMode(MusicPlayService.REPEAT_MODE_RANDOM);
                            mode.setBackgroundResource(R.drawable.mode_random);
                        } else if (playMode == MusicPlayService.REPEAT_MODE_RANDOM) {
                            playMode = MusicPlayService.REPEAT_MODE_NORMAL;
                            musicService.setPlayMode(MusicPlayService.REPEAT_MODE_NORMAL);
                            mode.setBackgroundResource(R.drawable.mode_normal);
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.btn_music_play_pre:
                    play.setBackgroundResource(R.drawable.selecter_music_pause);
                    try {
                        musicService.previous();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.btn_music_play_next:
                    play.setBackgroundResource(R.drawable.selecter_music_pause);
                    try {
                        musicService.next();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }


}
