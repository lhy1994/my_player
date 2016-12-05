package com.liuhaoyuan.myplayer.pager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.liuhaoyuan.myplayer.MusicPlayActivity;
import com.liuhaoyuan.myplayer.R;
import com.liuhaoyuan.myplayer.domain.NetMusicInfo;
import com.liuhaoyuan.myplayer.domain.Song;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by liuhaoyuan on 2016/7/26.
 */
public class NetMusicPager extends BasePager {
    private int kindId;
    private ListView listView;
    private final String SECRET = "27ed84878a794201a8b325c301b031d4";
    private final String APPID = "22447";
    private ArrayList<Song> songlist;
    private NetMusicListAdpater adpater;
    private View headerView;
    private Button playAll;

    public int getKindId() {
        return kindId;
    }

    public void setKindId(int kindId) {
        this.kindId = kindId;
    }

    public NetMusicPager(Activity activity) {
        super(activity);
    }

    @Override
    public View inintView() {
        View view;
        view = View.inflate(activity, R.layout.pager_net_music, null);
        listView = (ListView) view.findViewById(R.id.lv_net_music);
        headerView = View.inflate(activity, R.layout.list_header_net_music, null);
        playAll = (Button) headerView.findViewById(R.id.btn_header_play_all);
        listView.addHeaderView(headerView);
        return view;
    }

    @Override
    public void initData() {
        getDataFromServer();
    }

    public void getDataFromServer() {
        RequestParams request = new RequestParams("http://route.showapi.com/213-4?" + "&showapi_sign=" + SECRET + "&showapi_appid=" + APPID + "&topid=" + kindId);
//        Log.e("tst", "http://route.showapi.com/213-4?" + "&showapi_sign=" + SECRET + "&showapi_appid=" + APPID + "&topid=" + kindId);
        x.http().get(request, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
//                Log.e("test", "result is " + result);
                parseData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
                Toast.makeText(activity, "网络错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void parseData(String result) {
        Gson gson = new Gson();
        NetMusicInfo netMusicInfo = gson.fromJson(result, NetMusicInfo.class);
//        Log.e("tst", netMusicInfo.showapi_res_body.pagebean.toString());
        songlist = netMusicInfo.showapi_res_body.pagebean.songlist;
        adpater = new NetMusicListAdpater();
        listView.setAdapter(adpater);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(activity, MusicPlayActivity.class);
                intent.putExtra("position", position - listView.getHeaderViewsCount());
//                Log.e("position",position+"");
//                Log.e("header",listView.getHeaderViewsCount()+"");
                intent.putExtra("is_remote_play", true);
                Bundle bundle = new Bundle();
                bundle.putSerializable("song_list", songlist);
                intent.putExtras(bundle);
                activity.startActivity(intent);
            }
        });
        playAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, MusicPlayActivity.class);
                intent.putExtra("position", 0);
                intent.putExtra("is_remote_play", true);
                Bundle bundle = new Bundle();
                bundle.putSerializable("song_list", songlist);
                intent.putExtras(bundle);
                activity.startActivity(intent);
            }
        });
    }

    class NetMusicListAdpater extends BaseAdapter {

        @Override
        public int getCount() {
            return songlist.size();
        }

        @Override
        public Object getItem(int i) {
            return songlist.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 1;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            Viewholder holder;
            if (view == null) {
                view = View.inflate(activity, R.layout.list_item_net_music, null);
                holder = new Viewholder();
                holder.number = (TextView) view.findViewById(R.id.tv_net_music_position);
                holder.title = (TextView) view.findViewById(R.id.tv_net_music_title);
                holder.artist = (TextView) view.findViewById(R.id.tv_net_music_artist);
                holder.more = (ImageButton) view.findViewById(R.id.btn_net_music_more);

                view.setTag(holder);
            } else {
                holder = (Viewholder) view.getTag();
            }

            holder.number.setText("" + (position + 1));
            holder.title.setText(songlist.get(position).songname);
            if (TextUtils.isEmpty(songlist.get(position).singername)) {
                holder.artist.setText("未知");
            } else {
                holder.artist.setText(songlist.get(position).singername);
            }
            holder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setItems(new String[]{"删除"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i) {
                                case 0:
                                    songlist.remove(position - listView.getHeaderViewsCount());
                                    adpater.notifyDataSetChanged();
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                    builder.show();
                }
            });
            return view;
        }
    }

    class Viewholder {
        TextView number;
        TextView title;
        TextView artist;
        ImageButton more;
    }
}
