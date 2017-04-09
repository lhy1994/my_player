package com.liuhaoyuan.myplayer.domain.video;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liuhaoyuan on 17/4/4.
 */

public class YouKuShowDetail implements Serializable{

    /**
     * id : 80ab667eedcc11e6b16e
     * state : normal
     * copyright_status : authorized
     * name : 醒醒吧2
     * subtitle : 女主播贪财遭骗局毁容
     * alias : [{"type":"0","alias":"醒醒吧之道德骑士"}]
     * link : http://www.youku.com/show_page/id_z80ab667eedcc11e6b16e.html
     * play_link : http://v.youku.com/v_show/id_XMjUwMDU0NjQ0MA==.html
     * poster : http://r2.ykimg.com/050D0000589AD1DA67BC3C7F4B0CB110
     * poster_large : http://r2.ykimg.com/050E0000589AD1DA67BC3C7F4B0CB110
     * thumbnail : http://r2.ykimg.com/050B0000589AD1CB67BC3C4496037FDC
     * thumbnail_large : http://r2.ykimg.com/050C0000589AD1CB67BC3C4496037FDC
     * streamtypes : ["hd2","flv","hd","3gphd","hd3","mp5hd","mp5hd2","mp5hd3","mp3"]
     * hasvideotype : ["正片"]
     * genre : 喜剧,剧情
     * area : 大陆
     * completed : 1
     * episode_count : 1
     * episode_collected : 1
     * episode_updated : 1
     * update_notice :
     * view_count : 2475415
     * score : 6.776
     * paid : 1
     * published : 2017-02-10
     * released : 2017-00-00
     * releasedate_mainland : 2017-00-00
     * createtime : 2017-02-08 15:02:06
     * category : 电影
     * site : ["youku","tudou"]
     * description : 这个世界上总会有一群愚昧无知的人，也会有利用这群人混口饭的投机者，《名编》里的郑义就是典型的投机者，他会根据自身的优势及外部条件，抓住一切机会从那些盲从者手中骗取金钱利益，而《小师妹》中的小师妹则是典型的愚昧之人，她没有自己的判断力，他人的三言两语就能将她忽悠过去，盲从的同时也损失了金钱和时间，《有故事的人》里的三兄弟介于两者之间，他们既是愚昧之人，又想要骗取他们自认为愚昧之人的钱财，不想弄巧成拙，不仅赔了钱财，还伤了自己。
     * rank : 0
     * view_yesterday_count : 0
     * view_week_count : 0
     * comment_count : 109
     * favorite_count : 0
     * up_count : 382
     * down_count : 16
     * douban_num : 26974074
     * distributor : null
     * production : null
     * attr : {"director":[{"id":"900543","name":"周星星","link":"http://www.youku.com/star_page/uid_UMzYwMjE3Mg==.html"}],"performer":[{"id":"794277","name":"姜寒","character":"司马难","link":"http://www.youku.com/star_page/uid_UMzE3NzEwOA==.html"},{"id":"213822","name":"肖旭","character":"夏侯冤","link":"http://www.youku.com/star_page/uid_UODU1Mjg4.html"},{"id":"861691","name":"鄂博","character":"唐三","link":"http://www.youku.com/star_page/uid_UMzQ0Njc2NA==.html"},{"id":"870612","name":"衣云鹤","character":"外甥","link":"http://www.youku.com/star_page/uid_UMzQ4MjQ0OA==.html"},{"id":"815515","name":"何泓姗","character":"宇航队长","link":"http://www.youku.com/star_page/uid_UMzI2MjA2MA==.html"}],"screenwriter":[{"id":"900543","name":"周星星","link":"http://www.youku.com/star_page/uid_UMzYwMjE3Mg==.html"},{"id":"870612","name":"衣云鹤","link":"http://www.youku.com/star_page/uid_UMzQ4MjQ0OA==.html"},{"id":"900544","name":"彭旭","link":"http://www.youku.com/star_page/uid_UMzYwMjE3Ng==.html"},{"id":"900545","name":"李海超","link":"http://www.youku.com/star_page/uid_UMzYwMjE4MA==.html"}],"starring":null,"producer":null}
     * premium : {"onlinetime":"2017-02-10 00:00:00","offlinetime":"2017-05-10 00:00:00","permit_duration":"2","price":"300","pay_type":["vod","mon"]}
     * series : {"seriesid":"7824","showseq":"2","series":"醒醒吧"}
     */

    public String id;
    public String state;
    public String copyright_status;
    public String name;
    public String subtitle;
    public String link;
    public String play_link;
    public String poster;
    public String poster_large;
    public String thumbnail;
    public String thumbnail_large;
    public String genre;
    public String area;
    public int completed;
    public String episode_count;
    public String episode_collected;
    public String episode_updated;
    public String update_notice;
    public String view_count;
    public String score;
    public int paid;
    public String published;
    public String released;
    public String releasedate_mainland;
    public String createtime;
    public String category;
    public String description;
    public String rank;
    public String view_yesterday_count;
    public String view_week_count;
    public String comment_count;
    public String favorite_count;
    public String up_count;
    public String down_count;
    public String douban_num;
    public Object distributor;
    public Object production;
    public AttrBean attr;

