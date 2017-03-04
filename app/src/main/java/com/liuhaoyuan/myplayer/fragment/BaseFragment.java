package com.liuhaoyuan.myplayer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.liuhaoyuan.myplayer.R;

/**
 * Created by hyliu on 2017/1/14.
 */

public abstract class BaseFragment extends Fragment {

    private RelativeLayout mContainer;
    private ProgressBar mProgressBar;
    private AppCompatButton mButton;
    private View mSuccessView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=View.inflate(getContext(), R.layout.fragment_base,null);
        mContainer = (RelativeLayout) view.findViewById(R.id.rl_loading);
        mProgressBar = (ProgressBar) view.findViewById(R.id.pb_loading);
        mButton = (AppCompatButton) view.findViewById(R.id.btn_retry);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
            }
        });
    }

    public abstract void loadData();

    public abstract View onCreateSuccessView();

    public void initData(){
        mButton.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        loadData();
    }

    public void onLoadingComplete(boolean isSuccess){

        if (mSuccessView!=null){
            mContainer.removeView(mSuccessView);
        }
        if (isSuccess){
            mSuccessView=onCreateSuccessView();
            mContainer.addView(mSuccessView);
            mProgressBar.setVisibility(View.GONE);
            mButton.setVisibility(View.GONE);
        }else {
            showRetryButton();
        }
    }

    public void showRetryButton(){
        mButton.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }
}
