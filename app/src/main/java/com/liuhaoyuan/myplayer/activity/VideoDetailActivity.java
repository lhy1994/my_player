package com.liuhaoyuan.myplayer.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.AutoTransition;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.liuhaoyuan.myplayer.APP;
import com.liuhaoyuan.myplayer.R;
import com.liuhaoyuan.myplayer.TestActivity;
import com.liuhaoyuan.myplayer.db.FavoriteDbUtils;
import com.liuhaoyuan.myplayer.domain.video.NetVideoAddress;
import com.liuhaoyuan.myplayer.domain.video.NetVideoItem;
import com.liuhaoyuan.myplayer.utils.ConstantValues;
import com.liuhaoyuan.myplayer.utils.VideoUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by hyliu on 2017/1/26.
 */

public class VideoDetailActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView name;
    private TextView status;
    private TextView score;
    private TextView director;
    private TextView actor;
    private TextView tag;
    private TextView total;
    private TextView year;
    private TextView time;
    private TextView content;
    private NetVideoItem videoInfo;
    private static final String APPKEY = "66c85262ac2869e8";
    private ArrayList<NetVideoAddress.VideoAddress> addresses;
    private int mRequestCount = 0;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        int currentTheme = APP.getCurrentTheme();
        APP application = (APP) getApplication();
        int currentTheme = application.getCurrentTheme();
        setTheme(currentTheme);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        // 设置一个exit transition
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setAllowEnterTransitionOverlap(true);
            getWindow().setExitTransition(new AutoTransition());//new Slide()  new Fade()
            getWindow().setEnterTransition(new AutoTransition());
            getWindow().setSharedElementEnterTransition(new AutoTransition());
            getWindow().setSharedElementExitTransition(new AutoTransition());
        }
        setContentView(R.layout.activity_video_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initView();
        initData();
    }

    private void initView() {
        imageView = (ImageView) findViewById(R.id.iv_net_video_detail);
        name = (TextView) findViewById(R.id.tv_net_video_detail_name);
        status = (TextView) findViewById(R.id.tv_net_video_detail_status);
        score = (TextView) findViewById(R.id.tv_net_video_detail_score);
        director = (TextView) findViewById(R.id.tv_net_video_detail_director);
        actor = (TextView) findViewById(R.id.tv_net_video_detail_actor);
        tag = (TextView) findViewById(R.id.tv_net_video_detail_tag);
        total = (TextView) findViewById(R.id.tv_net_video_detail_total);
        year = (TextView) findViewById(R.id.tv_net_video_detail_year);
        time = (TextView) findViewById(R.id.tv_net_video_detail_time);
        content = (TextView) findViewById(R.id.tv_net_video_detail_content);

        linearLayout = (LinearLayout) findViewById(R.id.ll_net_video_detail);
        setTitle("");
    }

    private void initData() {
        mRequestCount=0;
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        videoInfo = (NetVideoItem) bundle.getSerializable(ConstantValues.VIDEO_INFO);

        Bitmap bitmap = bundle.getParcelable(ConstantValues.VIDEO_PIC);
        if (bitmap == null) {
            x.image().bind(imageView, videoInfo.vpic);
        } else {
            imageView.setImageBitmap(bitmap);
        }
        name.setText(videoInfo.name);
        if (!TextUtils.isEmpty(videoInfo.displaystatus)) {
            status.setText(videoInfo.displaystatus);
        }
        score.setText("评分 " + videoInfo.score.substring(0, videoInfo.score.indexOf(".") + 2));

        StringBuilder directorBuilder = new StringBuilder();
        directorBuilder.append("导演:");
        for (String s : videoInfo.director) {
            directorBuilder.append(" " + s);
        }
        director.setText(directorBuilder.toString());

        StringBuilder actorBuilder = new StringBuilder();
        actorBuilder.append("演员:");
        for (String s : videoInfo.performer) {
            actorBuilder.append(" " + s);
        }
        actor.setText(actorBuilder.toString());

        StringBuilder tagBuilder = new StringBuilder();
        tagBuilder.append("标签:");
        for (String s : videoInfo.genre) {
            tagBuilder.append(" " + s);
        }
        tag.setText(tagBuilder.toString());

        total.setText("浏览总量: " + videoInfo.totalvv);
        year.setText("上映年代: " + videoInfo.releaseYear);
        time.setText("上映时间: " + videoInfo.releaseDate);
        content.setText("    " + videoInfo.brief);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FavoriteDbUtils dbUtils = FavoriteDbUtils.getInstance(VideoDetailActivity.this);
                dbUtils.insertVideo(videoInfo);
                final Snackbar snackbar = Snackbar.make(view, "收藏成功", Snackbar.LENGTH_LONG);
                snackbar.setAction("知道了", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });
                snackbar.show();
            }
        });

        getDataFromServer();
    }

    private void getDataFromServer() {
        mRequestCount++;
        final String proID = videoInfo.programmeId;
        RequestParams requestParams = new RequestParams("https://openapi.youku.com/v2/searches/show/address_unite.json");
        requestParams.addParameter("client_id", APPKEY);
        requestParams.addParameter("progammeId", proID);
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                int pos1 = result.indexOf("\"");
                int pos2 = result.indexOf("\"", pos1 + 1);
                String target = result.substring(pos1 + 1, pos2);
                result = result.replace(target, "data");

                Log.e("url", "https://openapi.youku.com/v2/searches/show/address_unite.json?" + "client_id=" + APPKEY + "&progammeId=" + proID);
                //                Log.e("result", result);
                parseData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("address_error", "error...........");
//                if (mRequestCount < 8) {
//                    getDataFromServer();
//                } else {
//                    Toast.makeText(VideoDetailActivity.this, "无播放站点", Toast.LENGTH_LONG).show();
//                }
                getDataFromServer();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void parseData(String result) {
        Gson gson = new Gson();
        NetVideoAddress netVideoAddress = gson.fromJson(result, NetVideoAddress.class);
        //        Log.e("data",netVideoAddress.toString());
        ArrayList<NetVideoAddress.AddressInfo> infos = netVideoAddress.data;
        for (final NetVideoAddress.AddressInfo info : infos) {
            AppCompatButton button = new AppCompatButton(this);
            button.setText(VideoUtils.getVideoSiteName(info.source_site));
            button.setBackgroundColor(Color.TRANSPARENT);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(VideoDetailActivity.this, VideoListActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("address", info.addresses);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            linearLayout.addView(button);
        }

//        if (infos != null && infos.size() > 0) {
//            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.lv_sites);
////            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(VideoDetailActivity.this,LinearLayoutManager.HORIZONTAL,false);
//            RecyclerView.LayoutManager layoutManager=new GridLayoutManager(VideoDetailActivity.this,4);
//            layoutManager.setAutoMeasureEnabled(true);
//
//            SitesAdapter adapter = new SitesAdapter(infos);
//            recyclerView.setLayoutManager(layoutManager);
//            recyclerView.setNestedScrollingEnabled(false);
//            recyclerView.setAdapter(adapter);
//        }
    }

    private class SitesAdapter extends RecyclerView.Adapter<SimpleViewHolder> {

        private ArrayList<NetVideoAddress.AddressInfo> data;

        private SitesAdapter(ArrayList<NetVideoAddress.AddressInfo> data) {
            this.data = data;
        }

        @Override
        public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_simple, parent, false);
            SimpleViewHolder simpleViewHolder = new SimpleViewHolder(view);
            return simpleViewHolder;
        }

        @Override
        public void onBindViewHolder(SimpleViewHolder holder, final int position) {
//            holder.nameTv.setText(VideoUtils.getVideoSiteName(data.get(position).source_site));
            holder.button.setText(VideoUtils.getVideoSiteName(data.get(position).source_site));
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(VideoDetailActivity.this, VideoListActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("address", data.get(position).addresses);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private class SimpleViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTv;
        private Button button;

        public SimpleViewHolder(View itemView) {
            super(itemView);
//            nameTv = (TextView) itemView.findViewById(R.id.tv_simple);
            button = (Button) itemView.findViewById(R.id.btn_simple);
        }
    }
}