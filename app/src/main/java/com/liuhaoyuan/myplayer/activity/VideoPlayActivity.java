package com.liuhaoyuan.myplayer.activity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.liuhaoyuan.myplayer.R;
import com.liuhaoyuan.myplayer.domain.video.VideoInfo;
import com.liuhaoyuan.myplayer.utils.TimeFormatUtils;
import com.liuhaoyuan.myplayer.view.MyVideoView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by liuhaoyuan on 2016/7/22.
 */
public class VideoPlayActivity extends AppCompatActivity {

    private ImageButton play;
    private ImageButton pre;
    private ImageButton next;
    private TextView beginTime;
    private TextView totalTime;
    private SeekBar progressBar;
    private MyVideoView myVideoView;
    private boolean isPaying;
    private MyVideoListener listener;
    private int duration;
    private boolean isDestoryed;
    private TextView currentTime;
    private TextView title;
    private ArrayList<VideoInfo> videoList;
    private int position;
    private GestureDetector gestureDetector;
    private RelativeLayout controllerLayout;
    private boolean showController = false;
    private WindowManager windowManager;
    private AudioManager audioManager;
    private int currentVolume;
    private int maxVolume;

    private static final int TIME_UPDATE = 1;
    private static final int HIDE_CONTROLLER = 2;
    private static final int SCREEN_TYPE_FILL = 3;
    private static final int SCREEN_TYPE_DEFAULT = 4;
    private static final int HIDE_LOCK=5;
    private static final int FINISH=6;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TIME_UPDATE:
                    if (!isDestoryed) {
                        int position = myVideoView.getCurrentPosition();
                        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                        Date date = new Date();
                        currentTime.setText(format.format(date));
                        progressBar.setProgress(position);
                        beginTime.setText(TimeFormatUtils.timeFormat((long) position));

                        if (isRemotePlay){
                            int bufferPercent=myVideoView.getBufferPercentage();
                            progressBar.setSecondaryProgress(bufferPercent*progressBar.getMax()/100);
                        }

                        handler.sendEmptyMessageDelayed(TIME_UPDATE, 1000);
                    }
                    break;
                case HIDE_CONTROLLER:
                    showController = false;
                    controllerLayout.setVisibility(View.GONE);
                    break;
                case  HIDE_LOCK:
                    lock.setVisibility(View.GONE);
                    break;
                case FINISH:

                    finish();
                    break;
                default:
                    break;
            }
        }
    };
    private int screenWidth;
    private int screenHeight;
    private int videoWidth;
    private int videoHeight;
    private boolean isFullScreen = false;
    private boolean isScreenLocked=false;
    private ImageButton lock;
    private float startX=-1;
    private float startY=-1;
    private Uri uri;
    private boolean isRemotePlay;
    private int currentPosition;
    private int maxPosition;
    private TextView seekIngTime;
    private RelativeLayout loadingLayout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //获得当前窗体对象
        Window window = getWindow();
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);

        setContentView(R.layout.activity_video_play);
        isDestoryed = false;

        play = (ImageButton) findViewById(R.id.btn_video_play);
        pre = (ImageButton) findViewById(R.id.btn_video_pre);
        next = (ImageButton) findViewById(R.id.btn_video_next);
        beginTime = (TextView) findViewById(R.id.tv_video_begin_time);
        totalTime = (TextView) findViewById(R.id.tv_video_end_time);
        progressBar = (SeekBar) findViewById(R.id.pb_video);
        currentTime = (TextView) findViewById(R.id.tv_current_time);
        title = (TextView) findViewById(R.id.tv_title_video_play);
        myVideoView = (MyVideoView) findViewById(R.id.vv_play);
        controllerLayout = (RelativeLayout) findViewById(R.id.rl_controller);
        lock = (ImageButton) findViewById(R.id.btn_screen_lock);
        seekIngTime = (TextView) findViewById(R.id.tv_seeking_time);
        loadingLayout = (RelativeLayout) findViewById(R.id.rl_loading);

        windowManager = getWindowManager();
        screenWidth = windowManager.getDefaultDisplay().getWidth();
        screenHeight = windowManager.getDefaultDisplay().getHeight();

        audioManager= (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVolume=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras!=null){
            videoList = (ArrayList<VideoInfo>) extras.getSerializable("video_info_list");
            position = intent.getIntExtra("position", 0);
        }

        uri = intent.getData();


        setData();
        init();
    }
    private void setData(){
        if (videoList!=null && videoList.size()>0){
            isRemotePlay=false;
            loadingLayout.setVisibility(View.GONE);
            myVideoView.setVideoURI(Uri.parse(videoList.get(position).getData()));
            title.setText(videoList.get(position).getTitle());
        }else if (uri!=null) {
            isRemotePlay=true;
            myVideoView.setVideoURI(uri);
            title.setText(uri.toString());
        }
    }

    private void init() {
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (!isScreenLocked){
                    if (isFullScreen){
                        isFullScreen=false;
                        setScreenType(SCREEN_TYPE_DEFAULT);
                    }else {
                        isFullScreen=true;
                        setScreenType(SCREEN_TYPE_FILL);
                    }
                }
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                lock.setVisibility(View.VISIBLE);
                handler.sendEmptyMessageDelayed(HIDE_LOCK,5000);

                if (!isScreenLocked){
                    if (showController) {
                        showController = false;
                        controllerLayout.setVisibility(View.GONE);
                        lock.setVisibility(View.GONE);
                    } else {
                        showController = true;
                        controllerLayout.setVisibility(View.VISIBLE);
                        lock.setVisibility(View.VISIBLE);
                        handler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 5000);
                        handler.sendEmptyMessageDelayed(HIDE_LOCK,5000);
                    }
                }
                return true;
            }
        });
        listener = new MyVideoListener();

        myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {

                videoWidth = mediaPlayer.getVideoWidth();
                videoHeight = mediaPlayer.getVideoHeight();
                setScreenType(SCREEN_TYPE_DEFAULT);
                isFullScreen=false;
                showController = false;
                controllerLayout.setVisibility(View.GONE);
                lock.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);

                myVideoView.start();
                isPaying = true;

                play.setImageResource(R.drawable.ic_pause_white_24dp);
                duration = myVideoView.getDuration();
                totalTime.setText(TimeFormatUtils.timeFormat(duration));
                progressBar.setMax(duration);
                handler.sendEmptyMessageDelayed(TIME_UPDATE, 1000);
            }
        });

        play.setOnClickListener(listener);
        pre.setOnClickListener(listener);
        next.setOnClickListener(listener);
        lock.setOnClickListener(listener);
        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    myVideoView.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeMessages(HIDE_CONTROLLER);
                handler.removeMessages(HIDE_LOCK);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 5000);
                handler.sendEmptyMessageDelayed(HIDE_LOCK,5000);
            }
        });

        myVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                isPaying = false;
                play.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                playNextVideo();
            }
        });

        myVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
