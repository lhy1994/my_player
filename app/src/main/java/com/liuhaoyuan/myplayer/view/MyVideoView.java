package com.liuhaoyuan.myplayer.view;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.VideoView;

/**
 * Created by liuhaoyuan on 2016/7/22.
 */


public class MyVideoView extends VideoView{

    private int mVideoHeight;
    private int mVideoWidth;
    private MediaPlayer.OnInfoListener mOnInfoListener;

    public MyVideoView(Context context) {
        super(context);
    }

    public MyVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width=getDefaultSize(mVideoWidth,widthMeasureSpec);
        int height=getDefaultSize(mVideoHeight,heightMeasureSpec);
        setMeasuredDimension(width,height);
    }
    public void setVideoSize(int width,int height){
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width=width;
        layoutParams.height=height;
        setLayoutParams(layoutParams);
    }

    public void setOnInfoListener(MediaPlayer.OnInfoListener listener){
        mOnInfoListener=listener;
        
    }

}

