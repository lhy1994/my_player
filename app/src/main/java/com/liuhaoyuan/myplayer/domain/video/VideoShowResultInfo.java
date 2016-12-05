package com.liuhaoyuan.myplayer.domain.video;

import java.util.ArrayList;

/**
 * Created by liuhaoyuan on 2016/8/2.
 */
public class VideoShowResultInfo {
    public String total;
    public ArrayList<VideoShowResult> shows;
    public class VideoShowResult{
        public String id;
        public String name;
        public String link;
        public String play_link;
        public String poster;
        public String bigPoster;
        public String thumbnail;
        public String bigThumbnail;
        public String showcategory;
        public String description;
        public String area;
        public String episode_count;
        public String episode_updated;
        public String view_count;
        public String score;

        @Override
        public String toString() {
            return "VideoShowResult{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", link='" + link + '\'' +
                    ", play_link='" + play_link + '\'' +
                    ", poster='" + poster + '\'' +
                    ", bigPoster='" + bigPoster + '\'' +
                    ", thumbnail='" + thumbnail + '\'' +
                    ", bigThumbnail='" + bigThumbnail + '\'' +
                    ", showcategory='" + showcategory + '\'' +
                    ", description='" + description + '\'' +
                    ", area='" + area + '\'' +
                    ", episode_count='" + episode_count + '\'' +
                    ", episode_updated='" + episode_updated + '\'' +
                    ", view_count='" + view_count + '\'' +
                    ", score='" + score + '\'' +
                    '}';
        }
    }
}