//                Toast.makeText(VideoPlayActivity.this,"播放出错了",Toast.LENGTH_SHORT).show();

//                finish();
                mediaPlayer.stop();
                mediaPlayer.release();

                Intent vitamioIntent =new Intent(VideoPlayActivity.this,VideoPlayActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("video_info_list",videoList);
                vitamioIntent.putExtras(bundle);
                vitamioIntent.putExtra("position",position);
                vitamioIntent.setData(uri);
                startActivity(vitamioIntent);

                handler.sendEmptyMessageDelayed(FINISH,5000);
                return true;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        if (!isScreenLocked){
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    startX = event.getX();
                    startY = event.getY();

                    currentVolume=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    currentPosition = myVideoView.getCurrentPosition();
                    maxPosition=  myVideoView.getDuration();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float x=  event.getX();
                    float y=  event.getY();

                    if (Math.abs(x-startX)>Math.abs(y-startY)){
                        float distanceX=x-startX;
                        if (Math.abs(distanceX)>50){
                            float percent=distanceX/(float) screenWidth;
                            int p=currentPosition+(int) (percent*maxPosition);
                            if (p<0){
                                p=0;
                            }else if (p>maxPosition){
                                p=maxPosition;
                            }
                            if (p!=currentPosition){
                                seekIngTime.setVisibility(View.VISIBLE);
                                seekIngTime.setText(TimeFormatUtils.timeFormat((long) p));
                                progressBar.setProgress(p);
                                myVideoView.seekTo(p);
                            }
                        }
                    }else {
                        float distanceY=startY-y;
                        float percent=distanceY/(float) (screenHeight);
                        int vol= (int) (percent*maxVolume)+currentVolume;
                        if (vol<0){
                            vol=0;
                        }else if (vol>maxVolume){
                            vol=maxVolume;
                        }
                        if (vol!=currentVolume){
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,vol,1);
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    seekIngTime.setVisibility(View.GONE);
                    startX=-1;
                    startY=-1;
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    class MyVideoListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_video_play:
                    if (isPaying) {
                        myVideoView.pause();
                        isPaying = false;
                        play.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                    } else {
                        myVideoView.start();
                        isPaying = true;
                        play.setImageResource(R.drawable.ic_pause_white_24dp);
                    }
                    break;
                case R.id.btn_video_next:
                    playNextVideo();
                    break;
                case R.id.btn_video_pre:
                    playPreviousVideo();
                    break;
                case R.id.btn_screen_lock:
                    if (isScreenLocked){
                        isScreenLocked=false;
                        lock.setImageResource(R.drawable.ic_lock_open_white_24dp);
                    }else {
                        isScreenLocked=true;
                        lock.setImageResource(R.drawable.ic_lock_white_24dp);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void playPreviousVideo() {
        if (!isRemotePlay){
            position--;
            if (position < 0) {
                position = 0;
            }
            VideoInfo videoInfo = videoList.get(position);
            myVideoView.setVideoPath(videoInfo.getData());
            title.setText(videoInfo.getTitle());
        }
    }

    private void playNextVideo() {
        if (!isRemotePlay){
            position++;
            if (position >= videoList.size()) {
                position--;
                Toast.makeText(this, "没有视频了", Toast.LENGTH_SHORT).show();
            } else {
                VideoInfo videoInfo = videoList.get(position);
                myVideoView.setVideoPath(videoInfo.getData());
                title.setText(videoInfo.getTitle());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestoryed = true;
    }

    private void setScreenType(int type) {
        switch (type) {
            case SCREEN_TYPE_DEFAULT:
                int width = screenWidth;
                int height = screenHeight;
                if (videoWidth > 0 && videoHeight > 0) {
                    if (videoWidth * height > width * videoHeight) {
                        height = width * videoHeight / videoWidth;
                    } else if (videoWidth * height < width * videoHeight) {
                        width = height * videoWidth / videoHeight;
                    } else {
                    }
                }
                myVideoView.setVideoSize(width,height);
                isFullScreen=false;
                break;
            case SCREEN_TYPE_FILL:
                myVideoView.setVideoSize(screenWidth, screenHeight);
                isFullScreen = true;
                break;
            default:
                break;
        }
    }
}
