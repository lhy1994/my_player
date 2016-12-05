package com.liuhaoyuan.myplayer.domain.video;

import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by liuhaoyuan on 2016/8/1.
 */
public class VideoResultInfo {
    public String total;
    public ArrayList<VideoResult> videos;
    public class VideoResult{
        public String id;
        public String title;
        public String link;
        public String thumbnail;
        public String thumbnail_v2;
        public String bigThumbnail;
        public String bigThumbnail_v2;
        public String duration;
        public String category;
        public String tags;
        public String state;
        public String view_count;
        public String comment_count;
        public String favorite_count;
        public String up_count;
        public String down_count;
        public String published;

        @Override
        public String toString() {
            return "VideoResult{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", link='" + link + '\'' +
                    ", thumbnail='" + thumbnail + '\'' +
                    ", thumbnail_v2='" + thumbnail_v2 + '\'' +
                    ", bigThumbnail='" + bigThumbnail + '\'' +
                    ", bigThumbnail_v2='" + bigThumbnail_v2 + '\'' +
                    ", duration='" + duration + '\'' +
                    ", category='" + category + '\'' +
                    ", tags='" + tags + '\'' +
                    ", state='" + state + '\'' +
                    ", view_count='" + view_count + '\'' +
                    ", comment_count='" + comment_count + '\'' +
                    ", favorite_count='" + favorite_count + '\'' +
                    ", up_count='" + up_count + '\'' +
                    ", down_count='" + down_count + '\'' +
                    ", published='" + published + '\'' +
                    '}';
        }
    }
}
