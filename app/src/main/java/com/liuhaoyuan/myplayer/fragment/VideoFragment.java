package com.liuhaoyuan.myplayer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liuhaoyuan.myplayer.R;
import com.liuhaoyuan.myplayer.utils.ConstantValues;

import java.util.ArrayList;

/**
 * Created by hyliu on 2017/1/13.
 */

public class VideoFragment extends Fragment {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private String[] videoTypes=new String[]{"电影","电视剧","综艺","动漫"};
    private ArrayList<VideoListFragment> mFragments;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.fragment_video, null);
        mViewPager = (ViewPager) view.findViewById(R.id.vp_video);
        mTabLayout = (TabLayout) view.findViewById(R.id.tl_video);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragments = new ArrayList<>();
        for (int i=0;i<videoTypes.length;i++){
            VideoListFragment videoListFragment=new VideoListFragment();
            Bundle bundle=new Bundle();
            bundle.putString(ConstantValues.VIDEO_TYPE,videoTypes[i]);
            videoListFragment.setArguments(bundle);
            mFragments.add(videoListFragment);
        }
        VideoFramentAdapter adapter=new VideoFramentAdapter(getChildFragmentManager());
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private class VideoFramentAdapter extends FragmentPagerAdapter{

        public VideoFramentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return videoTypes.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return videoTypes[position];
        }
    }

}
