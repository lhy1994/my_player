package com.liuhaoyuan.myplayer.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.liuhaoyuan.myplayer.R;
import com.liuhaoyuan.myplayer.activity.SingerDetailActivity;
import com.liuhaoyuan.myplayer.db.FavoriteDbManager;
import com.liuhaoyuan.myplayer.domain.music.Singer;
import com.liuhaoyuan.myplayer.utils.ConstantValues;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by hyliu on 2017/1/31.
 */

public class FavoriteSingerFragment extends BaseFragment {
    private ArrayList<Singer> mData;

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

            RecyclerView.LayoutManager layoutManager=new GridLayoutManager(getContext(),3);
            SingerAdapter adapter=new SingerAdapter();
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

    private class SingerAdapter extends RecyclerView.Adapter<SingerViewHolder> {

        @Override
        public SingerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_singer, parent, false);
            SingerViewHolder holder=new SingerViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(SingerViewHolder holder, final int position) {
            ImageOptions.Builder builder=new ImageOptions.Builder();
            builder.setFailureDrawableId(R.drawable.nodata);
            ImageOptions imageOptions = builder.build();
            x.image().bind(holder.imageView,mData.get(position).singerPic,imageOptions);
            holder.nameTv.setText(mData.get(position).singerName);
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
                                Intent intent=new Intent(getContext(), SingerDetailActivity.class);
                                intent.putExtra(ConstantValues.ARTIST_NAME, mData.get(position).singerName);
                                intent.putExtra(ConstantValues.ARTIST_ID, mData.get(position).singerId);
                                intent.putExtra(ConstantValues.ARTIST_LOGO, mData.get(position).singerPic);
                                startActivity(intent);
                            }else if (itemId==R.id.menu_delete){
                                FavoriteDbManager dbUtils = FavoriteDbManager.getInstance(getContext());
                                dbUtils.deleteSinger(mData.get(position).singerId);
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
                    Intent intent=new Intent(getContext(), SingerDetailActivity.class);
                    intent.putExtra(ConstantValues.ARTIST_NAME, mData.get(position).singerName);
                    intent.putExtra(ConstantValues.ARTIST_ID, mData.get(position).singerId);
                    intent.putExtra(ConstantValues.ARTIST_LOGO, mData.get(position).singerPic);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

    private class SingerViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView nameTv;
        private Button moreBtn;
        private final FrameLayout frameLayout;

        public SingerViewHolder(View itemView) {
            super(itemView);
            frameLayout = (FrameLayout) itemView.findViewById(R.id.fl_container);
            imageView = (ImageView) itemView.findViewById(R.id.iv_singer);
            nameTv = (TextView) itemView.findViewById(R.id.tv_singer_name);
            moreBtn = (Button) itemView.findViewById(R.id.btn_singer_more);
        }
    }

    private class SimpleTask extends AsyncTask<Context, Integer, ArrayList<Singer>> {

        @Override
        protected ArrayList<Singer> doInBackground(Context... params) {
            FavoriteDbManager dbUtils = FavoriteDbManager.getInstance(params[0]);
            ArrayList<Singer> singers = dbUtils.queryAllSinger();
            return singers;
        }

        @Override
        protected void onPostExecute(ArrayList<Singer> singers) {
            super.onPostExecute(singers);
            if (singers == null) {
                onLoadingComplete(false);
            } else {
                if (singers.size() > 0) {
                    mData = singers;
                } else {
                    mData = null;
                }
                onLoadingComplete(true);
            }
        }
    }
}
