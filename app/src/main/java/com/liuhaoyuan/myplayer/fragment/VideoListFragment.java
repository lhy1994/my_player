package com.liuhaoyuan.myplayer.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.liuhaoyuan.myplayer.R;
import com.liuhaoyuan.myplayer.activity.MusicPlayActivity;
import com.liuhaoyuan.myplayer.activity.VideoDetailActivity;
import com.liuhaoyuan.myplayer.domain.video.NetVideoInfo;
import com.liuhaoyuan.myplayer.domain.video.NetVideoItem;
import com.liuhaoyuan.myplayer.utils.ConstantValues;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by hyliu on 2017/1/22.
 */

public class VideoListFragment extends BaseFragment {
    private static final String APPKEY = "66c85262ac2869e8";
    private int mCurrentPage = 0;
    private String mVideoType;
    private ArrayList<NetVideoItem> mData;
    private XRecyclerView mRecyclerView;
    private VideoAdapter mAdapter;

    @Override
    public void loadData() {
        Bundle arguments = getArguments();
        mVideoType = arguments.getString(ConstantValues.VIDEO_TYPE);
        mCurrentPage = 0;
        getDataFromServer(true, false);
    }

    @Override
    public View onCreateSuccessView() {
        View view = View.inflate(getContext(), R.layout.fragment_video_list, null);
        mRecyclerView = (XRecyclerView) view.findViewById(R.id.lv_video);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mAdapter = new VideoAdapter();
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLoadingMoreEnabled(true);
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                refresh();
            }

            @Override
            public void onLoadMore() {
                loadMore();
            }
        });
        return view;
    }

    private void refresh() {
        mCurrentPage = 0;
        getDataFromServer(false, true);
    }

    private void loadMore() {
        getDataFromServer(false, false);
    }

    public void getDataFromServer(final boolean isInit, final boolean isRefresh) {
        RequestParams requestParams = new RequestParams("https://openapi.youku.com/v2/searches/show/top_unite.json");
        requestParams.addParameter("client_id", APPKEY);
        requestParams.addParameter("category", mVideoType);
        requestParams.addParameter("headnum", "1");
        requestParams.addParameter("tailnum", "1");
        requestParams.addParameter("page", (mCurrentPage + 1) + "");

        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                parseData(result, isInit, isRefresh);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("error", "get video error..............");
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void parseData(String result, boolean isInit, boolean isRefresh) {
        Gson gson = new Gson();
        NetVideoInfo netVideoInfo = gson.fromJson(result, NetVideoInfo.class);

        Toast.makeText(getContext(),netVideoInfo.toString(),Toast.LENGTH_LONG).show();
        Log.e("test",netVideoInfo.toString());
        if (netVideoInfo.data != null) {
            mCurrentPage++;
            if (isInit) {
                mData = netVideoInfo.data;
                onLoadingComplete(true);
            } else {
                if (isRefresh) {
                    mData = netVideoInfo.data;
                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.refreshComplete();
                } else {
                    mData.addAll(netVideoInfo.data);
                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.loadMoreComplete();
                }
            }
        } else {
            Toast.makeText(getContext(), "获取网络信息出错", Toast.LENGTH_LONG).show();
        }
    }

    private class VideoAdapter extends RecyclerView.Adapter<VideoViewHolder> {

        @Override
        public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_list, parent, false);
            VideoViewHolder holder = new VideoViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final VideoViewHolder holder, final int position) {
            final Bitmap[] pic = new Bitmap[1];

            ImageOptions.Builder builder = new ImageOptions.Builder();
            builder.setFailureDrawableId(R.drawable.nodata);
            ImageOptions imageOptions = builder.build();
            x.image().bind(holder.videoIv, mData.get(position).vpic, imageOptions, new Callback.CommonCallback<Drawable>() {
                @Override
                public void onSuccess(Drawable result) {
                    int defaultColor = getResources().getColor(R.color.pinkPrimary);
                    BitmapDrawable drawable = (BitmapDrawable) result;
                    Bitmap bitmap = drawable.getBitmap();
                    Palette palette = Palette.from(bitmap).generate();
                    int dominantColor = palette.getDominantColor(defaultColor);
                    holder.videoCardView.setCardBackgroundColor(palette.getDominantColor(dominantColor));
                    pic[0] =bitmap;
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
//            x.image().bind(holder.videoIv,mData.get(position).vpic);
            holder.videoTitleTv.setText(mData.get(position).name);

            StringBuilder directorBuilder = new StringBuilder();
            directorBuilder.append("导演:");
            for (String s : mData.get(position).director) {
                directorBuilder.append(" " + s);
            }
            holder.videoDirectorTv.setText(directorBuilder);

            StringBuilder actorBuilder = new StringBuilder();
            actorBuilder.append("演员:");
            for (String s : mData.get(position).performer) {
                actorBuilder.append(" " + s);
            }
            holder.videoActorTv.setText(actorBuilder.toString());

            holder.videoYearTv.setText("上映年代: " + mData.get(position).releaseYear);
            holder.videoTimeTv.setText("上映日期: " + mData.get(position).releaseDate);
//            holder.videoWatchCountTv.setText("浏览总量: "+mData.get(position).totalvv);

//            holder.videoBriefTv.setText(mData.get(position).brief);
            holder.videoMoreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), VideoDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(ConstantValues.VIDEO_INFO, mData.get(position));
                    bundle.putParcelable(ConstantValues.VIDEO_PIC, pic[0]);
                    intent.putExtras(bundle);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), holder.videoIv, getString(R.string.transition_video_pic));
                        startActivity(intent,options.toBundle());
                    }else {
                        startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

    private class VideoViewHolder extends RecyclerView.ViewHolder {

        private ImageView videoIv;
        private TextView videoTitleTv;
        private TextView videoBriefTv;
        private Button videoMoreBtn;
        private TextView videoActorTv;
        private TextView videoDirectorTv;
        private TextView videoYearTv;
        private TextView videoTimeTv;
        private TextView videoWatchCountTv;
        private CardView videoCardView;

        public VideoViewHolder(View itemView) {
            super(itemView);
            videoIv = (ImageView) itemView.findViewById(R.id.iv_video);
            videoTitleTv = (TextView) itemView.findViewById(R.id.tv_video_title);
            videoDirectorTv = (TextView) itemView.findViewById(R.id.tv_video_director);
            videoActorTv = (TextView) itemView.findViewById(R.id.tv_video_actor);
//            videoBriefTv = (TextView) itemView.findViewById(R.id.tv_video_brief);
            videoYearTv = (TextView) itemView.findViewById(R.id.tv_video_year);
            videoTimeTv = (TextView) itemView.findViewById(R.id.tv_video_time);
//            videoWatchCountTv = (TextView) itemView.findViewById(R.id.tv_video_watch_count);
            videoMoreBtn = (Button) itemView.findViewById(R.id.btn_video_more);
            videoCardView = (CardView) itemView.findViewById(R.id.card_video);
        }
    }
}
