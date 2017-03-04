package com.liuhaoyuan.myplayer.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.liuhaoyuan.myplayer.R;
import com.liuhaoyuan.myplayer.aidl.Song;
import com.liuhaoyuan.myplayer.db.FavoriteDbUtils;
import com.liuhaoyuan.myplayer.utils.MusicUtils;

import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by hyliu on 2017/1/31.
 */

public class FavoriteSongFragment extends BaseFragment {
    private ArrayList<Song> mData;

    @Override
    public void loadData() {
        SimpleTask simpleTask = new SimpleTask();
        simpleTask.execute(getContext());
    }

    @Override
    public View onCreateSuccessView() {
        View view = View.inflate(getContext(), R.layout.fragment_favorite_detail, null);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.lv_favorite);
        LinearLayout linearLayout= (LinearLayout) view.findViewById(R.id.ll_favorite_empty);
        if (mData!=null){
            recyclerView.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.INVISIBLE);
            RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getContext());
            FavoriteSongAdapter adapter=new FavoriteSongAdapter();
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
        }else {
            recyclerView.setVisibility(View.INVISIBLE);
            linearLayout.setVisibility(View.VISIBLE);
        }
        return view;
    }

    private class FavoriteSongAdapter extends RecyclerView.Adapter<FavoriteSongViewHolder>{

        @Override
        public FavoriteSongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rank_songs, parent, false);
            FavoriteSongViewHolder viewHolder=new FavoriteSongViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(FavoriteSongViewHolder holder, final int position) {
            x.image().bind(holder.imageView,mData.get(position).albumpic_big);
            holder.songNameTv.setText(mData.get(position).songname);
            holder.singerNameTv.setText(mData.get(position).singername);
            holder.moreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PopupMenu popupMenu=new PopupMenu(getContext(),v);
                    popupMenu.inflate(R.menu.popup_local_music);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int itemId = item.getItemId();
                            if (itemId==R.id.menu_play){
                                MusicUtils.playMusic(getContext(),position,true,mData);
                            }else if (itemId==R.id.menu_delete){
                                FavoriteDbUtils dbUtils = FavoriteDbUtils.getInstance(getContext());
                                dbUtils.deleteSong(mData.get(position).songid);
                                mData.remove(position);
                                notifyDataSetChanged();
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
                    MusicUtils.playMusic(getContext(),position,true,mData);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

    private class FavoriteSongViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView songNameTv;
        private TextView singerNameTv;
        private Button moreBtn;

        public FavoriteSongViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_rank_song);
            songNameTv = (TextView) itemView.findViewById(R.id.tv_song_name);
            singerNameTv = (TextView) itemView.findViewById(R.id.tv_singer);
            moreBtn = (Button) itemView.findViewById(R.id.btn_song_more);
        }
    }

    private class SimpleTask extends AsyncTask<Context, Integer, ArrayList<Song>> {

        @Override
        protected ArrayList<Song> doInBackground(Context... params) {
            FavoriteDbUtils dbUtils = FavoriteDbUtils.getInstance(getContext());
            ArrayList<Song> songList = dbUtils.queryAllSong();
            return songList;
        }

        @Override
        protected void onPostExecute(ArrayList<Song> songs) {
            super.onPostExecute(songs);
            if (songs != null) {
                if (songs.size()>0){
                    mData = songs;
                }else {
                    mData=null;
                }
                onLoadingComplete(true);
            } else {
                onLoadingComplete(false);
            }
        }
    }
}
