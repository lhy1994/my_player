package com.liuhaoyuan.myplayer.fragment;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.liuhaoyuan.myplayer.R;
import com.liuhaoyuan.myplayer.aidl.Song;
import com.liuhaoyuan.myplayer.utils.MusicUtils;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by hyliu on 2017/1/26.
 */

public class LocalMusicFragment extends BaseFragment {
    private ArrayList<Song> mData;

    @Override
    public void loadData() {
        SimpleTask simpleTask = new SimpleTask();
        simpleTask.execute(getContext());
    }

    @Override
    public View onCreateSuccessView() {
        View view = View.inflate(getContext(), R.layout.fragment_local_music, null);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.lv_local_songs);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        MusicAdapter adapter = new MusicAdapter();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        return view;
    }

    private String getAlbumArt(String album_id) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = getContext().getContentResolver().query(
                Uri.parse(mUriAlbums + "/" + album_id),
                projection, null, null, null);
        String album_art = null;
        if (cur != null) {
            if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
                cur.moveToNext();
                album_art = cur.getString(0);
            }
            cur.close();
        }
        return album_art;
    }

    private class SimpleTask extends AsyncTask<Context, Integer, ArrayList<Song>> {

        @Override
        protected ArrayList<Song> doInBackground(Context... params) {
            ArrayList<Song> songList = new ArrayList<>();
            ContentResolver resolver = params[0].getContentResolver();
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String[] projecttion = new String[]{
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.ALBUM_ID,
                    MediaStore.Audio.Media.ALBUM
            };
            Cursor cursor = resolver.query(uri, projecttion, null, null, null);
            while (cursor.moveToNext()) {
                Song info = new Song();
                info.songname = cursor.getString(0);
                info.url = cursor.getString(1);
                info.singername = cursor.getString(2);
                info.albumid = cursor.getString(3);
                info.albumname = cursor.getString(4);
                info.albumpic_big = getAlbumArt(info.albumid);
                songList.add(info);
            }
            return songList;
        }

        @Override
        protected void onPostExecute(ArrayList<Song> songs) {
            super.onPostExecute(songs);
            if (songs.size() > 0) {
                mData = songs;
                onLoadingComplete(true);
            } else onLoadingComplete(false);
        }
    }

    private class MusicAdapter extends RecyclerView.Adapter<MusicViewHolder> {

        @Override
        public MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_local_songs, parent, false);
            MusicViewHolder musicViewHolder = new MusicViewHolder(view);
            return musicViewHolder;
        }

        @Override
        public void onBindViewHolder(MusicViewHolder holder, final int position) {
            ImageOptions.Builder builder=new ImageOptions.Builder();
            ImageOptions imageOptions = builder.setFailureDrawableId(R.drawable.music_fail).build();
            x.image().bind(holder.mAlbumArtIv,mData.get(position).albumpic_big,imageOptions);
            holder.mSongNameTV.setText(mData.get(position).songname);
            holder.mSingerNameTv.setText(mData.get(position).singername);
            holder.mMoreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(getContext(), v);
                    popupMenu.inflate(R.menu.popup_local_music);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.menu_play) {
                                MusicUtils.playMusic(getContext(), position, true, mData);
                            } else if (item.getItemId() == R.id.menu_delete) {
                                ContentResolver resolver = getContext().getContentResolver();
                                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                                resolver.delete(uri, MediaStore.Audio.Media.TITLE + "=?", new String[]{mData.get(position).songname});
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
                    MusicUtils.playMusic(getContext(), position, true, mData);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

    private class MusicViewHolder extends RecyclerView.ViewHolder {

        private TextView mSongNameTV;
        private TextView mSingerNameTv;
        private Button mMoreBtn;
        private ImageView mAlbumArtIv;

        public MusicViewHolder(View itemView) {
            super(itemView);
            mAlbumArtIv = (ImageView) itemView.findViewById(R.id.iv_rank_song);
            mSongNameTV = (TextView) itemView.findViewById(R.id.tv_song_name);
            mSingerNameTv = (TextView) itemView.findViewById(R.id.tv_singer);
            mMoreBtn = (Button) itemView.findViewById(R.id.btn_song_more);
        }
    }
}
