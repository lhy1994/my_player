package com.liuhaoyuan.myplayer;

import android.content.Intent;
import android.graphics.Color;
import android.icu.text.IDNA;
import android.icu.text.StringPrepParseException;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import com.liuhaoyuan.myplayer.domain.video.NetVideoAddress;
import com.liuhaoyuan.myplayer.domain.video.NetVideoItem;
import com.liuhaoyuan.myplayer.utils.MyJsonUtils;
import com.liuhaoyuan.myplayer.utils.VideoUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;

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
    private AppCompatButton button;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VideoDetailActivity.this, TestActivity.class);
                startActivity(intent);
            }
        });

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
        button = (AppCompatButton) findViewById(R.id.btn_net_video_detail_more);

        linearLayout = (LinearLayout) findViewById(R.id.ll_net_video_detail);
    }

    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        videoInfo = (NetVideoItem) bundle.getSerializable("video_info");

        setTitle("");
        x.image().bind(imageView, videoInfo.pic);
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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(VideoDetailActivity.this, VideoListActivity.class);
                in.putExtra("video_id", videoInfo.programmeId);
                startActivity(in);
            }
        });

        getDataFromServer();
    }

    private void getDataFromServer() {
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
            Log.e("info", info.toString());

            AppCompatButton button=new AppCompatButton(this);
            button.setText(VideoUtils.getVideoSiteName(info.source_site));
            button.setBackgroundColor(Color.TRANSPARENT);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(VideoDetailActivity.this,VideoListActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("address",info.addresses);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            linearLayout.addView(button);
        }
    }
}
