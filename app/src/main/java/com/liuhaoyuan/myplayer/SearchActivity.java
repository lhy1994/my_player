package com.liuhaoyuan.myplayer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.liuhaoyuan.myplayer.domain.video.NetVideoAddress;
import com.liuhaoyuan.myplayer.domain.video.UrlResult;
import com.liuhaoyuan.myplayer.domain.video.VideoResultInfo;
import com.liuhaoyuan.myplayer.domain.video.VideoShowResultInfo;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private static final String APPKEY = "66c85262ac2869e8";
    private ArrayList<VideoResultInfo.VideoResult> videos=new ArrayList<>();
    private MyAdapter adapter;
    private ListView listView;
    private ListView showListView;
    private ArrayList<VideoShowResultInfo.VideoShowResult> shows=new ArrayList<>();
//    private MyShowAdapter showAdapter;
    private String queryString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.lv_search);
//        showListView = (ListView) findViewById(R.id.lv_search_show);

        SearchView searchView = (SearchView) findViewById(R.id.sv_search);
        searchView.setQueryHint("在这里输入关键词");
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                queryString = query;
                searchShow(query);
//                searchVideo(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void searchShow(String query) {
        RequestParams requestParams = new RequestParams("https://openapi.youku.com/v2/searches/show/by_keyword.json");
        requestParams.addParameter("client_id", APPKEY);
        requestParams.addParameter("keyword", query);
        requestParams.addParameter("unite", "1");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("show_result", result);
                parseShowResult(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    private void parseShowResult(String result) {
        Gson gson = new Gson();
        VideoShowResultInfo videoShowResultInfo = gson.fromJson(result, VideoShowResultInfo.class);
        shows = videoShowResultInfo.shows;

        searchVideo(queryString);
    }

    private void searchVideo(String query) {
        final RequestParams requestParams = new RequestParams("https://openapi.youku.com/v2/searches/video/by_keyword.json");
        requestParams.addParameter("client_id", APPKEY);
        requestParams.addParameter("keyword", query);
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("search_result", result);
                parseResult(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void parseResult(String result) {
        Gson gson = new Gson();
        VideoResultInfo videoResultInfo = gson.fromJson(result, VideoResultInfo.class);
        videos = videoResultInfo.videos;

        adapter = new MyAdapter();
        listView.setAdapter(adapter);
    }

    class MyAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return videos.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (position < shows.size()) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public Object getItem(int i) {
            if (getItemViewType(i) == 0) {
                return shows.get(i);
            } else {
                return videos.get(i - shows.size());
            }
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            Viewholder holder = null;
            int type = getItemViewType(position);

            if (view == null) {
                switch (type) {
                    case 0:
                        view = View.inflate(SearchActivity.this, R.layout.list_item_search_show, null);
                        holder = new Viewholder();
                        holder.imageViewShow = (ImageView) view.findViewById(R.id.iv_search_video_show);
                        holder.titleShow = (TextView) view.findViewById(R.id.tv_search_video_show_title);
                        holder.scoreShow = (TextView) view.findViewById(R.id.tv_search_video_show_score);
                        holder.categoryShow = (TextView) view.findViewById(R.id.tv_search_video_show_category);
                        holder.areaShow = (TextView) view.findViewById(R.id.tv_search_video_show_area);
                        holder.contentShow = (TextView) view.findViewById(R.id.tv_search_video_show_content);
                        holder.buttonShow = (Button) view.findViewById(R.id.btn_search_video_show);

                        view.setTag(holder);
                        break;
                    case 1:
                        view = View.inflate(SearchActivity.this, R.layout.list_item_search_video, null);
                        holder = new Viewholder();

                        holder.imageViewVideo = (ImageView) view.findViewById(R.id.iv_search_video);
                        holder.categoryVideo = (TextView) view.findViewById(R.id.tv_search_video_category);
//                        holder.tagVideo = (TextView) view.findViewById(R.id.tv_search_video_tags);
                        holder.titleVideo = (TextView) view.findViewById(R.id.tv_search_video_title);
                        holder.viewcountVideo = (TextView) view.findViewById(R.id.tv_search_video_viewcount);
                        holder.buttonVideo = (Button) view.findViewById(R.id.btn_search_video);
                        view.setTag(holder);
                        break;
                    default:
                        break;
                }
            } else {
                holder = (Viewholder) view.getTag();
            }

            switch (type){
                case 0:
                    x.image().bind(holder.imageViewShow, shows.get(position).poster);
                    holder.titleShow.setText(shows.get(position).name);
                    holder.categoryShow.setText("分类: " + shows.get(position).showcategory);
                    holder.areaShow.setText("地区: " + shows.get(position).area);
                    String score=shows.get(position).score;
                    holder.scoreShow.setText("评分: " +score.substring(0,score.indexOf(".")+2) );
                    holder.contentShow.setText("   " + shows.get(position).description);

                    holder.buttonShow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(SearchActivity.this, VideoListActivity.class);
                            intent.putExtra("video_id", shows.get(position).id);
                            startActivity(intent);
                        }
                    });
                    break;
                case 1:
                    x.image().bind(holder.imageViewVideo, videos.get(position-shows.size()).bigThumbnail);
                    holder.titleVideo.setText(videos.get(position-shows.size()).title);
                    holder.categoryVideo.setText("分类: " + videos.get(position-shows.size()).category);

//                    String tag = videos.get(position-shows.size()).tags;
//                    if (tag != null) {
//                        holder.tagVideo.setText("标签: " + videos.get(position-shows.size()).tags);
//                    }

                    holder.viewcountVideo.setText("观看次数: " + videos.get(position-shows.size()).view_count);

                    holder.buttonVideo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getRealUrl(videos.get(position-shows.size()).link);
                        }
                    });
                    break;
            }

            return view;
        }
    }

    private void getRealUrl(final String url) {
        RequestParams requestParams = new RequestParams("http://apis.baidu.com/dmxy/truevideourl/truevideourl");
        requestParams.addHeader("apikey", "052f6a481873bd14b533c8b45745a845");
        requestParams.addParameter("url", url);
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("url", url);
                Log.e("resulr", result);
                parseUrl(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void parseUrl(String result) {
        Gson gson = new Gson();
        UrlResult urlResult = gson.fromJson(result, UrlResult.class);
        if (!TextUtils.isEmpty(urlResult.mp4)) {
            Intent intent = new Intent(SearchActivity.this, VideoPlayActivity.class);
            intent.setData(Uri.parse(urlResult.mp4));
            startActivity(intent);
        } else {
            Log.e("error_real_url", "error..........");
        }
    }

    class Viewholder {
        ImageView imageViewVideo;
        TextView titleVideo;
        TextView categoryVideo;
//        TextView tagVideo;
        TextView viewcountVideo;
        Button buttonVideo;

        ImageView imageViewShow;
        TextView titleShow;
        TextView scoreShow;
        TextView categoryShow;
        TextView areaShow;
        TextView contentShow;
        Button buttonShow;
    }

}
