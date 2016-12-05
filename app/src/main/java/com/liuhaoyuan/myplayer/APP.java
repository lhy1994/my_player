package com.liuhaoyuan.myplayer;

import android.app.Application;

import org.xutils.x;

/**
 * Created by lhy on 2016/7/26.
 */
public class APP extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化
        x.Ext.init(this);
        // 设置是否输出debug
        x.Ext.setDebug(true);
    }
}
