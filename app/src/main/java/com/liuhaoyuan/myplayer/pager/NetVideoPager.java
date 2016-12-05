package com.liuhaoyuan.myplayer.pager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.liuhaoyuan.myplayer.R;
import com.liuhaoyuan.myplayer.VideoDetailActivity;
import com.liuhaoyuan.myplayer.domain.video.NetVideoInfo;
import com.liuhaoyuan.myplayer.domain.video.NetVideoItem;
import com.liuhaoyuan.myplayer.view.RefreshListView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by liuhaoyuan on 2016/7/29.
 */
public class NetVideoPager extends BasePager {

    private static final String APPKEY = "66c85262ac2869e8";
    private static final String SECRET = "3013240e6eba90ada5328cb11e58d8d6";

    private String videoType;
    private RefreshListView listView;
    private ArrayList<NetVideoItem> videoList;
    private NetVideoListAdapter adapter;
    private int currentPage=1;

    public String getVideoType() {
        return videoType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    public NetVideoPager(Activity activity) {
        super(activity);
    }

    @Override
    public View inintView() {
        View view = View.inflate(activity, R.layout.pager_net_video, null);
        listView = (RefreshListView) view.findViewById(R.id.lv_net_video);
        listView.setOnRefreshListener(new RefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDataFromServer();
            }

            @Override
            public void onLoadMore() {
                getMoreDataFromServer();
            }
        });
        return view;
    }

    private void getMoreDataFromServer() {
        currentPage++;
        RequestParams requestParams=new RequestParams("https://openapi.youku.com/v2/searches/show/top_unite.json");
        requestParams.addParameter("client_id",APPKEY);
        requestParams.addParameter("category",getVideoType());
        requestParams.addParameter("headnum","1");
        requestParams.addParameter("tailnum","1");
        requestParams.addParameter("page",currentPage+"");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("more",result);
                parseData(result,true);
                listView.onRefreshComplete(true);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                listView.onRefreshComplete(false);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    public void initData() {
        getDataFromServer();
    }

    public void getDataFromServer() {
        currentPage=1;
        RequestParams requestParams=new RequestParams("https://openapi.youku.com/v2/searches/show/top_unite.json");
        requestParams.addParameter("client_id",APPKEY);
        requestParams.addParameter("category",getVideoType());
        requestParams.addParameter("headnum","1");
        requestParams.addParameter("tailnum","1");

        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("result",result);
                parseData(result,false);
                listView.onRefreshComplete(true);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("error","get video error..............");
                listView.onRefreshComplete(false);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void parseData(String result ,boolean isMore) {
        Gson gson=new Gson();
        NetVideoInfo netVideoInfo = gson.fromJson(result, NetVideoInfo.class);
        if (!isMore){

            videoList = netVideoInfo.data;
//        for (NetVideoItem item:videoList){
//            Log.e("item",item.toString());
//        }
            adapter = new NetVideoListAdapter();
            listView.setAdapter(adapter);
        }else {
            ArrayList<NetVideoItem> moreVideos = netVideoInfo.data;
            videoList.addAll(moreVideos);
            if (adapter!=null){
                adapter.notifyDataSetChanged();
            }
        }

    }

    class NetVideoListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return videoList.size();
        }

        @Override
        public Object getItem(int i) {
            return videoList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view==null){
                view=View.inflate(activity,R.layout.list_item_net_video,null);
                holder=new ViewHolder();

                holder.name= (TextView) view.findViewById(R.id.tv_net_video_name);
                holder.score= (TextView) view.findViewById(R.id.tv_net_video_score);
                holder.actor= (TextView) view.findViewById(R.id.tv_net_video_actor);
                holder.brief= (TextView) view.findViewById(R.id.tv_net_video_brief);
                holder.imageView= (ImageView) view.findViewById(R.id.iv_net_video);
                holder.more= (Button) view.findViewById(R.id.btn_net_video_more);

                view.setTag(holder);
            }else {
                holder= (ViewHolder) view.getTag();
            }
            holder.name.setText(videoList.get(position).name);

            String score=videoList.get(position).score;
            holder.score.setText("评分 "+score.substring(0,score.indexOf(".")+2));

            StringBuilder stringBuilder=new StringBuilder();
            stringBuilder.append("主演：");
            for (String s:videoList.get(position).performer){
                stringBuilder.append(" "+s);
            }
            holder.actor.setText(stringBuilder.toString());

            holder.brief.setText(videoList.get(position).brief);

            final ImageOptions.Builder builder=new ImageOptions.Builder();
            x.image().bind(holder.imageView,videoList.get(position).pic);

            holder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(activity, VideoDetailActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("video_info",videoList.get(position));
                    intent.putExtras(bundle);
                    activity.startActivity(intent);
                }
            });
            return view;
        }
    }

    class ViewHolder{
        ImageView imageView;
        TextView name;
        TextView score;
        TextView actor;
        TextView brief;
        Button more;
    }
}
