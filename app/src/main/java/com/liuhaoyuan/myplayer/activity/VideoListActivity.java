package com.liuhaoyuan.myplayer.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.liuhaoyuan.myplayer.APP;
import com.liuhaoyuan.myplayer.R;
import com.liuhaoyuan.myplayer.domain.video.NetVideoAddress;
import com.liuhaoyuan.myplayer.domain.video.UrlResult;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by hyliu on 2017/1/26.
 */

public class VideoListActivity extends AppCompatActivity {

    private ListView listView;
    private String programmeId;
    private static final String APPKEY = "66c85262ac2869e8";
    private ArrayList<NetVideoAddress.VideoAddress> addresses;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        int currentTheme = APP.getCurrentTheme();
        APP application = (APP) getApplication();
        int currentTheme = application.getCurrentTheme();
        setTheme(currentTheme);
        setContentView(R.layout.activity_video_list);
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initView();
        initData();
    }

    private void initView() {
        setTitle("视频播放列表");
        listView = (ListView) findViewById(R.id.lv_video_list);
    }

    private void initData() {
        intent = getIntent();
        programmeId = intent.getStringExtra("video_id");

        Bundle bundle = intent.getExtras();
        addresses = (ArrayList<NetVideoAddress.VideoAddress>) bundle.getSerializable("address");

        if (addresses != null && addresses.size() > 0) {
            MySimpleAdapter adapter = new MySimpleAdapter();
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    String url = addresses.get(position).url;
                    getRealVideoUrl(url);
                }
            });
        } else {
            getDataFromServer();
        }
    }

    private void getDataFromServer() {
        final String proID = programmeId;
        RequestParams requestParams = new RequestParams("https://openapi.youku.com/v2/searches/show/address_unite.json");
        requestParams.addParameter("client_id", APPKEY);
        requestParams.addParameter("progammeId", proID);
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                int pos1 = result.indexOf("\"");
                int pos2 = result.indexOf("\"", pos1 + 1);
                String target = result.substring(pos1 + 1, pos2);
                result = result.replace(target, "data");

                Log.e("url", "https://openapi.youku.com/v2/searches/show/address_unite.json?" + "client_id=" + APPKEY + "&progammeId=" + proID);
                //                Log.e("result", result);
                parseData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("address_error", "error...........");
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
        NetVideoAddress netVideoAddress = gson.fromJson(result, NetVideoAddress.class);
        //        Log.e("data",netVideoAddress.toString());
        ArrayList<NetVideoAddress.AddressInfo> infos = netVideoAddress.data;
        if (infos != null && infos.size() > 0) {
            NetVideoAddress.AddressInfo info = infos.get(1);
            addresses = info.addresses;

            MySimpleAdapter adapter = new MySimpleAdapter();
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    String url = addresses.get(position).url;
                    getRealVideoUrl(url);
                }
            });
        }
    }

    private void getRealVideoUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            url = url.replace("oplay", "albumplay");
            Log.e("url", url);
            RequestParams requestParams = new RequestParams("http://api.hi189.net/index.php");
            requestParams.addParameter("apikey", "2CF95AA17FE12415A1137A1354F4EA4F");
            requestParams.addParameter("url", url);
            //            requestParams.addParameter("url","http://www.tudou.com/albumplay/yjcmOqysUEc/pH8SBj0o51A.html");
            requestParams.setConnectTimeout(50000);
            x.http().get(requestParams, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    parseUrl(result);
                    Log.e("real_url", result);
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Toast.makeText(VideoListActivity.this, "播放地址解析出错，试试换个地址或重新点击", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });

        } else {
            Toast.makeText(this, "播放地址为空", Toast.LENGTH_SHORT).show();
        }
    }

    private void parseUrl(String result) {
        Gson gson = new Gson();
        UrlResult urlResult = gson.fromJson(result, UrlResult.class);
        if (!TextUtils.isEmpty(urlResult.mp4)) {
            Intent intent = new Intent(VideoListActivity.this, VideoPlayActivity.class);
            intent.setData(Uri.parse(urlResult.mp4));
            startActivity(intent);
        } else {
            Log.e("error_real_url", "error..........");
            Log.e("error_real_url", urlResult.msg + "");
        }
    }

    private String getVideoType(String type){
        type=type.trim();
        if (type.equals("1")){
            return "正片";
        }else if (type.equals("2")){
            return "预告片";
        }else if (type.equals("3")){
            return "花絮";
        }else {
            return "";
        }
    }


    class MySimpleAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return addresses.size();
        }

        @Override
        public Object getItem(int i) {
            return addresses.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            NetVideoAddress.VideoAddress videoAddress = addresses.get(i);
            if (view==null){
                view=View.inflate(VideoListActivity.this,R.layout.item_video_address_list,null);
            }
            VideoViewHolder holder = VideoViewHolder.getInstance(view);
            if (TextUtils.isEmpty(videoAddress.name)) {
                holder.nameTV.setText("第" + videoAddress.order + "集");
            } else {
                holder.nameTV.setText(videoAddress.name);
            }
            holder.typeTv.setText(getVideoType(videoAddress.video_type));
            Log.e("type",videoAddress.video_type);
            return view;
        }
    }


}

class VideoViewHolder {
    TextView nameTV;
    TextView typeTv;

    VideoViewHolder(View convertView){
        nameTV= (TextView) convertView.findViewById(R.id.tv_video_name);
        typeTv= (TextView) convertView.findViewById(R.id.tv_video_type);
    }

    public static VideoViewHolder getInstance(View convertView) {
        VideoViewHolder holder= (VideoViewHolder) convertView.getTag();
        if (holder==null){
            holder=new VideoViewHolder(convertView);
            convertView.setTag(holder);
        }
        return holder;
    }
}