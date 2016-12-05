package com.liuhaoyuan.myplayer.utils;

/**
 * Created by liuhaoyuan on 2016/11/29.
 */

public class VideoUtils {

    public static String getVideoSiteName(String siteID) {
        switch (siteID) {
            case "1":
                return "土豆";
            case "2":
                return "56网";
            case "3":
                return "新浪";
            case "4":
                return "琥珀";
            case "5":
                return "第一视频";
            case "6":
                return "搜狐";
            case "7":
                return "央视";
            case "8":
                return "凤凰";
            case "9":
                return "激动";
            case "10":
                return "酷6";
            case "11":
                return "天线视频";
            case "12":
                return "六间房";
            case "13":
                return "中关村在线";
            case "14":
                return "优酷";
            case "15":
                return "CNTV";
            case "16":
                return "电影网";
            case "17":
                return "乐视";
            case "18":
                return "小银幕";
            case "19":
                return "爱奇艺";
            case "20":
                return "江苏卫视";
            case "23":
                return "安徽卫视";
            case "24":
                return "芒果卫视";
            case "25":
                return "爱拍游戏";
            case "26":
                return "音悦台";
            case "27":
                return "腾讯";
            case "28":
                return "迅雷";
            case "29":
                return "优米";
            case "30":
                return "163";
            case "31":
                return "PPTV";
            default:
                return "未知站点";
        }
    }
}
