package com.liuhaoyuan.myplayer.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.liuhaoyuan.myplayer.R;
import com.liuhaoyuan.myplayer.domain.music.Lyric;

import java.util.ArrayList;

/**
 * Created by liuhaoyuan on 2016/7/25.
 */
public class LyricTextView extends android.support.v7.widget.AppCompatTextView{

    private Paint currentPaint;
    private Paint paint;
    private boolean nextLine;
    private float mCurrentY;
    private float mTempY;
    private String mCurrentText;
    private float mTanslateDistance;
    private float mDistance;

    public void setLyrics(ArrayList<Lyric> lyrics) {
        this.lyrics = lyrics;
    }

    ArrayList<Lyric> lyrics;
    private int index;
    private  float mTextHeight;
    private static final float DEFAULT_TEXT_HEIGHT=14;
    private float progress;
    private float timePoint;
    private float duration;
    private int mlyricColor;
    private static final int DEFAUT_COLOR=Color.YELLOW;

    public LyricTextView(Context context) {
        super(context);
        initView();
    }

    public LyricTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LyricTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LyricTextView, defStyleAttr, 0);
        mlyricColor=typedArray.getColor(R.styleable.LyricTextView_lyricColor,DEFAUT_COLOR);
        typedArray.recycle();
        initView();
    }

    private void initView(){
        mTextHeight =getTextSize();
        mDistance=mTextHeight*1.5f;

        currentPaint =new Paint();
        currentPaint.setColor(mlyricColor);
        currentPaint.setAntiAlias(true);
        currentPaint.setTextAlign(Paint.Align.CENTER);
        currentPaint.setTextSize(mTextHeight);

        paint =new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(mTextHeight);

    }

    private int width;
    private int height;
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width=w;
        height=h;
    }

    public void showLyric(int progress){
        this.progress=progress;

        if (lyrics==null){
            return;
        }

        for (int i=1;i<lyrics.size();i++){
            if (progress<=lyrics.get(i).timePoint){
                int tempIndex=i-1;
//                if (progress>=lyrics.get(tempIndex).timePoint){
//                    index=tempIndex;
//                    timePoint=lyrics.get(tempIndex).timePoint;
//                    duration=lyrics.get(tempIndex).duration;
//                    break;
//                }
                if (lyrics.get(i).timePoint-progress<=500){
                    nextLine = true;
                    index=i;
                    timePoint=lyrics.get(index).timePoint;
                }else {
                    nextLine=false;
                    index=tempIndex;
                    timePoint=lyrics.get(index).timePoint;
                }
                break;
            }
        }
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        float distance=0;
//        if (duration==0){
//            distance=0;
//        }else {
//            float step=((progress-timePoint)/duration) * mTextHeight;
//            distance= mTextHeight +step;
//        }
//        canvas.translate(0,-distance);
        if (lyrics!=null && lyrics.size()>0){
            if (nextLine){
                mCurrentY = height/2+mDistance;
                mTanslateDistance = ((progress-(timePoint-500))/500)*mDistance;
                canvas.translate(0,-mTanslateDistance);
            }else {
                mCurrentY=height/2;
            }

            mCurrentText = lyrics.get(index).text;
            canvas.drawText(mCurrentText,width/2,mCurrentY,currentPaint);

            mTempY = mCurrentY;
            for (int i=index-1;i>0;i--){
                mTempY -=(mDistance);
                if (mTempY <0-mDistance){
                    break;
                }
                canvas.drawText(lyrics.get(i).text,width/2, mTempY,paint);
            }

            mTempY =mCurrentY;
            for (int i=index+1;i<lyrics.size();i++){
                mTempY +=(mDistance);
                if (mTempY >height+mDistance){
                    break;
                }
                canvas.drawText(lyrics.get(i).text,width/2, mTempY,paint);
            }
        }else {
            canvas.drawText("没有找到歌词",width/2,height/2, currentPaint);
        }
    }
}
