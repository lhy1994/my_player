package com.liuhaoyuan.myplayer;

import android.app.Application;
import android.content.SharedPreferences;

import com.liuhaoyuan.myplayer.utils.ConstantValues;

import org.xutils.x;

/**
 * Created by lhy on 2016/7/26.
 */
public class APP extends Application {
    public int mCurrentTheme;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化
        x.Ext.init(this);
        // 设置是否输出debug
        x.Ext.setDebug(true);

        sharedPreferences = getSharedPreferences(ConstantValues.CONFIG, MODE_PRIVATE);
        mCurrentTheme=sharedPreferences.getInt(ConstantValues.CURRENT_THEME,R.style.AppTheme_NoActionBar);
    }

    public int getCurrentTheme() {
        return mCurrentTheme;
    }

    public void setCurrentTheme(int theme) {
        mCurrentTheme = theme;
        sharedPreferences.edit().putInt(ConstantValues.CURRENT_THEME, theme).commit();
    }
}
