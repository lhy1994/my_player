package com.liuhaoyuan.myplayer;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.liuhaoyuan.myplayer.domain.video.NetVideoAddress;
import com.liuhaoyuan.myplayer.domain.video.UrlResult;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

public class VideoListActivity extends AppCompatActivity {

    private ListView listView;
    private String programmeId;
    private static final String APPKEY = "66c85262ac2869e8";
    private ArrayList<NetVideoAddress.VideoAddress> addresses;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        initView();
        initData();
    }

    private void initView(){
        setTitle("视频播放列表");
        listView = (ListView) findViewById(R.id.lv_video_list);
    }

    private void initData(){
        intent = getIntent();
//        programmeId = intent.getStringExtra("video_id");

        Bundle bundle = intent.getExtras();
        addresses= (ArrayList<NetVideoAddress.VideoAddress>) bundle.getSerializable("address");

        if (addresses!=null && addresses.size()>0){
            MySimpleAdapter adapter=new MySimpleAdapter();
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

    private void getDataFromServer() {
        final String proID=programmeId;
        RequestParams requestParams=new RequestParams("https://openapi.youku.com/v2/searches/show/address_unite.json");
        requestParams.addParameter("client_id",APPKEY);
        requestParams.addParameter("progammeId",proID);
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                int pos1=result.indexOf("\"");
                int pos2=result.indexOf("\"",pos1+1);
                String target=result.substring(pos1+1,pos2);
                result=result.replace(target,"data");

                Log.e("url","https://openapi.youku.com/v2/searches/show/address_unite.json?"+"client_id="+APPKEY+"&progammeId="+proID);
//                Log.e("result", result);
                parseData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("address_error","error...........");
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
        Gson gson=new Gson();
        NetVideoAddress netVideoAddress = gson.fromJson(result, NetVideoAddress.class);
//        Log.e("data",netVideoAddress.toString());
        ArrayList<NetVideoAddress.AddressInfo> infos = netVideoAddress.data;
        for (NetVideoAddress.AddressInfo info:infos){
            Log.e("info",info.toString());
        }
        if (infos!=null&&infos.size()>0){
            NetVideoAddress.AddressInfo info = infos.get(1);
            addresses = info.addresses;

            MySimpleAdapter adapter=new MySimpleAdapter();
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
        if (!TextUtils.isEmpty(url)){
            url=url.replace("oplay","albumplay");
            Log.e("url",url);
            RequestParams requestParams=new RequestParams("http://api.hi189.net/index.php");
            requestParams.addParameter("apikey","2CF95AA17FE12415A1137A1354F4EA4F");
            requestParams.addParameter("url",url);
//            requestParams.addParameter("url","http://www.tudou.com/albumplay/yjcmOqysUEc/pH8SBj0o51A.html");
            requestParams.setConnectTimeout(50000);
            x.http().get(requestParams, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    parseUrl(result);
                    Log.e("real_url",result);
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Toast.makeText(VideoListActivity.this,"播放地址解析出错，试试换个地址或重新点击",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });

        }else {
            Toast.makeText(this,"播放地址为空",Toast.LENGTH_SHORT).show();
        }
    }

    private void parseUrl(String result) {
        Gson gson=new Gson();
        UrlResult urlResult = gson.fromJson(result, UrlResult.class);
        if (!TextUtils.isEmpty(urlResult.mp4)){
            Intent intent=new Intent(VideoListActivity.this,VideoPlayActivity.class);
            intent.setData(Uri.parse(urlResult.mp4));
            startActivity(intent);
        }else {
            Log.e("error_real_url","error..........");
            Log.e("error_real_url",urlResult.msg+"");
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
            TextView textView=new TextView(VideoListActivity.this);
            textView.setLines(2);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setTextSize(18);
            if (TextUtils.isEmpty(videoAddress.name)){
                textView.setText("第"+videoAddress.order+"集");
            }else {
                textView.setText(videoAddress.name);
            }
            return textView;
        }
    }
}
