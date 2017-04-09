package com.liuhaoyuan.myplayer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.liuhaoyuan.myplayer.R;
import com.liuhaoyuan.myplayer.domain.video.YouKuShowCategoryInfo;
import com.liuhaoyuan.myplayer.utils.ConstantValues;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hyliu on 2017/1/13.
 */

public class VideoFragment extends Fragment {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private ArrayList<VideoListFragment> mFragments;
    private List<YouKuShowCategoryInfo.CategoriesBean> mData;

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
        getShowCategory();
    }

    private void getShowCategory(){
        RequestParams params=new RequestParams("https://openapi.youku.com/v2/schemas/show/category.json");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                parseData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(getClass().getSimpleName(), "onError: "+"no category info!");
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
        YouKuShowCategoryInfo categoryInfo = gson.fromJson(result, YouKuShowCategoryInfo.class);
        mData= categoryInfo.getCategories();
        initAdapter();
    }

    private void initAdapter() {
        if (mData!=null){
            mFragments = new ArrayList<>();
            int length=mData.size();
            for (int i=0;i<length;i++){
                VideoListFragment videoListFragment=new VideoListFragment();
                Bundle bundle=new Bundle();
                bundle.putString(ConstantValues.VIDEO_TYPE,mData.get(i).getLabel());
                videoListFragment.setArguments(bundle);
                mFragments.add(videoListFragment);
            }
            VideoFragmentAdapter adapter=new VideoFragmentAdapter(getChildFragmentManager());
            mViewPager.setAdapter(adapter);
            mTabLayout.setupWithViewPager(mViewPager);
        }
    }

    private class VideoFragmentAdapter extends FragmentPagerAdapter{

        public VideoFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (mFragments!=null){
                return mFragments.get(position);
            }
            return null;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mData.get(position).getLabel();
        }
    }

}
