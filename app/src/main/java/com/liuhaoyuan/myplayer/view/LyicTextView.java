package com.liuhaoyuan.myplayer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.liuhaoyuan.myplayer.domain.Lyric;
import com.liuhaoyuan.myplayer.utils.LyricUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by liuhaoyuan on 2016/7/25.
 */
public class LyicTextView extends TextView{

    private Paint currentPaint;
    private Paint paint;

    public void setLyrics(ArrayList<Lyric> lyrics) {
        this.lyrics = lyrics;
    }

    ArrayList<Lyric> lyrics;
    private int index;
    private  float TEXT_HEIGHT=20;
    private float progress;
    private float timePoint;
    private float duration;

    public LyicTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView(){

        currentPaint =new Paint();
        currentPaint.setColor(Color.YELLOW);
        currentPaint.setAntiAlias(true);
        currentPaint.setTextAlign(Paint.Align.CENTER);
        currentPaint.setTextSize(getTextSize()*1.2f);

        paint =new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(getTextSize());

        TEXT_HEIGHT=getTextSize();

//        LyricUtils lyricUtils=new LyricUtils();
//        File file=new File(Environment.getExternalStorageDirectory(),"zxmzf.lrc");
//        if (file.exists()){
//            try {
//                lyricUtils.readLyricFile(file);
//                lyrics=lyricUtils.getLyrics();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }else {
//            Log.e("test","..........."+"no this file");
//        }

//        lyrics=new ArrayList<>();
//        for (int i=0;i<20;i++){
//            Lyric lyric=new Lyric();
//            lyric.text=i+"aaaaaaa";
//            lyric.duration=2000;
//            lyric.timePoint=i*1000;
//
//            lyrics.add(lyric);
//        }
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
            if (progress<lyrics.get(i).timePoint){
                int tempIndex=i-1;

//                Log.e("test","progress...."+progress);
//                Log.e("tst","timepoint......"+lyrics.get(tempIndex).timePoint);
                if (progress>=lyrics.get(tempIndex).timePoint){
                    index=tempIndex;
                    timePoint=lyrics.get(tempIndex).timePoint;
                    duration=lyrics.get(tempIndex).duration;
                    break;
                }
            }
        }
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);

        float distance=0;
        if (duration==0){
            distance=0;
        }else {
            float step=((progress-timePoint)/duration) *TEXT_HEIGHT;
            distance=TEXT_HEIGHT+step;
        }
        canvas.translate(0,-distance);
//        Log.e("tst","distance is ......."+distance);

        if (lyrics!=null && lyrics.size()>0){
            String currentText=lyrics.get(index).text;
            canvas.drawText(currentText,width/2,height/2,currentPaint);

            int tempY=height/2;
            for (int i=index-1;i>0;i--){
                tempY-=(TEXT_HEIGHT*2);
                if (tempY<0){
                    break;
                }
                canvas.drawText(lyrics.get(i).text,width/2,tempY,paint);
            }

            tempY=height/2;
            for (int i=index+1;i<lyrics.size();i++){
                tempY+=(TEXT_HEIGHT*2);
                if (tempY>height){
                    break;
                }
                canvas.drawText(lyrics.get(i).text,width/2,tempY,paint);
            }
        }else {
            canvas.drawText("没有找到歌词",width/2,height/2, currentPaint);
        }
    }
}
