package com.liuhaoyuan.myplayer.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liuhaoyuan.myplayer.R;
import com.liuhaoyuan.myplayer.aidl.Song;
import com.liuhaoyuan.myplayer.db.HistoryDbManager;
import com.liuhaoyuan.myplayer.manager.ThreadPoolManger;
import com.liuhaoyuan.myplayer.utils.MusicUtils;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by liuhaoyuan on 17/4/8.
 */

public class HistoryFragment extends BaseFragment {
    private ArrayList<Song> mData;
    private ArrayList<Song> mData2;

    @Override
    public void loadData() {
        MyTask task = new MyTask();
        task.execute(getContext());
    }

    @Override
    public View onCreateSuccessView() {
        View view = View.inflate(getContext(), R.layout.fragment_music_history, null);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.lv_music_history);
        RecyclerView recyclerView2 = (RecyclerView) view.findViewById(R.id.lv_music_history2);
        LinearLayout container= (LinearLayout) view.findViewById(R.id.ll_container);
        LinearLayout emptyView = (LinearLayout) view.findViewById(R.id.ll_empty);
        if (mData.isEmpty() && mData2.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            container.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            container.setVisibility(View.VISIBLE);

            Collections.reverse(mData);
            RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false);
            ListAdapter listAdapter=new ListAdapter(mData);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(listAdapter);

            Collections.reverse(mData2);
            RecyclerView.LayoutManager layoutManager2=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
            ListAdapter listAdapter2=new ListAdapter(mData2);
            recyclerView2.setLayoutManager(layoutManager2);
            recyclerView2.setAdapter(listAdapter2);
        }
        return view;
    }

    private class ListAdapter extends RecyclerView.Adapter<ViewHolder>{
        private ArrayList<Song> mListData;
        public ListAdapter(ArrayList<Song> data){
            mListData=data;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_history, parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final Song data = mListData.get(position);
            ImageOptions.Builder builder=new ImageOptions.Builder();
            builder.setFailureDrawableId(R.drawable.music_fail);
            ImageOptions imageOptions = builder.build();
            x.image().bind(holder.mSongAlbumIv,data.albumpic_big,imageOptions);
            holder.mSongNameTv.setText(data.songname);
            holder.mSingerName.setText(data.singername);
            holder.mMoreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    PopupMenu popupMenu=new PopupMenu(getContext(),v);
                    popupMenu.inflate(R.menu.popup_local_music);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.menu_play:
                                    MusicUtils.playMusic(getContext(),position,true,mListData);
                                    break;
                                case R.id.menu_delete:
                                    mListData.remove(position);
                                    notifyDataSetChanged();
                                    ThreadPoolManger.getInstance().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            HistoryDbManager.getInstance(getContext()).deleteMusicHistory(data.url);
                                        }
                                    });
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
                    MusicUtils.playMusic(getContext(),position,true,mListData);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private CardView mCard;
        private ImageView mSongAlbumIv;
        private TextView mSongNameTv;
        private TextView mSingerName;
        private AppCompatButton mMoreBtn;
        private final FrameLayout frameLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            frameLayout = (FrameLayout) itemView.findViewById(R.id.fl_container);
            mCard = (CardView) itemView.findViewById(R.id.card_music_history);
            mSongAlbumIv = (ImageView) itemView.findViewById(R.id.iv_music_history);
            mSongNameTv = (TextView) itemView.findViewById(R.id.tv_song_name);
            mSingerName = (TextView) itemView.findViewById(R.id.tv_singer_name);
            mMoreBtn = (AppCompatButton) itemView.findViewById(R.id.btn_more);
        }
    }

    private class MyTask extends AsyncTask<Context, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Context... params) {
            HistoryDbManager manager = HistoryDbManager.getInstance(params[0]);
            mData=manager.getAllMusicHistory();
            mData2=manager.getAllMusicHistoryByCount();
            return mData!=null && mData2!=null;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            if (b) {
                onLoadingComplete(true);
            } else {
                onLoadingComplete(false);
            }
        }
    }
}
