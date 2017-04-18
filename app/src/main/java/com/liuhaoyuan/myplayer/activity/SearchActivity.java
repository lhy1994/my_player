package com.liuhaoyuan.myplayer.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.liuhaoyuan.myplayer.APP;
import com.liuhaoyuan.myplayer.R;
import com.liuhaoyuan.myplayer.api.DataObserver;
import com.liuhaoyuan.myplayer.api.MusicApi;
import com.liuhaoyuan.myplayer.aidl.Song;
import com.liuhaoyuan.myplayer.domain.video.UrlResult;
import com.liuhaoyuan.myplayer.domain.video.VideoResultInfo;
import com.liuhaoyuan.myplayer.domain.video.VideoShowResultInfo;
import com.liuhaoyuan.myplayer.utils.ConstantValues;
import com.liuhaoyuan.myplayer.utils.MusicUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private static final String APPKEY = "66c85262ac2869e8";
    private ArrayList<VideoResultInfo.VideoResult> videos = new ArrayList<>();
    private ArrayList<VideoShowResultInfo.VideoShowResult> shows = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        int currentTheme = APP.getCurrentTheme();
        APP application = (APP) getApplication();
        int currentTheme = application.getCurrentTheme();
        setTheme(currentTheme);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        SearchView searchView = (SearchView) findViewById(R.id.search);
        searchView.setSubmitButtonEnabled(true);

        Intent intent = getIntent();
        final int fragmentId = intent.getIntExtra(ConstantValues.FRAGMENT_ID, R.id.nav_music);
        String query = intent.getStringExtra(ConstantValues.SEARCH_STRING);
        if (!TextUtils.isEmpty(query)) {
            if (fragmentId == R.id.nav_music) {
                doMusicSearch(query);
            } else if (fragmentId == R.id.nav_video) {
                doVideoSearch(query);
            }
        }else {
            searchView.setIconified(false);
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (fragmentId == R.id.nav_music) {
                    doMusicSearch(query);
                } else if (fragmentId == R.id.nav_video) {
                    doVideoSearch(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void doVideoSearch(String queryString) {
        searchShow(queryString);
    }

    private void doMusicSearch(String queryString) {
        MusicApi qqMusicApi = MusicApi.getInstance();
        qqMusicApi.registerObserver(new DataObserver<ArrayList<Song>>(DataObserver.TYPE_SONG) {
            @Override
            public void onComplete(ArrayList<Song> data) {
                if (data != null) {
                    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.lv_search);
                    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(SearchActivity.this, 2);
                    MusicAdapter musicAdapter = new MusicAdapter(data);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(musicAdapter);
                }
            }
        });
        qqMusicApi.getSong(queryString, "1");
    }

    private class MusicAdapter extends RecyclerView.Adapter<MusicViewHolder> {

        private ArrayList<Song> data;

        private MusicAdapter(ArrayList<Song> data) {
            this.data = data;
        }

        @Override
        public MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hotmusic, parent, false);
            MusicViewHolder musicViewHolder = new MusicViewHolder(view);
            return musicViewHolder;
        }

        @Override
        public void onBindViewHolder(MusicViewHolder holder, final int position) {
            ImageOptions.Builder builder = new ImageOptions.Builder();
            builder.setFailureDrawableId(R.drawable.music_fail);
            ImageOptions imageOptions = builder.build();
            x.image().bind(holder.imageView, data.get(position).albumpic_small, imageOptions);
            holder.nameTv.setText(data.get(position).songname);
            holder.singerTv.setText(data.get(position).singername);
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    PopupMenu popupMenu = new PopupMenu(SearchActivity.this, v);
                    popupMenu.inflate(R.menu.popup_hotmusic);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_play:
                                    MusicUtils.playMusic(SearchActivity.this, position, true, data);
                                    break;
                                case R.id.menu_favorite:
                                    final Snackbar snackbar = Snackbar.make(v, "未实现", Snackbar.LENGTH_LONG);
                                    snackbar.setAction("知道了", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            snackbar.dismiss();
                                        }
                                    });
                                    snackbar.show();
                                    break;
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                }
            });
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MusicUtils.playMusic(SearchActivity.this, position, true, data);
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private class MusicViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView nameTv;
        private TextView singerTv;
        private AppCompatButton button;
        private CardView cardView;

        public MusicViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_hotmusic);
            nameTv = (TextView) itemView.findViewById(R.id.tv_hotmusic_name);
            singerTv = (TextView) itemView.findViewById(R.id.tv_hotmusic_singer);
            button = (AppCompatButton) itemView.findViewById(R.id.btn_hotmusic_more);
            cardView = (CardView) itemView.findViewById(R.id.card_hotmusic);
        }
    }

    private void searchShow(final String query) {
        RequestParams requestParams = new RequestParams("https://openapi.youku.com/v2/searches/show/by_keyword.json");
        requestParams.addParameter("client_id", APPKEY);
        requestParams.addParameter("keyword", query);
        requestParams.addParameter("unite", "1");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("show_result", result);
                parseShowResult(result, query);
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

    private void parseShowResult(String result, String queryString) {
        Gson gson = new Gson();
        VideoShowResultInfo videoShowResultInfo = gson.fromJson(result, VideoShowResultInfo.class);
        if (shows != null) {
            shows = videoShowResultInfo.shows;
        }

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
        if (videos != null) {
            videos = videoResultInfo.videos;
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.lv_search);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        SearchVideoAdapter adapter = new SearchVideoAdapter();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private class SearchVideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public int getItemViewType(int position) {
            if (position < shows.size()) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 0) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_show, parent, false);
                ShowViewHolder showViewHolder = new ShowViewHolder(view);
                return showViewHolder;
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_video, parent, false);
                VideoViewHolder videoViewHolder = new VideoViewHolder(view);
                return videoViewHolder;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            if (getItemViewType(position) == 0) {
                ShowViewHolder viewHolder = (ShowViewHolder) holder;
                viewHolder.titleTv.setText(shows.get(position).name);
                viewHolder.areaTV.setText("地区: " + shows.get(position).area);
                viewHolder.categoryTV.setText("分类: " + shows.get(position).showcategory);
                viewHolder.desTv.setText(shows.get(position).description);
                x.image().bind(viewHolder.showIv, shows.get(position).bigPoster);
                viewHolder.showBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SearchActivity.this, VideoDetailActivity.class);
