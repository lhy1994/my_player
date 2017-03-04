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

import java.util.ArrayList;

/**
 * Created by hyliu on 2017/1/31.
 */

public class FavoriteFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=View.inflate(getContext(), R.layout.fragment_favorite,null);
        tabLayout = (TabLayout) view.findViewById(R.id.tab_favorite);
        viewPager = (ViewPager) view.findViewById(R.id.vp_favorite);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayList<BaseFragment> fragments=new ArrayList<>();
        fragments.add(new FavoriteSongFragment());
        fragments.add(new FavoriteSingerFragment());
        fragments.add(new FavoriteVideoFragment());
        FavoriteAdapter adapter=new FavoriteAdapter(getChildFragmentManager(),fragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private class FavoriteAdapter extends FragmentPagerAdapter{

        private ArrayList<BaseFragment> fragments;
        private String [] titles=new String[]{"收藏歌曲","收藏歌手","收藏视频"};
        public FavoriteAdapter(FragmentManager fm,ArrayList<BaseFragment> fragments) {
            super(fm);
            this.fragments=fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
