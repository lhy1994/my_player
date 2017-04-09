package com.liuhaoyuan.myplayer.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.liuhaoyuan.myplayer.activity.VideoDetailActivity;
import com.liuhaoyuan.myplayer.domain.video.YouKuShowInfo;
import com.liuhaoyuan.myplayer.utils.ConstantValues;
import com.liuhaoyuan.myplayer.utils.ImageUtils;
import com.liuhaoyuan.myplayer.utils.VideoUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

/**
 * Created by hyliu on 2017/1/22.
 */

public class VideoListFragment extends BaseFragment {
    private static final String APPKEY = "66c85262ac2869e8";
    private int mCurrentPage = 0;
    private String mVideoType;
    private List<YouKuShowInfo.ShowsBean> mData;
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
        RequestParams requestParams = new RequestParams("https://openapi.youku.com/v2/shows/by_category.json");
        requestParams.addParameter("client_id", APPKEY);
        requestParams.addParameter("category", mVideoType);
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
        YouKuShowInfo youKuShowInfo=gson.fromJson(result,YouKuShowInfo.class);
        Log.e(getClass().getSimpleName(), "parseData: "+youKuShowInfo.getShows().get(0).getId());
        if (youKuShowInfo.getShows() != null) {
            mCurrentPage++;
            if (isInit) {
                mData = youKuShowInfo.getShows();
                onLoadingComplete(true);
            } else {
                if (isRefresh) {
                    mData.clear();
                    mData = youKuShowInfo.getShows();
                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.refreshComplete();
                } else {
                    mData.addAll(youKuShowInfo.getShows());
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
            return new VideoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final VideoViewHolder holder, final int position) {
            final YouKuShowInfo.ShowsBean data = mData.get(position);
            final Bitmap[] pic = new Bitmap[1];
            ImageOptions.Builder builder = new ImageOptions.Builder();
            builder.setFailureDrawableId(R.drawable.nodata);
            ImageOptions imageOptions = builder.build();
            x.image().bind(holder.videoIv, data.getBigthumbnail(), imageOptions, new Callback.CommonCallback<Drawable>() {
                @Override
                public void onSuccess(Drawable result) {
                    BitmapDrawable drawable = (BitmapDrawable) result;
                    Bitmap bitmap = drawable.getBitmap();
                    ImageUtils.getDominantColor(bitmap, new ImageUtils.PaletteCallBack() {
                        @Override
                        public void onColorGenerated(int color, int textColor) {
                            holder.videoCardView.setCardBackgroundColor(color);
                            holder.videoScoreTv.setTextColor(textColor);
                            holder.commentCountTv.setTextColor(textColor);
                            holder.videoDateTV.setTextColor(textColor);
                            holder.videoTitleTv.setTextColor(textColor);
                            holder.viewCountTv.setTextColor(textColor);
                        }
                    });
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
            holder.videoTitleTv.setText(data.getName());
            holder.videoScoreTv.setText("评分："+ VideoUtils.formatScore(data.getScore()));
            holder.viewCountTv.setText("观看次数："+VideoUtils.formatNum(data.getView_count()));
            holder.commentCountTv.setText("评论数: " + VideoUtils.formatNum(data.getComment_count()));
            holder.videoDateTV.setText("上映日期: " + data.getPublished());
            holder.videoMoreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), VideoDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(ConstantValues.VIDEO_ID, data.getId());

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
        private Button videoMoreBtn;
        private TextView viewCountTv;
        private TextView videoScoreTv;
        private TextView commentCountTv;
        private TextView videoDateTV;
        private CardView videoCardView;

        public VideoViewHolder(View itemView) {
            super(itemView);
            videoIv = (ImageView) itemView.findViewById(R.id.iv_video);
            videoTitleTv = (TextView) itemView.findViewById(R.id.tv_video_title);
            videoScoreTv = (TextView) itemView.findViewById(R.id.tv_video_score);
            viewCountTv = (TextView) itemView.findViewById(R.id.tv_video_view_count);
            commentCountTv = (TextView) itemView.findViewById(R.id.tv_video_comment_count);
            videoDateTV = (TextView) itemView.findViewById(R.id.tv_video_date);
            videoMoreBtn = (Button) itemView.findViewById(R.id.btn_video_more);
            videoCardView = (CardView) itemView.findViewById(R.id.card_video);
        }
    }
}
