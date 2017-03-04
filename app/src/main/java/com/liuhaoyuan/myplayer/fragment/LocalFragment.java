package com.liuhaoyuan.myplayer.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.liuhaoyuan.myplayer.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hyliu on 2017/1/26.
 */

public class LocalFragment extends Fragment {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ArrayList<BaseFragment> mFragments;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.fragment_local, null);
        mTabLayout = (TabLayout) view.findViewById(R.id.tab_local);
        mViewPager = (ViewPager) view.findViewById(R.id.vp_local);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();

    }

    private void initData() {
        mFragments = new ArrayList<>();
        mFragments.add(new LocalMusicFragment());
        mFragments.add(new LocalVideoFragment());
        LocalAdapter adapter=new LocalAdapter(getChildFragmentManager());
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private class LocalAdapter extends FragmentPagerAdapter{
        private String[] titles=new String[]{"本地音乐","本地视频"};

        public LocalAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }

}
