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
 * Created by hyliu on 2017/1/13.
 */

public class MusicFragment extends Fragment {

    private View view;
    private String[] titles;
    private ArrayList<BaseFragment> fragments;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = View.inflate(getContext(), R.layout.fragment_music,null);


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    private void initData() {
        titles = new String[]{"最热","歌手","排行榜"};
        int [] icons=new int[]{R.drawable.ic_action_fire,R.drawable.ic_action_people,R.drawable.ic_action_ranks};
        fragments = new ArrayList<>();
        fragments.add(new HotMusicFragment());
        fragments.add(new SingerFragment());
        fragments.add(new RanksFragment());

        TabLayout tabLayout= (TabLayout) view.findViewById(R.id.tab_music);
        ViewPager viewPager= (ViewPager) view.findViewById(R.id.vp_music);
        viewPager.setAdapter(new MusicAdapter(getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        int tabCount = tabLayout.getTabCount();
        for (int i = 0; i < tabCount; i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setIcon(icons[i]);
        }
    }

    class MusicAdapter extends FragmentPagerAdapter{

        public MusicAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return titles.length;
        }

//        @Override
//        public CharSequence getPageTitle(int position) {
//            return titles[position];
//        }
    }
}
