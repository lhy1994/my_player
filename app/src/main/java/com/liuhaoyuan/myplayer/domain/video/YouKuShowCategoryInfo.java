package com.liuhaoyuan.myplayer.domain.video;

import java.util.List;

/**
 * Created by liuhaoyuan on 17/4/4.
 */

public class YouKuShowCategoryInfo {

    private List<CategoriesBean> categories;

    public List<CategoriesBean> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoriesBean> categories) {
        this.categories = categories;
    }

    public static class CategoriesBean {
        /**
         * term : Movies
         * label : 电影
         * lang : zh_CN
         * genre : [{"term":"drama","label":"剧情","lang":"zh_CN"},{"term":"comedy","label":"喜剧","lang":"zh_CN"},{"term":"romance","label":"爱情","lang":"zh_CN"},{"term":"action","label":"动作","lang":"zh_CN"},{"term":"thriller","label":"惊悚","lang":"zh_CN"},{"term":"documentary","label":"纪录片","lang":"zh_CN"},{"term":"crime","label":"犯罪","lang":"zh_CN"},{"term":"horror","label":"恐怖","lang":"zh_CN"},{"term":"animation","label":"动画","lang":"zh_CN"},{"term":"fantasy","label":"奇幻","lang":"zh_CN"},{"term":"adventure","label":"冒险","lang":"zh_CN"},{"term":"mystery","label":"悬疑","lang":"zh_CN"},{"term":"science-fiction","label":"科幻","lang":"zh_CN"},{"term":"musical","label":"歌舞","lang":"zh_CN"},{"term":"war","label":"战争","lang":"zh_CN"},{"term":"western","label":"西部","lang":"zh_CN"},{"term":"biography","label":"传记","lang":"zh_CN"},{"term":"history","label":"历史","lang":"zh_CN"},{"term":"opera","label":"戏曲","lang":"zh_CN"},{"term":"martial","label":"武侠","lang":"zh_CN"},{"term":"children","label":"儿童","lang":"zh_CN"},{"term":"short","label":"短片","lang":"zh_CN"},{"term":"police-drama","label":"警匪","lang":"zh_CN"},{"term":"sports","label":"运动","lang":"zh_CN"},{"term":"youku-original","label":"优酷出品","lang":"zh_CN"}]
         */

        private String term;
        private String label;
        private String lang;
        private List<GenreBean> genre;

        public String getTerm() {
            return term;
        }

        public void setTerm(String term) {
            this.term = term;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getLang() {
            return lang;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }

        public List<GenreBean> getGenre() {
            return genre;
        }

        public void setGenre(List<GenreBean> genre) {
            this.genre = genre;
        }

        public static class GenreBean {
            /**
             * term : drama
             * label : 剧情
             * lang : zh_CN
             */

            private String term;
            private String label;
            private String lang;

            public String getTerm() {
                return term;
            }

            public void setTerm(String term) {
                this.term = term;
            }

            public String getLabel() {
                return label;
            }

            public void setLabel(String label) {
                this.label = label;
            }

            public String getLang() {
                return lang;
            }

            public void setLang(String lang) {
                this.lang = lang;
            }
        }
    }
}