    @Override
    public String toString() {
        return "YouKuShowDetail{" +
                "id='" + id + '\'' +
                ", state='" + state + '\'' +
                ", copyright_status='" + copyright_status + '\'' +
                ", name='" + name + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", link='" + link + '\'' +
                ", play_link='" + play_link + '\'' +
                ", poster='" + poster + '\'' +
                ", poster_large='" + poster_large + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", thumbnail_large='" + thumbnail_large + '\'' +
                ", genre='" + genre + '\'' +
                ", area='" + area + '\'' +
                ", completed=" + completed +
                ", episode_count='" + episode_count + '\'' +
                ", episode_collected='" + episode_collected + '\'' +
                ", episode_updated='" + episode_updated + '\'' +
                ", update_notice='" + update_notice + '\'' +
                ", view_count='" + view_count + '\'' +
                ", score='" + score + '\'' +
                ", paid=" + paid +
                ", published='" + published + '\'' +
                ", released='" + released + '\'' +
                ", releasedate_mainland='" + releasedate_mainland + '\'' +
                ", createtime='" + createtime + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", rank='" + rank + '\'' +
                ", view_yesterday_count='" + view_yesterday_count + '\'' +
                ", view_week_count='" + view_week_count + '\'' +
                ", comment_count='" + comment_count + '\'' +
                ", favorite_count='" + favorite_count + '\'' +
                ", up_count='" + up_count + '\'' +
                ", down_count='" + down_count + '\'' +
                ", douban_num='" + douban_num + '\'' +
                ", distributor=" + distributor +
                ", production=" + production +
                ", attr=" + attr +
                '}';
    }

    public static class AttrBean implements Serializable {
        /**
         * director : [{"id":"900543","name":"周星星","link":"http://www.youku.com/star_page/uid_UMzYwMjE3Mg==.html"}]
         * performer : [{"id":"794277","name":"姜寒","character":"司马难","link":"http://www.youku.com/star_page/uid_UMzE3NzEwOA==.html"},{"id":"213822","name":"肖旭","character":"夏侯冤","link":"http://www.youku.com/star_page/uid_UODU1Mjg4.html"},{"id":"861691","name":"鄂博","character":"唐三","link":"http://www.youku.com/star_page/uid_UMzQ0Njc2NA==.html"},{"id":"870612","name":"衣云鹤","character":"外甥","link":"http://www.youku.com/star_page/uid_UMzQ4MjQ0OA==.html"},{"id":"815515","name":"何泓姗","character":"宇航队长","link":"http://www.youku.com/star_page/uid_UMzI2MjA2MA==.html"}]
         * screenwriter : [{"id":"900543","name":"周星星","link":"http://www.youku.com/star_page/uid_UMzYwMjE3Mg==.html"},{"id":"870612","name":"衣云鹤","link":"http://www.youku.com/star_page/uid_UMzQ4MjQ0OA==.html"},{"id":"900544","name":"彭旭","link":"http://www.youku.com/star_page/uid_UMzYwMjE3Ng==.html"},{"id":"900545","name":"李海超","link":"http://www.youku.com/star_page/uid_UMzYwMjE4MA==.html"}]
         * starring : null
         * producer : null
         */

        public Object starring;
        public Object producer;
        public List<DirectorBean> director;
        public List<PerformerBean> performer;

        @Override
        public String toString() {
            return "AttrBean{" +
                    "starring=" + starring +
                    ", producer=" + producer +
                    ", director=" + director +
                    ", performer=" + performer +
                    '}';
        }

        public static class DirectorBean implements Serializable{
            /**
             * id : 900543
             * name : 周星星
             * link : http://www.youku.com/star_page/uid_UMzYwMjE3Mg==.html
             */

            public String id;
            public String name;
            public String link;

            @Override
            public String toString() {
                return "DirectorBean{" +
                        "id='" + id + '\'' +
                        ", name='" + name + '\'' +
                        ", link='" + link + '\'' +
                        '}';
            }
        }

        public static class PerformerBean implements Serializable{
            /**
             * id : 794277
             * name : 姜寒
             * character : 司马难
             * link : http://www.youku.com/star_page/uid_UMzE3NzEwOA==.html
             */

            public String id;
            public String name;
            public String character;
            public String link;

            @Override
            public String toString() {
                return "PerformerBean{" +
                        "id='" + id + '\'' +
                        ", name='" + name + '\'' +
                        ", character='" + character + '\'' +
                        ", link='" + link + '\'' +
                        '}';
            }
        }
    }
}
