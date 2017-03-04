package com.liuhaoyuan.myplayer.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.AutoTransition;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.liuhaoyuan.myplayer.APP;
import com.liuhaoyuan.myplayer.R;
import com.liuhaoyuan.myplayer.api.DataObserver;
import com.liuhaoyuan.myplayer.api.MusicApi;
import com.liuhaoyuan.myplayer.db.FavoriteDbUtils;
import com.liuhaoyuan.myplayer.domain.music.XiaMiArtistInfo;
import com.liuhaoyuan.myplayer.fragment.BaseFragment;
import com.liuhaoyuan.myplayer.fragment.SingerAlbumFragment;
import com.liuhaoyuan.myplayer.fragment.SingerSongsFragment;
import com.liuhaoyuan.myplayer.utils.ConstantValues;

import org.xutils.x;

import java.util.ArrayList;

public class SingerDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        int currentTheme = APP.getCurrentTheme();
        APP application = (APP) getApplication();
        int currentTheme = application.getCurrentTheme();
        setTheme(currentTheme);
        // 设置一个exit transition
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setAllowEnterTransitionOverlap(true);
            getWindow().setExitTransition(new AutoTransition());//new Slide()  new Fade()
            getWindow().setEnterTransition(new AutoTransition());
            getWindow().setSharedElementEnterTransition(new AutoTransition());
            getWindow().setSharedElementExitTransition(new AutoTransition());
        }
        setContentView(R.layout.activity_singer_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        final String artistID = intent.getStringExtra(ConstantValues.ARTIST_ID);
        final String artistLogo = intent.getStringExtra(ConstantValues.ARTIST_LOGO);
        final String artistName = intent.getStringExtra(ConstantValues.ARTIST_NAME);
//        Bitmap singerPic=intent.getExtras().getParcelable(ConstantValues.SINGER_PIC);

        setTitle("");
        final ImageView imageView = (ImageView) findViewById(R.id.iv_singer_detail);
        TextView nameTv = (TextView) findViewById(R.id.tv_singer_name);
        final TextView desTv = (TextView) findViewById(R.id.tv_des);
        final Button moreBtn = (Button) findViewById(R.id.btn_more);
//        if (singerPic!=null){
//            imageView.setImageBitmap(singerPic);
//        }else {
//            x.image().bind(imageView,artistLogo);
//        }
        x.image().bind(imageView, artistLogo);
        nameTv.setText(artistName);

        final MusicApi qqMusicApi = MusicApi.getInstance();
        qqMusicApi.registerObserver(new DataObserver<XiaMiArtistInfo.Artist>(DataObserver.TYPE_XIAMI_ARTIST) {
            @Override
            public void onComplete(final XiaMiArtistInfo.Artist data) {
                if (data != null) {
                    if (TextUtils.isEmpty(data.profile)) {
                        desTv.setVisibility(View.GONE);
                        moreBtn.setVisibility(View.GONE);
                    } else {
                        final String s = data.profile.replaceAll("&nbsp;", "");
                        desTv.setText(s);
                        moreBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(SingerDetailActivity.this);
                                builder.setTitle("歌手信息");
                                builder.setMessage(s);
                                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();
                            }
                        });
                    }
                    if (!TextUtils.isEmpty(data.logo)) {
                        x.image().bind(imageView, data.logo);
                    }
                } else {
                    desTv.setVisibility(View.GONE);
                    moreBtn.setVisibility(View.GONE);
                }
                qqMusicApi.unregisterObserver(this);
            }
        });
        qqMusicApi.getXiaMiArtist(artistID);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_singer);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FavoriteDbUtils dbUtils = FavoriteDbUtils.getInstance(SingerDetailActivity.this);
                dbUtils.insertSinger(artistID, artistName, artistLogo);
                final Snackbar snackbar = Snackbar.make(view, "收藏成功", Snackbar.LENGTH_LONG);
                snackbar.setAction("知道了", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });
                snackbar.show();
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tl_singer);
        ViewPager viewPager = (ViewPager) findViewById(R.id.vp_singer);
        ArrayList<BaseFragment> list = new ArrayList<>();
        Bundle bundle = new Bundle();
        bundle.putString(ConstantValues.ARTIST_ID, artistID);
        bundle.putString(ConstantValues.ARTIST_NAME, artistName);
        SingerSongsFragment singerSongsFragment = new SingerSongsFragment();
        singerSongsFragment.setArguments(bundle);
        SingerAlbumFragment singerAlbumFragment = new SingerAlbumFragment();
        singerAlbumFragment.setArguments(bundle);
        list.add(singerSongsFragment);
        list.add(singerAlbumFragment);
        SingerPagerAdapter adapter = new SingerPagerAdapter(getSupportFragmentManager(), list);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private class SingerPagerAdapter extends FragmentPagerAdapter {
        private String[] titles = new String[]{"热门单曲", "专辑列表"};

        private ArrayList<BaseFragment> data;

        public SingerPagerAdapter(FragmentManager fm, ArrayList<BaseFragment> data) {
            super(fm);
            this.data = data;
        }

        @Override
        public Fragment getItem(int position) {
            return data.get(position);
        }

        @Override
        public int getCount() {
            return this.data.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
