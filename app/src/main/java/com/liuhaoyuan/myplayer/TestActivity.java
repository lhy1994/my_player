package com.liuhaoyuan.myplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.liuhaoyuan.myplayer.api.DataObserver;
import com.liuhaoyuan.myplayer.api.MusicApi;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;


public class TestActivity extends AppCompatActivity {

    private static final String APPKEY = "66c85262ac2869e8";
    private static final String SECRET = "3013240e6eba90ada5328cb11e58d8d6";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    public void test(View view){
        final MusicApi qqMusicApi = MusicApi.getInstance();
        qqMusicApi.registerObserver(new DataObserver<String>(DataObserver.TYPE_LYRIC) {
            @Override
            public void onComplete(String data) {
                if (data!=null){
                    Log.e("test",data);
                    Toast.makeText(TestActivity.this,data,Toast.LENGTH_LONG).show();
                }
                qqMusicApi.unregisterObserver(this);
            }
        });
        qqMusicApi.getLyric("109191643");
    }
}
