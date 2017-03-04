package com.liuhaoyuan.myplayer.pager;

import android.app.Activity;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.liuhaoyuan.myplayer.R;

import java.util.ArrayList;

/**
 * Created by liuhaoyuan on 2016/7/21.
 */
public class VideoPager extends BasePager {


    private ViewPager viewPager;
    private TabLayout tableLayout;

    private String[] videoTypes=new String[]{"电影","电视剧","综艺","动漫"};
    private VideoPagerAdapter adapter;

    public VideoPager(Activity activity) {
        super(activity);
    }

    @Override
    public View inintView() {
        title = "视频";
        View view = View.inflate(activity, R.layout.fragment_video, null);
        viewPager = (ViewPager) view.findViewById(R.id.vp_video);
        tableLayout = (TabLayout) view.findViewById(R.id.tl_video);
        return view;
    }

    @Override
    public void initData() {
        ArrayList<NetVideoPager> videoPagers = new ArrayList<>();
        for (int i=0;i<videoTypes.length;i++){
            NetVideoPager videoPager=new NetVideoPager(activity);
            videoPager.setVideoType(videoTypes[i]);
            videoPagers.add(videoPager);
        }

        adapter = new VideoPagerAdapter(videoPagers);
        viewPager.setAdapter(adapter);
        tableLayout.setupWithViewPager(viewPager);
    }

    class VideoPagerAdapter extends PagerAdapter{

        private ArrayList<NetVideoPager> pagers;

        public VideoPagerAdapter(ArrayList<NetVideoPager> pagers){
            this.pagers=pagers;
        }

        @Override
        public int getCount() {
            return pagers.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            NetVideoPager pager=pagers.get(position);
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
            return pagers.get(position).getVideoType();
        }
    }
}
