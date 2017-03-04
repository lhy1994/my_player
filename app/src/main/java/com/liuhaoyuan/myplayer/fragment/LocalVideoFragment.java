package com.liuhaoyuan.myplayer.fragment;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.liuhaoyuan.myplayer.LocalVideoActivity;
import com.liuhaoyuan.myplayer.R;
import com.liuhaoyuan.myplayer.VitamioPlayActivity;
import com.liuhaoyuan.myplayer.domain.video.VideoInfo;
import com.liuhaoyuan.myplayer.utils.MusicUtils;
import com.liuhaoyuan.myplayer.utils.TimeFormatUtils;

import java.util.ArrayList;

/**
 * Created by hyliu on 2017/1/26.
 */

public class LocalVideoFragment extends BaseFragment {
    private ArrayList<VideoInfo> mData;
    private ArrayList<Bitmap> thumbnailList;
    private SimPleTask task;

    @Override
    public void loadData() {
        task = new SimPleTask();
        task.execute(getContext());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (task!=null){
            task.cancel(true);
        }
    }

    @Override
    public View onCreateSuccessView() {
        View view=View.inflate(getContext(),R.layout.fragment_local_music,null);
        RecyclerView recyclerView= (RecyclerView) view.findViewById(R.id.lv_local_songs);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getContext());
        VideoAdapter adapter=new VideoAdapter();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        return view;
    }

    private void getVideoThumbnailList(ArrayList<VideoInfo> list){
        if (list!=null){
            thumbnailList=new ArrayList<>();
            for (int i=0;i<list.size();i++){
                Bitmap bitmap = getVideoThumbnail(list.get(i).getData(), 100, 70, MediaStore.Images.Thumbnails.MICRO_KIND);
                thumbnailList.add(bitmap);
            }
        }
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

    private class VideoAdapter extends RecyclerView.Adapter<VideoViewHolder>{

        @Override
        public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_local_video, parent, false);
            VideoViewHolder holder=new VideoViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(VideoViewHolder holder, final int position) {
            if (thumbnailList!=null && thumbnailList.size()>0){
                holder.imageView.setImageBitmap(thumbnailList.get(position));
            }
            String s = mData.get(position).getDuration();
            if (!TextUtils.isEmpty(s)&& TextUtils.isDigitsOnly(s)){
                String hms= TimeFormatUtils.timeFormat(Long.valueOf(s));
                holder.durationTv.setText(hms);
            }
            holder.titleTv.setText(mData.get(position).getTitle());
            holder.moreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(getContext(), v);
                    popupMenu.inflate(R.menu.popup_local_music);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.menu_play) {
                                Intent intent=new Intent(getContext(),VitamioPlayActivity.class);
                                intent.putExtra("position",position);
                                Bundle bundle=new Bundle();
                                bundle.putSerializable("video_info_list",mData);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            } else if (item.getItemId() == R.id.menu_delete) {
                                ContentResolver resolver = getContext().getContentResolver();
                                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                                resolver.delete(uri,MediaStore.Video.Media.TITLE+"=?",new String[]{mData.get(position).getTitle()});
                                mData.remove(position);
                                thumbnailList.remove(position);
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
                    Intent intent=new Intent(getContext(),VitamioPlayActivity.class);
                    intent.putExtra("position",position);
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("video_info_list",mData);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

    private class VideoViewHolder extends RecyclerView.ViewHolder{

        private final ImageView imageView;
        private final TextView titleTv;
        private final TextView durationTv;
        private final Button moreBtn;

        public VideoViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_thumbnail_local_video);
            titleTv = (TextView) itemView.findViewById(R.id.tv_title_local_video);
            durationTv = (TextView) itemView.findViewById(R.id.tv_duration_local_video);
            moreBtn = (Button) itemView.findViewById(R.id.btn_more);
        }
    }


    private class SimPleTask extends AsyncTask<Context,Integer,ArrayList<VideoInfo>>{

        @Override
        protected ArrayList<VideoInfo> doInBackground(Context... params) {
            ContentResolver resolver = params[0].getContentResolver();
            Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            String[] projecttion = new String[]{
                    MediaStore.Video.Media.TITLE,
                    MediaStore.Video.Media.DURATION,
                    MediaStore.Video.Media.SIZE,
                    MediaStore.Video.Media.DATA
            };
            Cursor cursor = resolver.query(uri, projecttion, null, null, null);
            ArrayList<VideoInfo> list=new ArrayList<>();
            while (cursor.moveToNext()) {
                VideoInfo info = new VideoInfo();
                info.setTitle(cursor.getString(0));
                info.setDuration(cursor.getString(1));
                info.setSize(cursor.getLong(2));
                info.setData(cursor.getString(3));
                list.add(info);
            }
            if (list.size()>0){
                getVideoThumbnailList(list);
            }
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<VideoInfo> videoInfos) {
            super.onPostExecute(videoInfos);
            if (videoInfos.size()>0){
                mData=videoInfos;
                onLoadingComplete(true);
            }else {
                onLoadingComplete(false);
            }
        }
    }
}
