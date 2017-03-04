package com.liuhaoyuan.myplayer.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
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
import com.liuhaoyuan.myplayer.activity.VideoDetailActivity;
import com.liuhaoyuan.myplayer.db.FavoriteDbUtils;
import com.liuhaoyuan.myplayer.domain.video.NetVideoItem;
import com.liuhaoyuan.myplayer.utils.ConstantValues;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by hyliu on 2017/2/1.
 */

public class FavoriteVideoFragment extends BaseFragment {
    private ArrayList<NetVideoItem> mData;

    @Override
    public void loadData() {
        SimpleTask simpleTask = new SimpleTask();
        simpleTask.execute(getContext());
    }

    @Override
    public View onCreateSuccessView() {
        View view = View.inflate(getContext(), R.layout.fragment_favorite_detail, null);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.lv_favorite);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.ll_favorite_empty);
        if (mData == null) {
            recyclerView.setVisibility(View.INVISIBLE);
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.INVISIBLE);

            RecyclerView.LayoutManager layoutManager=new GridLayoutManager(getContext(),2);
            VideoAdapter adpater=new VideoAdapter();
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adpater);
        }
        return view;
    }
    private class VideoAdapter extends RecyclerView.Adapter<VideoViewHolder>{

        @Override
        public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_video, parent, false);
            VideoViewHolder holder=new VideoViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(VideoViewHolder holder, final int position) {
            ImageOptions.Builder builder=new ImageOptions.Builder();
            builder.setFailureDrawableId(R.drawable.nodata);
            ImageOptions imageOptions = builder.build();
            x.image().bind(holder.imageView,mData.get(position).vpic,imageOptions);
            holder.nameTv.setText(mData.get(position).name);
            holder.moreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu=new PopupMenu(getContext(),v);
                    popupMenu.inflate(R.menu.popup_favoriet_singer);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int itemId = item.getItemId();
                            if (itemId==R.id.menu_detail){
                                Intent intent = new Intent(getContext(), VideoDetailActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(ConstantValues.VIDEO_INFO, mData.get(position));
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }else if (itemId==R.id.menu_delete){
                                FavoriteDbUtils dbUtils = FavoriteDbUtils.getInstance(getContext());
                                dbUtils.deleteVideo(mData.get(position).programmeId);
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
                    Intent intent = new Intent(getContext(), VideoDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(ConstantValues.VIDEO_INFO, mData.get(position));
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

    private class VideoViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView nameTv;
        private Button moreBtn;

        public VideoViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_video);
            nameTv = (TextView) itemView.findViewById(R.id.tv_video_name);
            moreBtn = (Button) itemView.findViewById(R.id.btn_more);
        }
    }

    private class SimpleTask extends AsyncTask<Context, Integer, ArrayList<NetVideoItem>> {

        @Override
        protected ArrayList<NetVideoItem> doInBackground(Context... params) {
            FavoriteDbUtils dbUtils = FavoriteDbUtils.getInstance(params[0]);
            ArrayList<NetVideoItem> list = dbUtils.queryAllVideo();
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<NetVideoItem> netVideoItems) {
            super.onPostExecute(netVideoItems);
            if (netVideoItems == null) {
                onLoadingComplete(false);
            } else {
                if (netVideoItems.size() > 0) {
                    mData = netVideoItems;
                } else {
                    mData = null;
                }
                onLoadingComplete(true);
            }
        }
    }
}
