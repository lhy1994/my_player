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
import com.liuhaoyuan.myplayer.db.FavoriteDbManager;
import com.liuhaoyuan.myplayer.domain.video.NetVideoAddress;
import com.liuhaoyuan.myplayer.domain.video.YouKuShowDetail;
import com.liuhaoyuan.myplayer.utils.ConstantValues;
import com.liuhaoyuan.myplayer.utils.LogUtils;
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
    private TextView mVideoName;
    private TextView mScore;
    private TextView mDirectors;
    private TextView mActors;
    private TextView mTags;
    private TextView mViewCount;
    private TextView mPublishedTime;
    private TextView mContent;
    private String mVideoId;
    private static final String APPKEY = "66c85262ac2869e8";
    private ArrayList<NetVideoAddress.VideoAddress> addresses;
    private LinearLayout linearLayout;
    private YouKuShowDetail mVideoDetail;
    private TextView mArea;
    private TextView mCommentCount;
    private int mRequestCount = 0;
    private TextView mAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        imageView = (ImageView) findViewById(R.id.iv_net_video_detail);
        mVideoName = (TextView) findViewById(R.id.tv_net_video_detail_name);
        mScore = (TextView) findViewById(R.id.tv_net_video_detail_score);
        mArea = (TextView) findViewById(R.id.tv_net_video_detail_area);
        mDirectors = (TextView) findViewById(R.id.tv_net_video_detail_director);
        mActors = (TextView) findViewById(R.id.tv_net_video_detail_actor);
        mTags = (TextView) findViewById(R.id.tv_net_video_detail_tag);
        mViewCount = (TextView) findViewById(R.id.tv_net_video_detail_total);
        mCommentCount = (TextView) findViewById(R.id.tv_net_video_detail_comment);
        mPublishedTime = (TextView) findViewById(R.id.tv_net_video_detail_time);
        mContent = (TextView) findViewById(R.id.tv_net_video_detail_content);
        mAddress = (TextView) findViewById(R.id.tv_net_video_detail_more);

        linearLayout = (LinearLayout) findViewById(R.id.ll_net_video_detail);
        setTitle("");

        getVideoDeTail();
    }

    private void getVideoDeTail() {
        mVideoId = getIntent().getExtras().getString(ConstantValues.VIDEO_ID);
        RequestParams params = new RequestParams("https://openapi.youku.com/v2/shows/show.json");
        params.addParameter("client_id", APPKEY);
        params.addParameter("show_id", mVideoId);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                parseVideoDetail(result);
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

    private void parseVideoDetail(String result) {
        Gson gson = new Gson();
        mVideoDetail = gson.fromJson(result, YouKuShowDetail.class);
        if (mVideoDetail != null) {
            LogUtils.e(this, mVideoDetail.toString());
            initView();
        }
    }

    private void initView() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Bitmap bitmap = bundle.getParcelable(ConstantValues.VIDEO_PIC);
        if (bitmap == null) {
            x.image().bind(imageView, mVideoDetail.thumbnail_large);
        } else {
            imageView.setImageBitmap(bitmap);
        }
        setTextAndVisiblity(mVideoName,"",mVideoDetail.name);
        setTextAndVisiblity(mScore,"评分：",VideoUtils.formatScore(mVideoDetail.score));
        setTextAndVisiblity(mArea,"地区：",mVideoDetail.area);
        if (mVideoDetail.attr!=null){
            if (mVideoDetail.attr.director != null && mVideoDetail.attr.director.size() > 0) {
                StringBuilder directorBuilder = new StringBuilder();
                directorBuilder.append("导演:");
                for (YouKuShowDetail.AttrBean.DirectorBean bean : mVideoDetail.attr.director) {
                    directorBuilder.append(" " + bean.name);
                }
                mDirectors.setText(directorBuilder.toString());
            } else {
                mDirectors.setVisibility(View.GONE);
            }
            if (mVideoDetail.attr.performer != null && mVideoDetail.attr.performer.size() > 0) {
                StringBuilder actorBuilder = new StringBuilder();
                actorBuilder.append("演员:");
                for (YouKuShowDetail.AttrBean.PerformerBean bean : mVideoDetail.attr.performer) {
                    actorBuilder.append(" " + bean.name);
                }
                mActors.setText(actorBuilder.toString());
            } else {
                mActors.setVisibility(View.GONE);
            }
        }
        setTextAndVisiblity(mTags,"分类：",mVideoDetail.genre);
        setTextAndVisiblity(mViewCount,"浏览总量: ",VideoUtils.formatNum(mVideoDetail.view_count));
        setTextAndVisiblity(mCommentCount,"评论数：",VideoUtils.formatNum(mVideoDetail.comment_count));
        setTextAndVisiblity(mPublishedTime,"上映时间: ",mVideoDetail.published);
        setTextAndVisiblity(mContent,"     ",mVideoDetail.description);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FavoriteDbManager dbUtils = FavoriteDbManager.getInstance(VideoDetailActivity.this);
                dbUtils.insertVideo(mVideoDetail);
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

        mAddress.setVisibility(View.INVISIBLE);
        getVideoAddress();
    }

    private void setTextAndVisiblity(TextView view,String extr ,String text){
        if (!TextUtils.isEmpty(text)){
            view.setText(extr+text);
        }else {
            view.setVisibility(View.GONE);
        }
    }

    private void getVideoAddress() {
        mRequestCount++;
        RequestParams requestParams = new RequestParams("https://openapi.youku.com/v2/searches/show/address_unite.json");
        requestParams.addParameter("client_id", APPKEY);
        requestParams.addParameter("progammeId", mVideoId);
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                int pos1 = result.indexOf("\"");
                int pos2 = result.indexOf("\"", pos1 + 1);
                String target = result.substring(pos1 + 1, pos2);
                result = result.replace(target, "data");

                Log.e("url", "https://openapi.youku.com/v2/searches/show/address_unite.json?" + "client_id=" + APPKEY + "&progammeId=" + mVideoId);
                parseVideoAddress(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("address_error", "error...........");
                if (mRequestCount < 5) {
                    getVideoAddress();
                } else {
                    Toast.makeText(VideoDetailActivity.this, "无播放站点", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void parseVideoAddress(String result) {
        Gson gson = new Gson();
        NetVideoAddress netVideoAddress = gson.fromJson(result, NetVideoAddress.class);
        ArrayList<NetVideoAddress.AddressInfo> infos = netVideoAddress.data;
        if (infos!=null && infos.size()>0){
            mAddress.setVisibility(View.VISIBLE);
        }
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