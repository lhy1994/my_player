package com.liuhaoyuan.myplayer.activity;

import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.AutoTransition;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.liuhaoyuan.myplayer.APP;
import com.liuhaoyuan.myplayer.R;
import com.liuhaoyuan.myplayer.aidl.IMusicPlayService;
import com.liuhaoyuan.myplayer.api.DataObserver;
import com.liuhaoyuan.myplayer.api.MusicApi;
import com.liuhaoyuan.myplayer.aidl.Song;
import com.liuhaoyuan.myplayer.db.FavoriteDbManager;
import com.liuhaoyuan.myplayer.domain.music.Lyric;
import com.liuhaoyuan.myplayer.manager.ThreadPoolManger;
import com.liuhaoyuan.myplayer.service.MusicPlayService;
import com.liuhaoyuan.myplayer.utils.ConstantValues;
import com.liuhaoyuan.myplayer.utils.ImageUtils;
import com.liuhaoyuan.myplayer.utils.LogUtils;
import com.liuhaoyuan.myplayer.utils.LyricUtils;
import com.liuhaoyuan.myplayer.utils.TimeFormatUtils;
import com.liuhaoyuan.myplayer.view.CircleImageView;
import com.liuhaoyuan.myplayer.view.CircularSeekBar;
import com.liuhaoyuan.myplayer.view.LyricTextView;
import com.liuhaoyuan.myplayer.view.TimelyView;

import org.xutils.common.Callback;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuhaoyuan on 2016/7/24.
 */
public class MusicPlayActivity2 extends AppCompatActivity {

    private TextView mSongNameTv;
    private TextView mSingerNameTv;
    private ImageView mPlayModeBtn;
    private ImageView mPreviousBtn;
    private FloatingActionButton mMusicPlayBtn;
    private ImageView mNextBtn;
    private MusicListener mListener;
    private int mCurrentPosition;
    private int mCurrentProgress;
    private AudioManager audioManager;
    private int currentVolume;
    private int maxVolume;
    private boolean isPlaying = true;
    private int playMode;
    private boolean isDestoryed;
    private boolean mFavorited = false;

    private PreparedReceiver mReceiver;
    private LyricTextView mLyricTv;

    private ArrayList<Song> mSongList;
    private ImageView mAlbumArtBlurred;
    private boolean mPlaylisChanged;
    private TimelyView timelyView11;
    private TimelyView timelyView12;
    private TimelyView timelyView13;
    private TimelyView timelyView14;
    private TimelyView timelyView15;
    private CircularSeekBar mSeekBar;
    private ImageView mFavoritBtn;
    private CircleImageView mAlbumArt;
    private TextView hourColon;
    int[] timeArr = new int[]{0, 0, 0, 0, 0};
    Handler mElapsedTimeHandler;
    Handler mProgressHandler;
    Handler mLyricHandler;
    private ArrayList<Lyric> mLyrics;
    private String mCurrentUrl;

