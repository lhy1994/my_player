package com.liuhaoyuan.myplayer;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.liuhaoyuan.myplayer.domain.video.VideoInfo;
import com.liuhaoyuan.myplayer.utils.TimeFormatUtils;

import java.util.ArrayList;

/**
 * Created by liuhaoyuan on 2016/7/21.
 */
public class LocalVideoActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<VideoInfo> localVideoInfoList;
    private ArrayList<Bitmap> thumbnailList;
    private MyListAdapter adapter;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            thumbnailList=new ArrayList<>();
            for (int i=0;i<localVideoInfoList.size();i++){
                Bitmap bitmap = getVideoThumbnail(localVideoInfoList.get(i).getData(), 100, 70, MediaStore.Images.Thumbnails.MICRO_KIND);
                thumbnailList.add(bitmap);
            }

            adapter = new MyListAdapter();
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent=new Intent(LocalVideoActivity.this,VitamioPlayActivity.class);
                    intent.putExtra("position",i);
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("video_info_list",localVideoInfoList);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_video);

        listView = (ListView) findViewById(R.id.lv_local_video);
        getLocalVideoInfo();

    }

    private void getLocalVideoInfo() {
        localVideoInfoList = new ArrayList<>();
        scanFile();
        Thread thread = new Thread() {
            @Override
            public void run() {
                ContentResolver resolver = getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] projecttion = new String[]{
                        MediaStore.Video.Media.TITLE,
                        MediaStore.Video.Media.DURATION,
                        MediaStore.Video.Media.SIZE,
                        MediaStore.Video.Media.DATA
                };
                Cursor cursor = resolver.query(uri, projecttion, null, null, null);
                while (cursor.moveToNext()) {
                    VideoInfo info = new VideoInfo();
                    info.setTitle(cursor.getString(0));
                    info.setDuration(cursor.getString(1));
                    info.setSize(cursor.getLong(2));
                    info.setData(cursor.getString(3));
                    localVideoInfoList.add(info);
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

    private Bitmap getVideoThumbnail(String videoPath, int width, int height,
                                     int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }



    class MyListAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return localVideoInfoList.size();
        }

        @Override
        public Object getItem(int i) {
            return localVideoInfoList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                view = View.inflate(LocalVideoActivity.this, R.layout.item_local_video, null);
                holder = new ViewHolder();
                holder.imageView = (ImageView) view.findViewById(R.id.iv_thumbnail_local_video);
                holder.title = (TextView) view.findViewById(R.id.tv_title_local_video);
                holder.duration = (TextView) view.findViewById(R.id.tv_duration_local_video);
                holder.button= (Button) view.findViewById(R.id.btn_more);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(LocalVideoActivity.this);
                    builder.setItems(new String[]{"删除"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                           switch (i){
                               case 0:
                                   ContentResolver resolver = getContentResolver();
                                   Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                                   resolver.delete(uri,MediaStore.Video.Media.TITLE+"=?",new String[]{localVideoInfoList.get(position).getTitle()});
                                   localVideoInfoList.remove(position);
                                   thumbnailList.remove(position);
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

            String s = localVideoInfoList.get(position).getDuration();
            if (!TextUtils.isEmpty(s)&& TextUtils.isDigitsOnly(s)){
                String hms= TimeFormatUtils.timeFormat(Long.valueOf(s));
                holder.duration.setText(hms);
            }
            holder.title.setText(localVideoInfoList.get(position).getTitle());
            holder.imageView.setImageBitmap(thumbnailList.get(position));
            return view;
        }
    }

    class ViewHolder {
        ImageView imageView;
        TextView title;
        TextView duration;
        Button button;
    }
}
