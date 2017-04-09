package com.liuhaoyuan.myplayer.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.liuhaoyuan.myplayer.R;
import com.liuhaoyuan.myplayer.activity.SingerDetailActivity;
import com.liuhaoyuan.myplayer.api.DataObserver;
import com.liuhaoyuan.myplayer.api.MusicApi;
import com.liuhaoyuan.myplayer.domain.music.XiaMiSongInfo;
import com.liuhaoyuan.myplayer.utils.ConstantValues;
import com.liuhaoyuan.myplayer.utils.ImageUtils;

import org.xutils.common.Callback;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by hyliu on 2017/1/15.
 */

public class SingerFragment extends BaseFragment {
    private ArrayList<XiaMiSongInfo> mData;
    private SingerAdapter mAdapter;
    private int currentPage = 1;
    private XRecyclerView recyclerView;
    private String[] aliTopIds;

    @Override
    public void loadData() {
        final MusicApi qqMusicApi = MusicApi.getInstance();
        aliTopIds = qqMusicApi.getAliTopIds();
        qqMusicApi.registerObserver(new DataObserver<ArrayList<XiaMiSongInfo>>(DataObserver.TYPE_ALI_RANKS) {
            @Override
            public void onComplete(ArrayList<XiaMiSongInfo> data) {
                if (data != null) {
                    mData = getSingerNoRepeat(data);
                    onLoadingComplete(true);
                } else {
                    onLoadingComplete(false);
                }
                qqMusicApi.unregisterObserver(this);
            }
        });
        qqMusicApi.getAliRanks(aliTopIds[0], currentPage + "");
    }

    @Override
    public View onCreateSuccessView() {
        View view = View.inflate(getContext(), R.layout.fragment_singer, null);
        recyclerView = (XRecyclerView) view.findViewById(R.id.lv_singer);
        final RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        mAdapter = new SingerAdapter(mData);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLoadingMoreEnabled(true);
        recyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallSpinFadeLoader);
        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                refresh();
            }

            @Override
            public void onLoadMore() {
                loadingMore();
            }
        });
        return view;
    }

    private void refresh() {
        final MusicApi qqMusicApi = MusicApi.getInstance();
        qqMusicApi.registerObserver(new DataObserver<ArrayList<XiaMiSongInfo>>(DataObserver.TYPE_ALI_RANKS) {
            @Override
            public void onComplete(ArrayList<XiaMiSongInfo> data) {
                if (data != null) {
                    mData = getSingerNoRepeat(data);
                    mAdapter.setData(mData);
                    mAdapter.notifyDataSetChanged();
                    recyclerView.refreshComplete();
                }
                qqMusicApi.unregisterObserver(this);
            }
        });
        currentPage = 1;
        qqMusicApi.getAliRanks(aliTopIds[0], currentPage + "");
    }

    private void loadingMore() {
        final MusicApi qqMusicApi = MusicApi.getInstance();
        qqMusicApi.registerObserver(new DataObserver<ArrayList<XiaMiSongInfo>>(DataObserver.TYPE_ALI_RANKS) {
            @Override
            public void onComplete(ArrayList<XiaMiSongInfo> data) {
                if (data != null) {
                    mData.addAll(getSingerNoRepeat(data));
                    mAdapter.setData(mData);
                    mAdapter.notifyDataSetChanged();
                    currentPage++;
                    recyclerView.loadMoreComplete();
                }
                qqMusicApi.unregisterObserver(this);
            }
        });
        qqMusicApi.getAliRanks(aliTopIds[0], (currentPage + 1) + "");
    }

    private ArrayList<XiaMiSongInfo> getSingerNoRepeat(ArrayList<XiaMiSongInfo> list) {
        ArrayList<XiaMiSongInfo> data = new ArrayList<>();
        HashMap<String, XiaMiSongInfo> hashMap = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            XiaMiSongInfo songInfo = list.get(i);
            if (!TextUtils.isEmpty(songInfo.artistId)) {
                if (TextUtils.isEmpty(songInfo.artistLogo)) {
                    songInfo.artistLogo = songInfo.albumLogo;
                }
                hashMap.put(songInfo.artistId, songInfo);
            }
        }
        ;
        Collection<XiaMiSongInfo> values = hashMap.values();
        data.addAll(values);
        return data;
    }

    private class SingerAdapter extends RecyclerView.Adapter<SingerViewHolder> {

        private ArrayList<XiaMiSongInfo> data;

        public SingerAdapter(ArrayList<XiaMiSongInfo> data) {
            this.data = data;
        }

        public void setData(ArrayList<XiaMiSongInfo> data) {
            this.data = data;
        }

        @Override
        public SingerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_singer, parent, false);
            SingerViewHolder singerViewHolder = new SingerViewHolder(view);
            return singerViewHolder;
        }

        @Override
        public void onBindViewHolder(final SingerViewHolder holder, final int position) {
//            final Bitmap[] bitmap = new Bitmap[1];
            ImageOptions.Builder builder = new ImageOptions.Builder();
            builder.setFailureDrawableId(R.drawable.music_fail);
            ImageOptions options = builder.build();
            x.image().bind(holder.imageView, data.get(position).artistLogo, options, new Callback.CommonCallback<Drawable>() {
                @Override
                public void onSuccess(Drawable result) {
                    BitmapDrawable drawable = (BitmapDrawable) result;
                    Bitmap bitmap = drawable.getBitmap();
                    ImageUtils.getDominantColor(bitmap, new ImageUtils.PaletteCallBack() {
                        @Override
                        public void onColorGenerated(int color, int textColor) {
                            holder.textView.setBackgroundColor(color);
                            holder.textView.setTextColor(textColor);
                        }
                    });
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
            String artistName = data.get(position).artistName;
            int index = artistName.indexOf("-");
            if (index != -1) {
                artistName = artistName.substring(0, index);
            }
            holder.textView.setText(artistName);
            final String finalArtistName = artistName;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), SingerDetailActivity.class);
                    intent.putExtra(ConstantValues.ARTIST_NAME, finalArtistName);
                    intent.putExtra(ConstantValues.ARTIST_ID, data.get(position).artistId);
                    intent.putExtra(ConstantValues.ARTIST_LOGO, data.get(position).artistLogo);
//                    Bundle bundle = new Bundle();
//                    bundle.putParcelable(ConstantValues.SINGER_PIC,bitmap[0]);
//                    intent.putExtras(bundle);
//                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), holder.imageView, getString(R.string.transition_singer_pic));
//                        startActivity(intent, options.toBundle());
//                    } else {
//                        startActivity(intent);
//                    }
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private class SingerViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final TextView textView;

        public SingerViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_singer);
            textView = (TextView) itemView.findViewById(R.id.tv_singer);
        }
    }
}
