package com.liuhaoyuan.myplayer.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.liuhaoyuan.myplayer.R;
import com.liuhaoyuan.myplayer.activity.RanksDetailActivity;
import com.liuhaoyuan.myplayer.api.MusicApi;

/**
 * Created by hyliu on 2017/1/15.
 */

public class RanksFragment extends BaseFragment {
    private String[] titles=new String[]{"热歌榜","销量榜","欧美流行榜","内地音乐榜","港台劲歌金榜","韩国M-net周榜","日本Oricon公信榜","民谣榜","摇滚榜"};
    private int[] images=new int[]{R.drawable.rege,R.drawable.xiaoliang,R.drawable.oumei,R.drawable.neidi,R.drawable.gangtai,R.drawable.hanguo,R.drawable.riben,R.drawable.minyao,R.drawable.yaogun};

    private String[] topIds=new String[]{"26", "23","3", "5", "6", "16", "17", "18", "19"};;

    @Override
    public void loadData() {
        MusicApi qqMusicApi = MusicApi.getInstance();
        onLoadingComplete(true);
    }

    @Override
    public View onCreateSuccessView() {
        View view=View.inflate(getContext(),R.layout.fragment_ranks,null);
        RecyclerView recyclerView= (RecyclerView) view.findViewById(R.id.lv_ranks);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getContext());
        RankAdapter adapter=new RankAdapter();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        return view;
    }

    private class RankAdapter extends RecyclerView.Adapter<RankViewHolder>{

        @Override
        public RankViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ranks, parent, false);
            RankViewHolder rankViewHolder=new RankViewHolder(view);
            return rankViewHolder;
        }

        @Override
        public void onBindViewHolder(final RankViewHolder holder, final int position) {
            holder.imageView.setImageResource(images[position]);
            holder.nameTv.setText(titles[position]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getContext(), RanksDetailActivity.class);
                    intent.putExtra("rankName",titles[position]);
                    intent.putExtra("rankPic",images[position]);
                    intent.putExtra("rankId",topIds[position]);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), holder.imageView, getString(R.string.transition_rank_pic));
                        startActivity(intent, options.toBundle());
                    } else {
                        startActivity(intent);
                    }
//                    startActivity(intent);

                }
            });
        }

        @Override
        public int getItemCount() {
            return titles.length;
        }
    }

    private class RankViewHolder extends RecyclerView.ViewHolder{
        private final ImageView imageView;
        private final TextView nameTv;

        public RankViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_ranks);
            nameTv = (TextView) itemView.findViewById(R.id.tv_rank_name);
        }
    }
}
