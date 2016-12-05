package com.liuhaoyuan.myplayer.pager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;

import com.liuhaoyuan.myplayer.LocalMusicActivity;
import com.liuhaoyuan.myplayer.R;

import java.util.ArrayList;

/**
 * Created by liuhaoyuan on 2016/7/21.
 */
public class MusicPager extends BasePager{


    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String[] titles=new String[]{"热歌","欧美","内地","港台","韩国","日本","民谣","摇滚"};
    private int[] kindIds=new int[]{26,3,5,6,16,17,18,19};
    private MusicPagerAdapter adapter;

    public MusicPager(Activity activity) {
        super(activity);
    }

    @Override
    public View inintView() {
        title="音乐";
        View view= View.inflate(activity, R.layout.pager_music,null);
        tabLayout = (TabLayout) view.findViewById(R.id.tl_music);
        viewPager = (ViewPager) view.findViewById(R.id.vp_music);

        return view;
    }

    @Override
    public void initData() {
        ArrayList<BasePager> pagers=new ArrayList<>();
        for (int i=0;i<titles.length;i++){
            NetMusicPager pager=new NetMusicPager(activity);
            pager.setKindId(kindIds[i]);
            pagers.add(pager);
        }

        adapter = new MusicPagerAdapter(pagers);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    class MusicPagerAdapter extends PagerAdapter{
        public MusicPagerAdapter(ArrayList<BasePager> list){
            this.res=list;
        }

        private ArrayList<BasePager> res;
        @Override
        public int getCount() {
            return res.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            BasePager pager=res.get(position);
            container.addView(pager.rootView);
            pager.initData();
            return pager.rootView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
