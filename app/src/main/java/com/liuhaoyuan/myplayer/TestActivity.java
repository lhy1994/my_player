package com.liuhaoyuan.myplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.liuhaoyuan.myplayer.utils.MD5Utils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.vov.vitamio.utils.StringUtils;

public class TestActivity extends AppCompatActivity {

    private static final String APPKEY = "66c85262ac2869e8";
    private static final String SECRET = "3013240e6eba90ada5328cb11e58d8d6";
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        textView = (TextView) findViewById(R.id.tv_data);
        Button button = (Button) findViewById(R.id.btn_test_get);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDataFromSer();
            }
        });
    }

    private void getDataFromSer() {
        String time = System.currentTimeMillis()/1000 + "";

        HashMap<String,String> para=new HashMap<>();
        para.put("action","youku.search.video.keyword.get");
        para.put("client_id",APPKEY);
        para.put("access_token","");
        para.put("format","json");
        para.put("timestamp",time+"");
        para.put("version","3.0");

        StringBuilder signBuilder=new StringBuilder();
        signBuilder.append("action");
        signBuilder.append("youku.search.video.keyword.get");
        signBuilder.append("action");
        signBuilder.append("youku.search.video.keyword.get");
        signBuilder.append("access_token");
        signBuilder.append("access_token");
        signBuilder.append("client_id");
        signBuilder.append(APPKEY);
        signBuilder.append("client_id");
        signBuilder.append(APPKEY);
        signBuilder.append("format");
        signBuilder.append("json");
        signBuilder.append("format");
        signBuilder.append("json");
//        signBuilder.append("keyword");
//        signBuilder.append("刘德华");
        signBuilder.append("timestamp");
        signBuilder.append(para.get("timestamp"));
        signBuilder.append("timestamp");
        signBuilder.append(para.get("timestamp"));
        signBuilder.append("version");
        signBuilder.append(para.get("version"));
        signBuilder.append("version");
        signBuilder.append(para.get("version"));


//        String sign = signApiRequest(para,SECRET);

//        String sign=MD5Utils.encode(signBuilder.toString());
        RequestParams requestParams=new RequestParams("https://openapi.youku.com/v2/searches/video/by_keyword.json");

//        StringBuilder syspara=new StringBuilder();
//        syspara.append("{");
//        String[] keys = para.keySet().toArray(new String[0]);
//        for (String key:keys){
//            syspara.append("\"");
//            syspara.append(key);
//            syspara.append("\"");
//            syspara.append(":");
//            syspara.append("\"");
//            syspara.append(para.get(key));
//            syspara.append("\"");
//            syspara.append(",");
//        }
//        syspara.append("\"");
//        syspara.append("sign");
//        syspara.append("\"");
//        syspara.append(":");
//        syspara.append("\"");
//        syspara.append(sign);
//        syspara.append("\"");
//        syspara.append("}");
//        Log.e("sys",syspara.toString());
//        requestParams.addParameter("opensysparams",syspara.toString());
        requestParams.addParameter("client_id",APPKEY);
        requestParams.addParameter("keyword","大圣归来");
//        requestParams.addParameter("category","电影");
//        requestParams.addParameter("caller","NOVA");
//        requestParams.addParameter("progammeId","306517");

        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                result=result.replace("\\","");
                Log.e("result", result);
                textView.setText(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("error", "youku error");
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    public static String signApiRequest(HashMap<String, String> params, String secret) {
        // 第一步：检查参数是否已经排序
        String[] keys = params.keySet().toArray(new String[0]);
        Arrays.sort(keys);

        // 第二步：把所有参数名和参数值串在一起
        StringBuilder query = new StringBuilder();
        for (String key : keys) {
            String value = params.get(key);
            if (key!=null && value!=null) {
                query.append(key).append(value);
            }
        }

        // 第三步：使用MD5/HMAC加密
        query.append(secret);
        Log.e("para",query.toString());
        //32位小写md5加密
        try {
            return MD5Utils.encode(URLEncoder.encode(query.toString(),"utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }
    public String convert(String utfString){
        StringBuilder sb = new StringBuilder();
        int i = -1;
        int pos = 0;

        while((i=utfString.indexOf("\\u", pos)) != -1){
            sb.append(utfString.substring(pos, i));
            if(i+5 < utfString.length()){
                pos = i+6;
                sb.append((char)Integer.parseInt(utfString.substring(i+2, i+6), 16));
            }
        }

        return sb.toString();
    }
}
