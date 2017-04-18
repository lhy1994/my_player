package com.liuhaoyuan.myplayer.utils;

import com.liuhaoyuan.myplayer.utils.number.Eight;
import com.liuhaoyuan.myplayer.utils.number.Five;
import com.liuhaoyuan.myplayer.utils.number.Four;
import com.liuhaoyuan.myplayer.utils.number.Nine;
import com.liuhaoyuan.myplayer.utils.number.Null;
import com.liuhaoyuan.myplayer.utils.number.One;
import com.liuhaoyuan.myplayer.utils.number.Seven;
import com.liuhaoyuan.myplayer.utils.number.Six;
import com.liuhaoyuan.myplayer.utils.number.Three;
import com.liuhaoyuan.myplayer.utils.number.Two;
import com.liuhaoyuan.myplayer.utils.number.Zero;

import java.security.InvalidParameterException;

/**
 * Created by liuhaoyuan on 17/4/15.
 */

public class NumberUtils {
    public static float[][] getControlPointsFor(int start) {
        switch (start) {
            case (-1):
                return Null.getInstance().getControlPoints();
            case 0:
                return Zero.getInstance().getControlPoints();
            case 1:
                return One.getInstance().getControlPoints();
            case 2:
                return Two.getInstance().getControlPoints();
            case 3:
                return Three.getInstance().getControlPoints();
            case 4:
                return Four.getInstance().getControlPoints();
            case 5:
                return Five.getInstance().getControlPoints();
            case 6:
                return Six.getInstance().getControlPoints();
            case 7:
                return Seven.getInstance().getControlPoints();
            case 8:
                return Eight.getInstance().getControlPoints();
            case 9:
                return Nine.getInstance().getControlPoints();
            default:
                throw new InvalidParameterException("Unsupported number requested");
        }
    }
}
