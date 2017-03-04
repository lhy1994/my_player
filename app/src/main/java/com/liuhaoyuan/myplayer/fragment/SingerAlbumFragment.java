package com.liuhaoyuan.myplayer.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.liuhaoyuan.myplayer.R;
import com.liuhaoyuan.myplayer.activity.AlbumDetailActivity;
import com.liuhaoyuan.myplayer.api.DataObserver;
import com.liuhaoyuan.myplayer.api.MusicApi;
import com.liuhaoyuan.myplayer.domain.music.XiaMiArtistAlbumInfo;
import com.liuhaoyuan.myplayer.utils.ConstantValues;

import org.xutils.common.Callback;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by hyliu on 2017/1/17.
 */

public class SingerAlbumFragment extends BaseFragment {

    private String mArtistId;
    private int mCurrentPage = 1;
    private ArrayList<XiaMiArtistAlbumInfo.AlbumInfo> mData;
    private AlbumAdapter mAdapter;
    private XRecyclerView mRecyclerView;
    private String mArtistName;

    @Override
    public void loadData() {
        mArtistId = getArguments().getString(ConstantValues.ARTIST_ID);
        mArtistName = getArguments().getString(ConstantValues.ARTIST_NAME);
        final MusicApi qqMusicApi = MusicApi.getInstance();
        qqMusicApi.registerObserver(new DataObserver<ArrayList<XiaMiArtistAlbumInfo.AlbumInfo>>(DataObserver.TYPE_XIAMI_ARTIST_ALBUM) {
            @Override
            public void onComplete(ArrayList<XiaMiArtistAlbumInfo.AlbumInfo> data) {
                if (data != null) {
                    mData = data;
                    onLoadingComplete(true);
                } else {
                    onLoadingComplete(false);
                }
                qqMusicApi.unregisterObserver(this);
            }
        });
        qqMusicApi.getXiaMiArtistAlbum(mArtistId, mCurrentPage + "");
    }

    @Override
    public View onCreateSuccessView() {
        View view = View.inflate(getContext(), R.layout.fragment_singer_album, null);
        mRecyclerView = (XRecyclerView) view.findViewById(R.id.lv_singer_albums);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mAdapter = new AlbumAdapter();
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
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
        final MusicApi qqMusicApi = MusicApi.getInstance();
        qqMusicApi.registerObserver(new DataObserver<ArrayList<XiaMiArtistAlbumInfo.AlbumInfo>>(DataObserver.TYPE_XIAMI_ARTIST_ALBUM) {
            @Override
            public void onComplete(ArrayList<XiaMiArtistAlbumInfo.AlbumInfo> data) {
                qqMusicApi.unregisterObserver(this);
                if (data != null) {
                    mData = data;
                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.refreshComplete();
                } else {
                    refresh();
                }
            }
        });
        mCurrentPage = 1;
        qqMusicApi.getXiaMiArtistAlbum(mArtistId, mCurrentPage + "");
    }

    private void loadMore() {
        final MusicApi qqMusicApi = MusicApi.getInstance();
        qqMusicApi.registerObserver(new DataObserver<ArrayList<XiaMiArtistAlbumInfo.AlbumInfo>>(DataObserver.TYPE_XIAMI_ARTIST_ALBUM) {
            @Override
            public void onComplete(ArrayList<XiaMiArtistAlbumInfo.AlbumInfo> data) {
                if (data != null) {
                    mData.addAll(data);
                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.loadMoreComplete();
                    mCurrentPage++;
                }
                qqMusicApi.unregisterObserver(this);
            }
        });
        qqMusicApi.getXiaMiArtistAlbum(mArtistId, (mCurrentPage + 1) + "");
    }

    private String getRealPicUrl(String url){
        int index = url.lastIndexOf(",");
        String substring = url.substring(0, index+1);
        return substring+"limit_0,m_fixed,w_185,h_185";
    }

    private class AlbumAdapter extends RecyclerView.Adapter<AlbumsViewHolder> {

        @Override
        public AlbumsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_singer_albums, parent, false);
            AlbumsViewHolder holder = new AlbumsViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final AlbumsViewHolder holder, final int position) {
            holder.albumNameTv.setText(mData.get(position).albumName);
            holder.albumCompanyTv.setText(mData.get(position).company);
            holder.albumTimeTv.setText(mData.get(position).pubTime);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getContext(), AlbumDetailActivity.class);
                    intent.putExtra(ConstantValues.ALBUM_ID,mData.get(position).albumId);
                    intent.putExtra(ConstantValues.ARTIST_NAME,mArtistName);
                    intent.putExtra(ConstantValues.ALBUM_NAME,mData.get(position).albumName);
                    getContext().startActivity(intent);
                }
            });

            ImageOptions.Builder builder = new ImageOptions.Builder();
            builder.setFailureDrawableId(R.drawable.nodata);
            ImageOptions imageOptions = builder.build();

            String albumLogo = mData.get(position).albumLogo;
            String realPicUrl = getRealPicUrl(albumLogo);
            Log.e("url",realPicUrl);
            x.image().bind(holder.albumIv, realPicUrl, imageOptions, new Callback.CommonCallback<Drawable>() {

                @Override
                public void onSuccess(Drawable result) {
                    int defaultColor = getResources().getColor(R.color.pinkPrimary);
                    BitmapDrawable drawable = (BitmapDrawable) result;
                    Bitmap bitmap = drawable.getBitmap();
                    Palette palette = Palette.from(bitmap).generate();
                    int dominantColor = palette.getDominantColor(defaultColor);
                    holder.cardView.setCardBackgroundColor(palette.getVibrantColor(dominantColor));
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

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

    private class AlbumsViewHolder extends RecyclerView.ViewHolder {

        private final CardView cardView;
        private final ImageView albumIv;
        private final TextView albumNameTv;
        private final TextView albumCompanyTv;
        private final TextView albumTimeTv;

        public AlbumsViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_singer_album);
            albumIv = (ImageView) itemView.findViewById(R.id.iv_album);
            albumNameTv = (TextView) itemView.findViewById(R.id.tv_album_name);
            albumCompanyTv = (TextView) itemView.findViewById(R.id.tv_album_company);
            albumTimeTv = (TextView) itemView.findViewById(R.id.tv_album_time);
        }
    }
}
