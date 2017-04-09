package com.liuhaoyuan.myplayer.utils;

import android.util.Log;

/**
 * Created by liuhaoyuan on 17/4/4.
 */

public class LogUtils {
    public static void e(Object o,String text){
        Log.e(o.getClass().getSimpleName(), text);
    }
}
