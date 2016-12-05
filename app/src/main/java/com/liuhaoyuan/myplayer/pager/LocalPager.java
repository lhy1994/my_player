package com.liuhaoyuan.myplayer.pager;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.liuhaoyuan.myplayer.LocalMusicActivity;
import com.liuhaoyuan.myplayer.LocalVideoActivity;
import com.liuhaoyuan.myplayer.R;

import io.vov.vitamio.provider.MediaStore;

/**
 * Created by liuhaoyuan on 2016/7/25.
 */
public class LocalPager extends BasePager {

    private LocalButtonListener mListener;
    private Button music;
    private Button video;

    public LocalPager(Activity activity) {
        super(activity);
    }

    @Override
    public View inintView() {
        title="本地";
        View view=View.inflate(activity, R.layout.pager_local,null);
        music = (Button) view.findViewById(R.id.btn_local_music);
        video = (Button) view.findViewById(R.id.btn_local_video);

        return view;
    }

    @Override
    public void initData() {
        mListener = new LocalButtonListener();
        music.setOnClickListener(mListener);
        video.setOnClickListener(mListener);
    }

    class LocalButtonListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_local_music:
                    activity.startActivity(new Intent(activity, LocalMusicActivity.class));
                    break;
                case R.id.btn_local_video:
                    activity.startActivity(new Intent(activity, LocalVideoActivity.class));
                    break;
                default:
                    break;
            }
        }
    }
}
