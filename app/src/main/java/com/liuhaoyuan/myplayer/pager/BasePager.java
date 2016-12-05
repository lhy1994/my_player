package com.liuhaoyuan.myplayer.pager;

import android.app.Activity;
import android.content.Context;
import android.view.View;

/**
 * Created by liuhaoyuan on 2016/7/21.
 */
public abstract class BasePager {

    public Activity activity;
    public final View rootView;
    public String title;

    public BasePager(Activity activity){
        this.activity=activity;
        rootView = inintView();
    }

    public abstract View inintView();
    public abstract void initData();
}
