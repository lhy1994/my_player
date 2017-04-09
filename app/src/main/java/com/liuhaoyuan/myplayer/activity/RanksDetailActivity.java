package com.liuhaoyuan.myplayer.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.AutoTransition;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.liuhaoyuan.myplayer.APP;
import com.liuhaoyuan.myplayer.R;
import com.liuhaoyuan.myplayer.api.DataObserver;
import com.liuhaoyuan.myplayer.api.MusicApi;
import com.liuhaoyuan.myplayer.aidl.Song;
import com.liuhaoyuan.myplayer.db.FavoriteDbManager;
import com.liuhaoyuan.myplayer.utils.MusicUtils;

import org.xutils.x;

import java.util.ArrayList;

public class RanksDetailActivity extends AppCompatActivity {

    private String mRankID;
    private ArrayList<Song> mSonglist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        int currentTheme = APP.getCurrentTheme();
        APP application = (APP) getApplication();
        int currentTheme = application.getCurrentTheme();
        setTheme(currentTheme);

        // 设置一个exit transition
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setAllowEnterTransitionOverlap(true);
            getWindow().setExitTransition(new AutoTransition());//new Slide()  new Fade()
            getWindow().setEnterTransition(new AutoTransition());
            getWindow().setSharedElementEnterTransition(new AutoTransition());
            getWindow().setSharedElementExitTransition(new AutoTransition());
        }

        setContentView(R.layout.activity_ranks_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        String rankName = intent.getStringExtra("rankName");
        int rankPic = intent.getIntExtra("rankPic", 0);
        mRankID = intent.getStringExtra("rankId");

        ImageView rankPicIv = (ImageView) findViewById(R.id.iv_rank_detail);
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        rankPicIv.setImageResource(rankPic);
        toolbarLayout.setTitle(rankName);
        initListData();
    }

    private void initListData() {
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.lv_rank_songs);
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        MusicApi qqMusicApi = MusicApi.getInstance();
        qqMusicApi.registerObserver(new DataObserver<ArrayList<Song>>(DataObserver.TYPE_QQ_RANKS) {
            @Override
            public void onComplete(ArrayList<Song> data) {
                if (data!=null){
                    mSonglist=data;
                    SongAdapter songAdapter=new SongAdapter(mSonglist);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(songAdapter);
                }
            }
        });
        qqMusicApi.getQQRanks(mRankID);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSonglist!=null && mSonglist.size()>0){
                    MusicUtils.playMusic(RanksDetailActivity.this,0,true,mSonglist);
                }else {
                    final Snackbar snackbar=Snackbar.make(view,"没有找到歌曲播放",Snackbar.LENGTH_LONG);
                    snackbar.setAction("知道了", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                        }
                    });
                    snackbar.show();
                }
            }
        });
    }

    private class SongAdapter extends RecyclerView.Adapter<SongViewHolder> {
        private ArrayList<Song> data;

        public SongAdapter(ArrayList<Song> data) {
            this.data = data;
        }

        @Override
        public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ranks_songs, parent, false);
            SongViewHolder songViewHolder = new SongViewHolder(view);
            return songViewHolder;
        }

        @Override
        public void onBindViewHolder(SongViewHolder holder, final int position) {
            holder.rankTv.setText((position+1)+"");
            x.image().bind(holder.imageView, data.get(position).albumpic_small);
            holder.nameTV.setText(data.get(position).songname);
            holder.singerTv.setText(data.get(position).singername);
            holder.moreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    PopupMenu popupMenu = new PopupMenu(RanksDetailActivity.this, v);
                    popupMenu.inflate(R.menu.popup_hotmusic);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_play:
                                    MusicUtils.playMusic(RanksDetailActivity.this, position, true, data);
                                    break;
                                case R.id.menu_favorite:
                                    FavoriteDbManager dbUtils = FavoriteDbManager.getInstance(RanksDetailActivity.this);
                                    Song song = data.get(position);
                                    dbUtils.insertSong(song.songid,song.songname,song.singername,song.url,song.albumpic_big);
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
                    MusicUtils.playMusic(RanksDetailActivity.this,position,true,data);
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private class SongViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final TextView nameTV;
        private final TextView singerTv;
        private final Button moreBtn;
        private final TextView rankTv;

        public SongViewHolder(View itemView) {
            super(itemView);
            rankTv = (TextView) itemView.findViewById(R.id.tv_rank);
            imageView = (ImageView) itemView.findViewById(R.id.iv_rank_song);
            nameTV = (TextView) itemView.findViewById(R.id.tv_song_name);
            singerTv = (TextView) itemView.findViewById(R.id.tv_singer);
            moreBtn = (Button) itemView.findViewById(R.id.btn_song_more);
        }
    }
}
