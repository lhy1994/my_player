package com.liuhaoyuan.myplayer.activity;

import android.app.Application;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.liuhaoyuan.myplayer.db.FavoriteDbUtils;
import com.liuhaoyuan.myplayer.domain.music.XiaMiAlbumSongsInfo;
import com.liuhaoyuan.myplayer.domain.music.XiaMiArtistAlbumInfo;
import com.liuhaoyuan.myplayer.utils.ConstantValues;
import com.liuhaoyuan.myplayer.utils.MusicUtils;

import org.xutils.x;

import java.util.ArrayList;

public class AlbumDetailActivity extends AppCompatActivity {

    private String mAlbumId;
    private ImageView mAlbumIv;
    private TextView mAlbumNameTv;
    private TextView mAlbumCompanyTv;
    private TextView mAlbumTimeTv;
    private TextView mAlbumStyleTv;
    private TextView mAlbumLanguageTv;
    private RecyclerView mRecyclerView;
    private String mArtistName;
    private String mAlbumName;
    private ArrayList<Song> mSongList;
    private SongAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        int currentTheme = APP.getCurrentTheme();
        APP application = (APP) getApplication();
        int currentTheme = application.getCurrentTheme();
        setTheme(currentTheme);
        setContentView(R.layout.activity_album_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initView();
    }

    private void initView(){
        setTitle("");
        mAlbumIv = (ImageView) findViewById(R.id.iv_album_detail);
        mAlbumNameTv = (TextView) findViewById(R.id.tv_album_name);
        mAlbumCompanyTv = (TextView) findViewById(R.id.tv_album_company);
        mAlbumTimeTv = (TextView) findViewById(R.id.tv_album_time);
        mAlbumLanguageTv = (TextView) findViewById(R.id.tv_album_language);
        mRecyclerView = (RecyclerView) findViewById(R.id.lv_album_songs);
        initData();
    }

    private void initData(){
        mAlbumId = getIntent().getStringExtra(ConstantValues.ALBUM_ID);
        mArtistName = getIntent().getStringExtra(ConstantValues.ARTIST_NAME);
        mAlbumName = getIntent().getStringExtra(ConstantValues.ALBUM_NAME);
        mAlbumNameTv.setText(mAlbumName);
        MusicApi qqMusicApi = MusicApi.getInstance();
        qqMusicApi.registerObserver(new DataObserver<XiaMiAlbumSongsInfo.Body>(DataObserver.TYPE_XIAMI_ALBUM_SONGS) {
            @Override
            public void onComplete(XiaMiAlbumSongsInfo.Body data) {
                if (data!=null){
                    XiaMiArtistAlbumInfo.AlbumInfo albumInfo =  data.album;
                    x.image().bind(mAlbumIv,albumInfo.albumLogo);
                    mAlbumCompanyTv.setText(albumInfo.company);
                    mAlbumTimeTv.setText(albumInfo.pubTime);
                    mAlbumLanguageTv.setText(albumInfo.language);
                }
            }
        });
        qqMusicApi.getXiaMiAlbumSongs(mAlbumId);
        initListData();
    }

    private void initListData(){
        MusicApi qqMusicApi = MusicApi.getInstance();
        qqMusicApi.registerObserver(new DataObserver<ArrayList<Song>>(DataObserver.TYPE_SONG) {
            @Override
            public void onComplete(ArrayList<Song> data) {
                if (data!=null){
                    mSongList=data;
                    RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(AlbumDetailActivity.this);
                    mAdapter = new SongAdapter();
                    mRecyclerView.setLayoutManager(layoutManager);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
        });
        qqMusicApi.getSong(mArtistName+" "+mAlbumName,"1");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               MusicUtils.playMusic(AlbumDetailActivity.this,0,true,mSongList);
            }
        });
    }


    private class SongAdapter extends RecyclerView.Adapter<SongViewHolder>{

        @Override
        public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_songs, parent, false);
            SongViewHolder songViewHolder=new SongViewHolder(view);
            return songViewHolder;
        }

        @Override
        public void onBindViewHolder(SongViewHolder holder, final int position) {
            holder.ranksTv.setText((position+1)+"");
            holder.songNameTv.setText(mSongList.get(position).songname);
            holder.moreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    PopupMenu popupMenu=new PopupMenu(AlbumDetailActivity.this,v);
                    popupMenu.inflate(R.menu.popup_hotmusic);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.menu_play:
                                    MusicUtils.playMusic(AlbumDetailActivity.this,position,true,mSongList);
                                    break;
                                case R.id.menu_favorite:
                                    FavoriteDbUtils dbUtils = FavoriteDbUtils.getInstance(AlbumDetailActivity.this);
                                    Song song = mSongList.get(position);
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
                    MusicUtils.playMusic(AlbumDetailActivity.this,position,true,mSongList);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mSongList.size();
        }
    }

    private class SongViewHolder extends RecyclerView.ViewHolder{

        private final TextView ranksTv;
        private final TextView songNameTv;
        private final Button moreBtn;

        public SongViewHolder(View itemView) {
            super(itemView);
            ranksTv = (TextView) itemView.findViewById(R.id.tv_ranks);
            songNameTv = (TextView) itemView.findViewById(R.id.tv_song_name);
            moreBtn = (Button) itemView.findViewById(R.id.btn_song_more);
        }
    }
}
