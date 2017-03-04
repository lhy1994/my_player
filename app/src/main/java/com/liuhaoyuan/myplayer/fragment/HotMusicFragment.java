package com.liuhaoyuan.myplayer.fragment;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.liuhaoyuan.myplayer.R;
import com.liuhaoyuan.myplayer.api.DataObserver;
import com.liuhaoyuan.myplayer.api.MusicApi;
import com.liuhaoyuan.myplayer.aidl.Song;
import com.liuhaoyuan.myplayer.db.FavoriteDbUtils;
import com.liuhaoyuan.myplayer.utils.MusicUtils;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by hyliu on 2017/1/14.
 */

public class HotMusicFragment extends BaseFragment {

    private ArrayList<Song> mData;
    private MusicApi qqMusicApi;
    private String[] topIds;
    private RecyclerView recyclerView;

    @Override
    public void loadData() {
        qqMusicApi = MusicApi.getInstance();
        topIds = qqMusicApi.getQQTopIds();
        qqMusicApi.registerObserver(new DataObserver<ArrayList<Song>>(DataObserver.TYPE_QQ_RANKS) {
            @Override
            public void onComplete(ArrayList<Song> data) {
                if (data != null) {
                    mData = data;
                    Log.e("test", data.toString());
                    onLoadingComplete(true);
                } else {
                    onLoadingComplete(false);
                }
                qqMusicApi.unregisterObserver(this);
            }
        });
        qqMusicApi.getQQRanks(topIds[0]);
    }

    @Override
    public View onCreateSuccessView() {
        View view = View.inflate(getContext(), R.layout.fragment_hotmusic, null);

        recyclerView = (RecyclerView) view.findViewById(R.id.lv_hotmusic);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        final HotMusicAdapter adapter = new HotMusicAdapter(mData);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        AppCompatSpinner spinner = (AppCompatSpinner) view.findViewById(R.id.spinner_hotmusic);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.category, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view;
                qqMusicApi.registerObserver(new DataObserver<ArrayList<Song>>(DataObserver.TYPE_QQ_RANKS) {
                    @Override
                    public void onComplete(ArrayList<Song> data) {
                        if (data != null) {
                            mData = data;
                            adapter.setData(mData);
                            adapter.notifyDataSetChanged();
                        }
                        qqMusicApi.unregisterObserver(this);
                    }
                });
                qqMusicApi.getQQRanks(topIds[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }





    class HotMusicAdapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<Song> data;

        HotMusicAdapter(ArrayList<Song> list) {
            this.data = list;
        }

        public void setData(ArrayList<Song> data) {
            this.data = data;
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hotmusic, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            ImageOptions.Builder builder=new ImageOptions.Builder();
            builder.setFailureDrawableId(R.drawable.music_fail);
            ImageOptions imageOptions = builder.build();
            x.image().bind(holder.imageView, data.get(position).albumpic_big,imageOptions);
            holder.nameTv.setText(data.get(position).songname);
            holder.singerTv.setText(data.get(position).singername);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   MusicUtils.playMusic(getContext(),position,true,data);
                }
            });
            holder.button.setOnClickListener(new View.OnClickListener() {
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
                                    FavoriteDbUtils dbUtils = FavoriteDbUtils.getInstance(getContext());
                                    Song song = mData.get(position);
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
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView nameTv;
        private TextView singerTv;
        private AppCompatButton button;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_hotmusic);
            nameTv = (TextView) itemView.findViewById(R.id.tv_hotmusic_name);
            singerTv = (TextView) itemView.findViewById(R.id.tv_hotmusic_singer);
            button = (AppCompatButton) itemView.findViewById(R.id.btn_hotmusic_more);
        }
    }
}
