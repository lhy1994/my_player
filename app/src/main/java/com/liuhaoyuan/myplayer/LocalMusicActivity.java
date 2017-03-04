package com.liuhaoyuan.myplayer;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.liuhaoyuan.myplayer.activity.MusicPlayActivity;
import com.liuhaoyuan.myplayer.aidl.Song;

import java.util.ArrayList;

/**
 * Created by liuhaoyuan on 2016/7/21.
 */
public class LocalMusicActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<Song> localMusicInfoList;
    private MusicListAdapter adapter;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            adapter = new MusicListAdapter();
            listView.setAdapter(adapter);
//            for (SongMusicInfo s:localMusicInfoList){
//                Log.e("test",s.toString());
//            }
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(LocalMusicActivity.this, MusicPlayActivity.class);
                    intent.putExtra("position", i - listView.getHeaderViewsCount());
                    intent.putExtra("is_remote_play", false);
                    startActivity(intent);
                }
            });
            playAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(LocalMusicActivity.this, MusicPlayActivity.class);
                    intent.putExtra("position", 0);
                    intent.putExtra("is_remote_play", false);
                    startActivity(intent);
                }
            });
        }
    };
    private View headerView;
    private Button playAll;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music);

        listView = (ListView) findViewById(R.id.lv_local_music);
        headerView = View.inflate(this, R.layout.list_header_net_music, null);
        playAll = (Button) headerView.findViewById(R.id.btn_header_play_all);
        listView.addHeaderView(headerView);
        getLocalVideoInfo();
    }

    private void getLocalVideoInfo() {
        localMusicInfoList = new ArrayList<>();
        scanFile();
        Thread thread = new Thread() {
            @Override
            public void run() {
                ContentResolver resolver = getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] projecttion = new String[]{
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.ARTIST
                };
                Cursor cursor = resolver.query(uri, projecttion, null, null, null);
                while (cursor.moveToNext()) {
                    Song info = new Song();
                    info.songname = cursor.getString(0);
                    info.singername = cursor.getString(1);
                    localMusicInfoList.add(info);
                }
                mHandler.sendEmptyMessage(0);
            }
        };
        thread.start();
    }

    private void scanFile() {
//        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(Environment.getExternalStorageDirectory()));
        sendBroadcast(scanIntent);
    }

    class MusicListAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return localMusicInfoList.size();
        }

        @Override
        public Object getItem(int i) {
            return localMusicInfoList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                view = View.inflate(LocalMusicActivity.this, R.layout.list_item_local_music, null);
                holder = new ViewHolder();
                holder.title = (TextView) view.findViewById(R.id.tv_title_local_music);
                holder.button = (ImageButton) view.findViewById(R.id.btn_more_music);
                holder.artist = (TextView) view.findViewById(R.id.tv_artist_local_music);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LocalMusicActivity.this);
                    builder.setItems(new String[]{"删除"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i) {
                                case 0:
                                    ContentResolver resolver = getContentResolver();
                                    Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                                    resolver.delete(uri, MediaStore.Audio.Media.TITLE + "=?", new String[]{localMusicInfoList.get(position).songname});
                                    localMusicInfoList.remove(position - listView.getHeaderViewsCount());
                                    adapter.notifyDataSetChanged();
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                    builder.show();
                }
            });

            holder.title.setText(localMusicInfoList.get(position).songname);
            holder.artist.setText(localMusicInfoList.get(position).singername);
            return view;
        }
    }

    class ViewHolder {
        TextView title;
        ImageButton button;
        TextView artist;
    }
}
