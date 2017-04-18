package com.liuhaoyuan.myplayer.fragment;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.liuhaoyuan.myplayer.R;
import com.liuhaoyuan.myplayer.api.DataObserver;
import com.liuhaoyuan.myplayer.api.MusicApi;
import com.liuhaoyuan.myplayer.aidl.Song;
import com.liuhaoyuan.myplayer.db.FavoriteDbManager;
import com.liuhaoyuan.myplayer.manager.ThreadPoolManger;
import com.liuhaoyuan.myplayer.utils.ConstantValues;
import com.liuhaoyuan.myplayer.utils.MusicUtils;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by hyliu on 2017/1/17.
 */

public class SingerSongsFragment extends BaseFragment {

    private ArrayList<Song> mData;
    private int mCurrentPage =1;
    private SongAdapter mAdapter;
    private XRecyclerView mRecyclerView;
    private String mArtistName;

    @Override
    public void loadData() {
        mArtistName =getArguments().getString(ConstantValues.ARTIST_NAME);
        final MusicApi qqMusicApi = MusicApi.getInstance();
        qqMusicApi.registerObserver(new DataObserver<ArrayList<Song>>(DataObserver.TYPE_SONG) {
            @Override
            public void onComplete(ArrayList<Song> data) {
                if (data != null) {
                    mData = data;
                    onLoadingComplete(true);
                } else {
                    onLoadingComplete(false);
                }
                qqMusicApi.unregisterObserver(this);
            }
        });
        qqMusicApi.getSong(mArtistName, mCurrentPage +"");
    }

    @Override
    public View onCreateSuccessView() {
        View view=View.inflate(getContext(), R.layout.fragment_artist_songs,null);
        mRecyclerView = (XRecyclerView) view.findViewById(R.id.lv_songs);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getContext());
        mAdapter = new SongAdapter(mData);
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

    private void refresh(){
        final MusicApi qqMusicApi = MusicApi.getInstance();
        qqMusicApi.registerObserver(new DataObserver<ArrayList<Song>>(DataObserver.TYPE_SONG) {
            @Override
            public void onComplete(ArrayList<Song> data) {
                qqMusicApi.unregisterObserver(this);
                if (data!=null){
                    mData=data;
                    mAdapter.setData(mData);
                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.refreshComplete();
                }else {
                    refresh();
                }
            }
        });
        mCurrentPage =1;
        qqMusicApi.getSong(mArtistName,mCurrentPage+"");
    }

    private void loadMore(){
        final MusicApi qqMusicApi = MusicApi.getInstance();
        qqMusicApi.registerObserver(new DataObserver<ArrayList<Song>>(DataObserver.TYPE_SONG) {
            @Override
            public void onComplete(ArrayList<Song> data) {
                if (data!=null){
                    mData.addAll(data);
                    mAdapter.setData(mData);
                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.loadMoreComplete();
                    mCurrentPage++;
                }
                qqMusicApi.unregisterObserver(this);
            }
        });
        qqMusicApi.getSong(mArtistName,(mCurrentPage +1)+"");
    }

    private class SongAdapter extends RecyclerView.Adapter<SongViewHolder>{
        ArrayList<Song> data;
        public void setData(ArrayList<Song> data){
            this.data=data;
        }

        public SongAdapter(ArrayList<Song> data){
            this.data=data;
        }

        @Override
        public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_singer_songs, parent, false);
            SongViewHolder songViewHolder=new SongViewHolder(view);
            return songViewHolder;
        }

        @Override
        public void onBindViewHolder(SongViewHolder holder, final int position) {
            ImageOptions.Builder builder=new ImageOptions.Builder();
            builder.setFailureDrawableId(R.drawable.music_fail);
            ImageOptions imageOptions = builder.build();
            x.image().bind(holder.imageView,data.get(position).albumpic_small,imageOptions);
            holder.nameTv.setText(data.get(position).songname);
            holder.albumTv.setText(data.get(position).albumname);
            holder.moreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    PopupMenu popupMenu=new PopupMenu(getContext(),v);
                    popupMenu.inflate(R.menu.popup_hotmusic);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.menu_play:
                                    MusicUtils.playMusic(getContext(),position,true,data);
                                    break;
                                case R.id.menu_favorite:
                                    ThreadPoolManger.getInstance().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            FavoriteDbManager.getInstance(getContext()).insertSong(data.get(position));
                                        }
                                    });
                                    final Snackbar snackbar=Snackbar.make(v,"收藏成功",Snackbar.LENGTH_LONG);
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
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MusicUtils.playMusic(getContext(),position,true,data);
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private class SongViewHolder extends RecyclerView.ViewHolder{

        private final ImageView imageView;
        private final TextView nameTv;
        private final TextView albumTv;
        private final Button moreBtn;

        public SongViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_singer_song);
            nameTv = (TextView) itemView.findViewById(R.id.tv_song_name);
            albumTv = (TextView) itemView.findViewById(R.id.tv_song_album);
            moreBtn = (Button) itemView.findViewById(R.id.btn_song_more);
        }
    }
}