    private static final int UPDATE = 1;
    private static final int SHOW_LYRICS = 2;
    private static final int INIT_SONGLIST_FINISH = 3;
    private static final int UPDATE_FAVORITED = 4;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case INIT_SONGLIST_FINISH:
//                    initLyric();
                    bindMusicService();
                    break;
                case UPDATE_FAVORITED:
                    if (mFavorited) {
                        mFavoritBtn.setImageResource(R.drawable.ic_favorite_red_400_36dp);
                    } else {
                        mFavoritBtn.setImageResource(R.drawable.ic_favorite_border_white_36dp);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private Runnable mUpdateLyric = new Runnable() {
        @Override
        public void run() {
            if (musicService != null) {
                try {
                    int progress = musicService.getCurrentProgress();
                    mLyricTv.showLyric(progress);
                    if (isPlaying && !isDestoryed) {
                        mLyricHandler.removeCallbacks(this);
                        mLyricHandler.post(this);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private Runnable mUpdateProgress = new Runnable() {
        @Override
        public void run() {
            if (musicService != null) {
                try {
                    mSeekBar.setProgress(musicService.getCurrentProgress());
                    if (isPlaying && !isDestoryed) {
                        mProgressHandler.postDelayed(this, 1000);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private Runnable mUpdateElapsedTime = new Runnable() {
        @Override
        public void run() {
            if (musicService != null) {
                try {
                    mCurrentProgress = musicService.getCurrentProgress();
                    String time = TimeFormatUtils.makeShortTimeString(MusicPlayActivity2.this, mCurrentProgress / 1000);
                    if (time.length() < 5) {
                        timelyView11.setVisibility(View.GONE);
                        timelyView12.setVisibility(View.GONE);
                        hourColon.setVisibility(View.GONE);
                        tv13(time.charAt(0) - '0');
                        tv14(time.charAt(2) - '0');
                        tv15(time.charAt(3) - '0');
                    } else if (time.length() == 5) {
                        timelyView12.setVisibility(View.VISIBLE);
                        tv12(time.charAt(0) - '0');
                        tv13(time.charAt(1) - '0');
                        tv14(time.charAt(3) - '0');
                        tv15(time.charAt(4) - '0');
                    } else {
                        timelyView11.setVisibility(View.VISIBLE);
                        hourColon.setVisibility(View.VISIBLE);
                        tv11(time.charAt(0) - '0');
                        tv12(time.charAt(2) - '0');
                        tv13(time.charAt(3) - '0');
                        tv14(time.charAt(5) - '0');
                        tv15(time.charAt(6) - '0');
                    }
                    if (isPlaying && !isDestoryed) {
                        mElapsedTimeHandler.postDelayed(this, 1000);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Full Screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        APP application = (APP) getApplication();
        int currentTheme = application.getCurrentTheme();
        setTheme(currentTheme);

        // 允许使用transitions
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
// 设置一个exit transition
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(new AutoTransition());//new Slide()  new Fade()
            getWindow().setEnterTransition(new AutoTransition());
            getWindow().setSharedElementEnterTransition(new AutoTransition());
            getWindow().setSharedElementExitTransition(new AutoTransition());
        }

        setContentView(R.layout.activity_music_play2);
        isDestoryed = false;
        init();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        init();
//        initData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_music_play, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putExtra(ConstantValues.FRAGMENT_ID, R.id.nav_music);
            startActivity(intent);
        } else if (itemId == R.id.action_playlist) {
            Intent intent = new Intent(this, PlaylistActivity.class);
            intent.putExtra(ConstantValues.FROM_NOW_PLAYING,true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
                        new Pair<View, String>(mSongNameTv,getString(R.string.transition_song_name)),
                        new Pair<View, String>(mSingerNameTv,getString(R.string.transition_singer_name)),
                        new Pair<View, String>(mMusicPlayBtn,getString(R.string.transition_play_button))
                );
                startActivityForResult(intent,1,options.toBundle());
            }else {
                startActivityForResult(intent,1);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==1){
            Intent intent=new Intent();
            intent.setAction(ConstantValues.MUSIC_PREPARED);
            sendBroadcast(intent);
        }
    }

    private void init() {
        mSongNameTv = (TextView) findViewById(R.id.tv_song_name);
        mSingerNameTv = (TextView) findViewById(R.id.tv_singer_name);
        timelyView11 = (TimelyView) findViewById(R.id.timelyView11);
        timelyView12 = (TimelyView) findViewById(R.id.timelyView12);
        timelyView13 = (TimelyView) findViewById(R.id.timelyView13);
        timelyView14 = (TimelyView) findViewById(R.id.timelyView14);
        timelyView15 = (TimelyView) findViewById(R.id.timelyView15);
        hourColon = (TextView) findViewById(R.id.hour_colon);
        mSeekBar = (CircularSeekBar) findViewById(R.id.song_progress_circular);
        mPlayModeBtn = (ImageView) findViewById(R.id.btn_music_play_mode);
        mFavoritBtn = (ImageView) findViewById(R.id.btn_music_favorite);
        mPreviousBtn = (ImageView) findViewById(R.id.btn_previous);
        mMusicPlayBtn = (FloatingActionButton) findViewById(R.id.btn_music_play);
        mNextBtn = (ImageView) findViewById(R.id.btn_next);
        mLyricTv = (LyricTextView) findViewById(R.id.lyric);
        mAlbumArtBlurred = (ImageView) findViewById(R.id.iv_album_art_blurred);
        mAlbumArt = (CircleImageView) findViewById(R.id.iv_album_art);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setTitle("");
    }

    private void initAudio() {
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    private void initData() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstantValues.MUSIC_PREPARED);
        mReceiver = new PreparedReceiver();
        registerReceiver(mReceiver, intentFilter);

        initSongList();
    }

    private void initSongList() {
        Intent intent = getIntent();
        mPlaylisChanged = intent.getBooleanExtra(ConstantValues.PLAYLIST_CHANGED, true);
        if (mPlaylisChanged) {
            mCurrentPosition = intent.getIntExtra(ConstantValues.MUSIC_CURRENT_POSITION, 0);
            Bundle bundle = intent.getExtras();
            mSongList = (ArrayList<Song>) bundle.getSerializable(ConstantValues.PLAYLIST);
        }
        mHandler.sendEmptyMessage(INIT_SONGLIST_FINISH);
    }

    class PreparedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (musicService != null) {
                if (!mPlaylisChanged) {
                    try {
                        mSongList = (ArrayList<Song>) musicService.getSongList();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                initListener();
                getMusicInfo();
                initLyric();
            }
        }
    }


    private void bindMusicService() {
        Intent intent = new Intent();
        intent.setAction("com.liuhaoyuan.myplayer.BIND_MUSIC_SERVICE");
        intent.setPackage(getPackageName());

//        intent.putExtra("is_remote_play", isRemotePlay);
        intent.putExtra(ConstantValues.PLAYLIST_CHANGED, mPlaylisChanged);
        if (mPlaylisChanged) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(ConstantValues.PLAYLIST, mSongList);
            intent.putExtras(bundle);
        }
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
                    if ((mPlaylisChanged) &&
                            ((TextUtils.isEmpty(musicService.getUrl())) ||
                                    (mCurrentPosition != musicService.getCurrentPosition() || !mSongList.get(mCurrentPosition).url.equals(musicService.getUrl())))) {
                        musicService.openAudio(mCurrentPosition);
                    } else {
                        Intent intent = new Intent();
                        intent.setAction(ConstantValues.MUSIC_PREPARED);
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
        mMusicPlayBtn.setOnClickListener(mListener);
        mPreviousBtn.setOnClickListener(mListener);
        mNextBtn.setOnClickListener(mListener);
        mPlayModeBtn.setOnClickListener(mListener);
        mFavoritBtn.setOnClickListener(mListener);

        mSeekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mSeekBar.setProgress(progress);
                    try {
                        musicService.seekTo(progress);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    if (mLyricHandler != null) {
                        mLyricHandler.post(mUpdateLyric);
                    }
                    if (mElapsedTimeHandler != null) {
                        mElapsedTimeHandler.post(mUpdateElapsedTime);
                    }
                }
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }
        });
    }

    private void getMusicInfo() {
        try {
            mCurrentPosition = musicService.getCurrentPosition();
            mSongNameTv.setText(musicService.getTitle());
            mSingerNameTv.setText(musicService.getArtist());
            isPlaying = musicService.isPlaying();
            if (isPlaying) {
                mMusicPlayBtn.setImageResource(R.drawable.ic_pause_white_24dp);
            } else {
                mMusicPlayBtn.setImageResource(R.drawable.ic_play_arrow_white_24dp);
            }

            playMode = musicService.getPlayMode();
            if (playMode == MusicPlayService.REPEAT_MODE_RANDOM) {
                mPlayModeBtn.setImageResource(R.drawable.ic_shuffle_white_36dp);
            } else if (playMode == MusicPlayService.REPEAT_MODE_ALL) {
                mPlayModeBtn.setImageResource(R.drawable.ic_repeat_white_36dp);
            } else if (playMode == MusicPlayService.REPEAT_MODE_SINGLE) {
                mPlayModeBtn.setImageResource(R.drawable.ic_repeat_one_white_36dp);
            }
            int dur = musicService.getDuration();
            mSeekBar.setMax(dur);
            mProgressHandler = new Handler();
            mProgressHandler.postDelayed(mUpdateProgress, 1000);
            initAlbumArt();
            initTimeViewInfo();
//            mHandler.sendEmptyMessageDelayed(UPDATE, 1000);
//            mHandler.sendEmptyMessage(SHOW_LYRICS);
            updateFavInfo();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void initTimeViewInfo() {
        try {
            mCurrentProgress = musicService.getCurrentProgress();
            String time = TimeFormatUtils.makeShortTimeString(MusicPlayActivity2.this, mCurrentProgress / 1000);
            if (time.length() < 5) {
                timelyView11.setVisibility(View.GONE);
                timelyView12.setVisibility(View.GONE);
                hourColon.setVisibility(View.GONE);
                changeDigit(timelyView13, time.charAt(0) - '0');
                changeDigit(timelyView14, time.charAt(2) - '0');
                changeDigit(timelyView15, time.charAt(3) - '0');

            } else if (time.length() == 5) {
                timelyView12.setVisibility(View.VISIBLE);
                changeDigit(timelyView12, time.charAt(0) - '0');
                changeDigit(timelyView13, time.charAt(1) - '0');
                changeDigit(timelyView14, time.charAt(3) - '0');
                changeDigit(timelyView15, time.charAt(4) - '0');
            } else {
                timelyView11.setVisibility(View.VISIBLE);
                hourColon.setVisibility(View.VISIBLE);
                changeDigit(timelyView11, time.charAt(0) - '0');
                changeDigit(timelyView12, time.charAt(2) - '0');
                changeDigit(timelyView13, time.charAt(3) - '0');
                changeDigit(timelyView14, time.charAt(5) - '0');
                changeDigit(timelyView15, time.charAt(6) - '0');
            }
            mElapsedTimeHandler = new Handler();
            mElapsedTimeHandler.postDelayed(mUpdateElapsedTime, 1000);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void updateFavInfo() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                mFavorited = FavoriteDbManager.getInstance(getApplicationContext()).songFavorited(mSongList.get(mCurrentPosition).url);
                Message message = mHandler.obtainMessage(UPDATE_FAVORITED);
                mHandler.sendMessage(message);
            }
        };
        thread.start();
    }

    private void initAlbumArt() {
        String imageUrl = mSongList.get(mCurrentPosition).albumpic_big;
        if (!TextUtils.isEmpty(imageUrl)) {
            ImageOptions imageOptions = new ImageOptions.Builder().setFailureDrawableId(R.drawable.music_fail).build();
            x.image().loadDrawable(imageUrl, imageOptions, new Callback.CommonCallback<Drawable>() {
                @Override
                public void onSuccess(Drawable result) {
                    BitmapDrawable drawable = (BitmapDrawable) result;
                    Bitmap bitmap = drawable.getBitmap();
                    mAlbumArt.setImageBitmap(bitmap);
                    ImageTask task = new ImageTask();
                    task.execute(bitmap);
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    mAlbumArtBlurred.setImageResource(R.drawable.skin_bg3);
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
        } else {
            mAlbumArtBlurred.setImageResource(R.drawable.skin_bg3);
        }
    }

    private void initLyric() {
        mLyricTv.setLyrics(null);
        if (mSongList != null && mSongList.size() > 0) {
            try {
                if (mLyrics!=null && mCurrentUrl.equals(musicService.getUrl())){
                    mLyricTv.setLyrics(mLyrics);

                    mLyricHandler = new Handler();
                    //此处延时是为了防止阻塞其他Handler
                    mLyricHandler.postDelayed(mUpdateLyric, 1000);
                }else {
                    mCurrentUrl=musicService.getUrl();
                    String songId = mSongList.get(mCurrentPosition).songid;
                    if (TextUtils.isEmpty(songId)) {
                        searchMoreLyrics(mSongList.get(mCurrentPosition).songname);
                    } else {
                        searchLyric(songId);
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void searchMoreLyrics(String songName) {
        MusicApi api = MusicApi.getInstance();
        api.registerObserver(new DataObserver<ArrayList<Song>>(DataObserver.TYPE_SONG) {
            @Override
            public void onComplete(ArrayList<Song> data) {
                if (data != null && data.size() > 0) {
                    searchLyric(data.get(0).songid);
                }
            }
        });
        api.getSong(songName, "1");
    }

    private void searchLyric(String songId) {
        MusicApi qqMusicApi = MusicApi.getInstance();
        qqMusicApi.registerObserver(new DataObserver<String>(DataObserver.TYPE_LYRIC) {
            @Override
            public void onComplete(String data) {
                if (!TextUtils.isEmpty(data)) {
                    LyricUtils lyricUtils = new LyricUtils();
                    lyricUtils.readLyricString(data);
                    mLyricTv.setLyrics(lyricUtils.getLyrics());
                    mLyrics=lyricUtils.getLyrics();
                    mLyricHandler = new Handler();
                    //此处延时是为了防止阻塞其他Handler
                    mLyricHandler.postDelayed(mUpdateLyric, 1000);
                }else {
                    mLyricTv.setLyrics(null);
                    mLyrics=null;
                }
            }
        });
        qqMusicApi.getLyric(songId);
    }

    @Override
    protected void onStop() {
//        if (mHandler != null) {
//            mHandler.removeCallbacksAndMessages(null);
//        }
//        if (mElapsedTimeHandler != null) {
//            mElapsedTimeHandler.removeCallbacksAndMessages(null);
//        }
//        if (mProgressHandler != null) {
//            mProgressHandler.removeCallbacksAndMessages(null);
//        }
//        if (mLyricHandler != null) {
//            mLyricHandler.removeCallbacksAndMessages(null);
//        }
//        if (mReceiver != null) {
//            unregisterReceiver(mReceiver);
//            mReceiver = null;
//        }
//        if (mConn != null) {
//            unbindService(mConn);
//        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mElapsedTimeHandler != null) {
            mElapsedTimeHandler.removeCallbacksAndMessages(null);
        }
        if (mProgressHandler != null) {
            mProgressHandler.removeCallbacksAndMessages(null);
        }
        if (mLyricHandler != null) {
            mLyricHandler.removeCallbacksAndMessages(null);
        }
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        if (mConn != null) {
            unbindService(mConn);
        }
        isDestoryed = true;
        super.onDestroy();
    }

    private class MusicListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_music_play:
                    try {
                        if (musicService.isPlaying()) {
                            isPlaying = false;
                            mMusicPlayBtn.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                            musicService.pause();
                        } else {
                            isPlaying = true;
                            mMusicPlayBtn.setImageResource(R.drawable.ic_pause_white_24dp);
                            musicService.play();
                            if (mProgressHandler != null) {
                                mProgressHandler.post(mUpdateProgress);
                            }
                            if (mElapsedTimeHandler != null) {
                                mElapsedTimeHandler.post(mUpdateElapsedTime);
                            }
                            if (mLyricHandler != null) {
                                mLyricHandler.post(mUpdateLyric);
                            }
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.btn_music_play_mode:
                    try {
                        if (playMode == MusicPlayService.REPEAT_MODE_SINGLE) {
                            playMode = MusicPlayService.REPEAT_MODE_ALL;
                            musicService.setPlayMode(MusicPlayService.REPEAT_MODE_ALL);
                            mPlayModeBtn.setImageResource(R.drawable.ic_repeat_white_36dp);
                        } else if (playMode == MusicPlayService.REPEAT_MODE_ALL) {
                            playMode = MusicPlayService.REPEAT_MODE_RANDOM;
                            musicService.setPlayMode(MusicPlayService.REPEAT_MODE_RANDOM);
                            mPlayModeBtn.setImageResource(R.drawable.ic_shuffle_white_36dp);
                        } else if (playMode == MusicPlayService.REPEAT_MODE_RANDOM) {
                            playMode = MusicPlayService.REPEAT_MODE_SINGLE;
                            musicService.setPlayMode(MusicPlayService.REPEAT_MODE_SINGLE);
                            mPlayModeBtn.setImageResource(R.drawable.ic_repeat_one_white_36dp);
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.btn_previous:
                    try {
                        musicService.previous();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.btn_next:
                    try {
                        musicService.next();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.btn_music_favorite:
                    if (mFavorited) {
                        mFavoritBtn.setImageResource(R.drawable.ic_favorite_border_white_36dp);
                        ThreadPoolManger.getInstance().execute(new Runnable() {
                            @Override
                            public void run() {
                                FavoriteDbManager.getInstance(getApplicationContext()).deleteSong(mSongList.get(mCurrentPosition).url);
                            }
                        });
                    } else {
                        mFavoritBtn.setImageResource(R.drawable.ic_favorite_red_400_36dp);
                        ThreadPoolManger.getInstance().execute(new Runnable() {
                            @Override
                            public void run() {
                                FavoriteDbManager.getInstance(getApplicationContext()).insertSong(mSongList.get(mCurrentPosition));
                            }
                        });
                    }
                    mFavorited = !mFavorited;
                default:
                    break;
            }
        }
    }

    public void changeDigit(TimelyView tv, int end) {
        ObjectAnimator obja = tv.animate(end);
        obja.setDuration(400);
        obja.start();
    }

    public void changeDigit(TimelyView tv, int start, int end) {
        try {
            ObjectAnimator obja = tv.animate(start, end);
            obja.setDuration(400);
            obja.start();
        } catch (InvalidParameterException e) {
            e.printStackTrace();
        }
    }

    public void tv11(int a) {
        if (a != timeArr[0]) {
            changeDigit(timelyView11, timeArr[0], a);
            timeArr[0] = a;
        }
    }

    public void tv12(int a) {
        if (a != timeArr[1]) {
            changeDigit(timelyView12, timeArr[1], a);
            timeArr[1] = a;
        }
    }

    public void tv13(int a) {
        if (a != timeArr[2]) {
            changeDigit(timelyView13, timeArr[2], a);
            timeArr[2] = a;
        }
    }

    public void tv14(int a) {
        if (a != timeArr[3]) {
            changeDigit(timelyView14, timeArr[3], a);
            timeArr[3] = a;
        }
    }

    public void tv15(int a) {
        if (a != timeArr[4]) {
            changeDigit(timelyView15, timeArr[4], a);
            timeArr[4] = a;
        }
    }

    private class ImageTask extends AsyncTask<Bitmap, Void, Drawable> {

        @Override
        protected Drawable doInBackground(Bitmap... params) {
            Drawable d = ImageUtils.createBlurredImageFromBitmap(params[0], getApplicationContext(), 3);
            return d;
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            super.onPostExecute(drawable);
            mAlbumArtBlurred.setImageDrawable(drawable);
        }
    }
}
