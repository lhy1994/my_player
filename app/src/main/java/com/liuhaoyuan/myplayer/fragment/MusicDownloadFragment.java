package com.liuhaoyuan.myplayer.fragment;

import android.view.View;
import android.widget.TextView;

/**
 * Created by hyliu on 2017/1/31.
 */

public class MusicDownloadFragment extends BaseFragment {
    @Override
    public void loadData() {
        onLoadingComplete(true);
    }

    @Override
    public View onCreateSuccessView() {
        TextView textView=new TextView(getContext());
        textView.setText("music down");
        return textView;
    }
}
