package com.liuhaoyuan.myplayer.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.AutoTransition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.liuhaoyuan.myplayer.APP;
import com.liuhaoyuan.myplayer.R;
import com.liuhaoyuan.myplayer.aidl.IMusicPlayService;
import com.liuhaoyuan.myplayer.aidl.Song;
import com.liuhaoyuan.myplayer.utils.ConstantValues;
import com.liuhaoyuan.myplayer.utils.ImageUtils;
import com.liuhaoyuan.myplayer.utils.LogUtils;
import com.liuhaoyuan.myplayer.utils.TimeFormatUtils;

import org.xutils.common.Callback;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

public class PlaylistActivity extends AppCompatActivity {

    private ImageView mAlbumArtBlurredIv;
    private ImageView mAlbumArtIv;
    private TextView mSongNameTv;
    private TextView mSingerNameTv;
    private SeekBar mSeekbar;
    private TextView mStartTimeTv;
    private TextView mEndTimeTv;
    private FloatingActionButton mPlayBtn;
    private TextView mSongCountTv;
    private RecyclerView mSongListView;
    private List<Song> mSongList;
    private PreparedReceiver mReceiver;
    private int mCurrentPosition;
    private boolean mIsPlaying;
    private ComponentListener mListener;

    private Handler mProgressHandler;
    private Runnable mUpdateProgressTask = new Runnable() {
        @Override
        public void run() {
            try {
                if (musicService != null) {
                    int currentProgress = musicService.getCurrentProgress();
                    mSeekbar.setProgress(currentProgress);
                    mStartTimeTv.setText(TimeFormatUtils.timeFormatNoHour(currentProgress));
                    if (mIsPlaying) {
                        mProgressHandler.postDelayed(this, 1000);
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        setContentView(R.layout.activity_playlist);
        init();
    }

    private void init() {
        mAlbumArtBlurredIv = (ImageView) findViewById(R.id.iv_album_art_blurred);
        mAlbumArtIv = (ImageView) findViewById(R.id.iv_album_art);
        mSongNameTv = (TextView) findViewById(R.id.tv_song_name);
        mSingerNameTv = (TextView) findViewById(R.id.tv_singer_name);
        mSeekbar = (SeekBar) findViewById(R.id.sb_playlist);
        mStartTimeTv = (TextView) findViewById(R.id.tv_start_time);
        mEndTimeTv = (TextView) findViewById(R.id.tv_end_time);
        mPlayBtn = (FloatingActionButton) findViewById(R.id.fab);
        mSongCountTv = (TextView) findViewById(R.id.tv_song_count);
        mSongListView = (RecyclerView) findViewById(R.id.lv_playlist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setTitle("");

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstantValues.MUSIC_PREPARED);
        mReceiver = new PreparedReceiver();
        registerReceiver(mReceiver, intentFilter);

        bindMusicService();
        boolean fromNowPlaying = getIntent().getBooleanExtra(ConstantValues.FROM_NOW_PLAYING, false);
        if (fromNowPlaying){
            Intent intent=new Intent();
            intent.putExtra(ConstantValues.PLAYLIST_CHANGED,false);
            setResult(1,intent);
        }
    }

    private void bindMusicService() {
        Intent intent = new Intent();
        intent.setAction("com.liuhaoyuan.myplayer.BIND_MUSIC_SERVICE");
        intent.setPackage(getPackageName());
        intent.putExtra(ConstantValues.PLAYLIST_CHANGED, false);
        startService(intent);
        bindService(intent, mConn, Context.BIND_AUTO_CREATE);
    }

    private void initData() {
        try {
            mSongList = musicService.getSongList();
            if (mSongList != null && mSongList.size() > 0) {
                mCurrentPosition = musicService.getCurrentPosition();
                mProgressHandler = new Handler();
                updateControlView();
                mListener = new ComponentListener();
                mSeekbar.setOnSeekBarChangeListener(mListener);
                mPlayBtn.setOnClickListener(mListener);
                updateAlbumArt();
                initList();
            } else {

            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void updateControlView() {
        try {
            mSongNameTv.setText(mSongList.get(mCurrentPosition).songname);
            mSingerNameTv.setText(mSongList.get(mCurrentPosition).singername);
            int duration = musicService.getDuration();
            int progress = musicService.getCurrentProgress();
            mStartTimeTv.setText(TimeFormatUtils.timeFormatNoHour(progress));
            mEndTimeTv.setText(TimeFormatUtils.timeFormatNoHour(duration));
            mSeekbar.setMax(duration);
            mSeekbar.setProgress(progress);
            mIsPlaying = musicService.isPlaying();
            mPlayBtn.setImageResource(mIsPlaying ? R.drawable.ic_pause_white_24dp : R.drawable.ic_play_arrow_white_24dp);
            mProgressHandler.removeCallbacks(mUpdateProgressTask);
            mProgressHandler.postDelayed(mUpdateProgressTask, 1000);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void updateAlbumArt() {
        String imageUrl = mSongList.get(mCurrentPosition).albumpic_big;
        if (!TextUtils.isEmpty(imageUrl)) {
            ImageOptions imageOptions = new ImageOptions.Builder().setFailureDrawableId(R.drawable.music_fail).build();
            x.image().bind(mAlbumArtIv, imageUrl, imageOptions, new Callback.CommonCallback<Drawable>() {
                @Override
                public void onSuccess(Drawable result) {
                    BitmapDrawable bitmapDrawable= (BitmapDrawable) result;
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    ImageTask imageTask=new ImageTask();
                    imageTask.execute(bitmap);
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {

                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
        } else {
            mAlbumArtBlurredIv.setBackgroundColor(Color.BLACK);
        }
    }

    private void initList() {
        mSongCountTv.setText(mSongList.size()+"首歌");
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        PlaylistAdapter adapter = new PlaylistAdapter();
        mSongListView.setLayoutManager(layoutManager);
        mSongListView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressHandler!=null){
            mProgressHandler.removeCallbacksAndMessages(null);
        }
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        if (mConn != null) {
            unbindService(mConn);
        }
    }

    private class PreparedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (musicService!=null){
                    mCurrentPosition = musicService.getCurrentPosition();
                    updateControlView();
                    updateAlbumArt();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private IMusicPlayService musicService;
    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            musicService = IMusicPlayService.Stub.asInterface(iBinder);
            if (musicService != null) {
                initData();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicService = null;
        }
    };

    private class ImageTask extends AsyncTask<Bitmap, Void, Drawable> {

        @Override
        protected Drawable doInBackground(Bitmap... params) {
            Drawable d = ImageUtils.createBlurredImageFromBitmap(params[0], getApplicationContext(), 3);
            return d;
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            super.onPostExecute(drawable);
            mAlbumArtBlurredIv.setImageDrawable(drawable);
        }
    }

    private class PlaylistAdapter extends RecyclerView.Adapter<PlaylistHolder> {

        @Override
        public PlaylistHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, parent, false);
            return new PlaylistHolder(view);
        }

        @Override
        public void onBindViewHolder(PlaylistHolder holder, final int position) {
            Song song = mSongList.get(position);
            final Bitmap[] pic = new Bitmap[1];
            ImageOptions imageOptions = new ImageOptions.Builder().setFailureDrawableId(R.drawable.music_fail).build();
            x.image().bind(holder.mAlbumLogoIv, song.albumpic_big, imageOptions, new Callback.CommonCallback<Drawable>() {
                @Override
                public void onSuccess(Drawable result) {
                    BitmapDrawable bitmapDrawable= (BitmapDrawable) result;
                    pic[0] = bitmapDrawable.getBitmap();
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {

                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
            holder.mSongNameTv.setText(song.songname);
            holder.mSingerNameTv.setText(song.singername);
            holder.mMoreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (musicService != null && mCurrentPosition!=position) {
                            musicService.openAudio(position);
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mSongList.size();
        }
    }

    private class PlaylistHolder extends RecyclerView.ViewHolder {

        private ImageView mAlbumLogoIv;
        private TextView mSongNameTv;
        private TextView mSingerNameTv;
        private Button mMoreBtn;

        public PlaylistHolder(View itemView) {
            super(itemView);
            mAlbumLogoIv = (ImageView) itemView.findViewById(R.id.iv_rank_song);
            mSongNameTv = (TextView) itemView.findViewById(R.id.tv_song_name);
            mSingerNameTv = (TextView) itemView.findViewById(R.id.tv_singer_name);
            mMoreBtn = (Button) itemView.findViewById(R.id.btn_more);
        }
    }

    private class ComponentListener implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fab:
                    if (musicService != null) {
                        try {
                            if (musicService.isPlaying()) {
                                mIsPlaying = false;
                                mPlayBtn.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                                musicService.pause();
                            } else {
                                mIsPlaying = true;
                                mPlayBtn.setImageResource(R.drawable.ic_pause_white_24dp);
                                musicService.play();
                                if (mProgressHandler != null) {
                                    mProgressHandler.removeCallbacks(mUpdateProgressTask);
                                    mProgressHandler.postDelayed(mUpdateProgressTask, 1000);
                                }
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                seekBar.setProgress(progress);
                mStartTimeTv.setText(TimeFormatUtils.timeFormatNoHour(progress));
                try {
                    if (musicService != null) {
                        musicService.seekTo(progress);
                    }
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

        }
    }
}