//                        intent.putExtra("video_id", shows.get(position).id);
                        Bundle bundle = new Bundle();
                        bundle.putString(ConstantValues.VIDEO_ID, shows.get(position).id);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            } else {
                VideoViewHolder viewHolder = (VideoViewHolder) holder;
                viewHolder.videoTitleTv.setText(videos.get(position - shows.size()).title);
                viewHolder.videoCategoryTv.setText("分类: " + videos.get(position - shows.size()).category);
                String tags = videos.get(position - shows.size()).tags;
                viewHolder.videoTagTv.setText("标签: " + tags);
                x.image().bind(viewHolder.videoIv, videos.get(position - shows.size()).bigThumbnail);
                viewHolder.videoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getRealUrl(videos.get(position - shows.size()).link);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return shows.size() + videos.size();
        }
    }

    private class ShowViewHolder extends RecyclerView.ViewHolder {
        ImageView showIv;
        TextView titleTv;
        TextView categoryTV;
        TextView areaTV;
        TextView desTv;
        Button showBtn;

        public ShowViewHolder(View itemView) {
            super(itemView);
            showIv = (ImageView) itemView.findViewById(R.id.iv_search_video_show);
            titleTv = (TextView) itemView.findViewById(R.id.tv_search_video_show_title);
            categoryTV = (TextView) itemView.findViewById(R.id.tv_search_video_show_category);
            areaTV = (TextView) itemView.findViewById(R.id.tv_search_video_show_area);
            desTv = (TextView) itemView.findViewById(R.id.tv_search_video_show_content);
            showBtn = (Button) itemView.findViewById(R.id.btn_search_video_show);
        }
    }

    private class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView videoIv;
        TextView videoTitleTv;
        TextView videoCategoryTv;
        TextView videoTagTv;
        Button videoBtn;

        public VideoViewHolder(View itemView) {
            super(itemView);
            videoIv = (ImageView) itemView.findViewById(R.id.iv_search_video);
            videoTitleTv = (TextView) itemView.findViewById(R.id.tv_search_video_title);
            videoCategoryTv = (TextView) itemView.findViewById(R.id.tv_search_video_category);
            videoTagTv = (TextView) itemView.findViewById(R.id.tv_search_video_tag);
            videoBtn = (Button) itemView.findViewById(R.id.btn_search_video);
        }
    }

    private void getRealUrl(final String url) {
        RequestParams requestParams = new RequestParams("http://api.hi189.net/index.php");
        requestParams.addParameter("apikey", "2CF95AA17FE12415A1137A1354F4EA4F");
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
