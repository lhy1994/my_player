package com.liuhaoyuan.myplayer.view;

/**
 * Created by liuhaoyuan on 2016/8/3.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;


import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

import com.liuhaoyuan.myplayer.R;

public class RefreshListView extends ListView implements AbsListView.OnScrollListener,
        android.widget.AdapterView.OnItemClickListener {
    private static final int STATE_PULL_REFRESH = 0;
    private static final int STATE_RELEASE_REFRESH = 1;
    private static final int STATE_REFRESHING = 2;

    private int currentState = STATE_PULL_REFRESH;
    private View headerView;
    private int startY;
    private int headerHight;
    private TextView tvTitle;
    private TextView tvDate;
//    private ImageView arrImageView;
    private ProgressBar progressBar;
//    private RotateAnimation rotateAnimationUP;
//    private RotateAnimation rotateAnimationDown;

    @SuppressLint("NewApi")
    public RefreshListView(Context context, AttributeSet attrs,
                           int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initHeaderView();
        initFooterView();
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initHeaderView();
        initFooterView();

    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHeaderView();
        initFooterView();

    }

    public RefreshListView(Context context) {
        super(context);
        initHeaderView();
        initFooterView();

    }

    public void initHeaderView() {
        headerView = View.inflate(getContext(), R.layout.refresh_header, null);
        this.addHeaderView(headerView);
        tvTitle = (TextView) headerView.findViewById(R.id.pull_title);
        tvDate = (TextView) headerView.findViewById(R.id.pull_date);
        progressBar = (ProgressBar) headerView.findViewById(R.id.pull_process);

        headerView.measure(0, 0);
        headerHight = headerView.getMeasuredHeight();
        headerView.setPadding(0, -headerHight, 0, 0);
//        initArrAnimation();
        tvDate.setText("最后刷新时间" + getCurrentTime());
    }

    public void initFooterView() {
        footerView = View.inflate(getContext(), R.layout.refresh_footer, null);
        this.addFooterView(footerView);
        footerView.measure(0, 0);
        footerViewHeight = footerView.getMeasuredHeight();
        footerView.setPadding(0, -footerViewHeight, 0, 0);
        this.setOnScrollListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:

                startY = (int) ev.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:

                if (startY == -1) {
                    startY = (int) ev.getRawY();
                }
                if (currentState == STATE_REFRESHING) {
                    break;
                }
                int endY = (int) ev.getRawY();
                int dy = endY - startY;
                if (dy > 0 && getFirstVisiblePosition() == 0)// �����ҵ�ǰ�ǵ�һ��item
                {
                    int padding = dy - headerHight;
                    headerView.setPadding(0, padding, 0, 0);

                    if (padding > 0 && currentState != STATE_RELEASE_REFRESH) {
                        currentState = STATE_RELEASE_REFRESH;
                        refreshState();
                    } else if (padding < 0 && currentState != STATE_PULL_REFRESH) {
                        currentState = STATE_PULL_REFRESH;
                    }
                    return true;
                }

                break;

            case MotionEvent.ACTION_UP:
                startY = -1;
                if (currentState == STATE_RELEASE_REFRESH) {
                    currentState = STATE_REFRESHING;
                    headerView.setPadding(0, 0, 0, 0);
                    refreshState();
                } else if (currentState == STATE_PULL_REFRESH) {
                    headerView.setPadding(0, -headerHight, 0, 0);
                }
                break;

        }
        return super.onTouchEvent(ev);
    }

    private void refreshState() {
        switch (currentState) {
            case STATE_PULL_REFRESH:
                tvTitle.setText("下拉刷新");
//                arrImageView.startAnimation(rotateAnimationDown);
//                arrImageView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                break;

            case STATE_REFRESHING:
                tvTitle.setText("正在刷新");
//                arrImageView.clearAnimation();// ���������������������
//                arrImageView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                if (refreshListener != null) {
                    refreshListener.onRefresh();
                }
                break;
            case STATE_RELEASE_REFRESH:
                tvTitle.setText("松开刷新...");
//                arrImageView.startAnimation(rotateAnimationUP);
//                arrImageView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }

//    private void initArrAnimation() {
//        // ��ͷ���϶���
//        rotateAnimationUP = new RotateAnimation(0, -180,
//                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
//                0.5f);
//        rotateAnimationUP.setDuration(200);
//        rotateAnimationUP.setFillAfter(true);
//
//        rotateAnimationDown = new RotateAnimation(0, -180,
//                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
//                0.5f);
//        rotateAnimationUP.setDuration(200);
//        rotateAnimationUP.setFillAfter(true);
//    }

    OnRefreshListener refreshListener;
    private View footerView;

    public void setOnRefreshListener(OnRefreshListener listener) {
        refreshListener = listener;
    }

    public interface OnRefreshListener {
        void onRefresh();
        void onLoadMore();
    }

    public void onRefreshComplete(boolean success) {
        if (isLoadingMore) {
            footerView.setPadding(0, footerViewHeight, 0, 0);
            isLoadingMore = false;
        } else {
            currentState = STATE_PULL_REFRESH;
            tvTitle.setText("刷新完成");
//            arrImageView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            headerView.setPadding(0, -headerHight, 0, 0);
            if (success) {
                tvDate.setText("最后刷新时间" + getCurrentTime());
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    public String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }

    private boolean isLoadingMore;
    private int footerViewHeight;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE
                || scrollState == SCROLL_STATE_FLING && !isLoadingMore) {
            if (getLastVisiblePosition() == getCount() - 1) {
                footerView.setPadding(0, 0, 0, 0);
                setSelection(getCount());
                isLoadingMore = true;
                if (refreshListener != null) {
                    refreshListener.onLoadMore();
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

    }
    //��װlistview������Զ���ȥheaderview����
    OnItemClickListener itemClickListener;

    @Override
    public void setOnItemClickListener(
            android.widget.AdapterView.OnItemClickListener listener) {
        // TODO Auto-generated method stub
        super.setOnItemClickListener(this);
        itemClickListener = listener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stub
        if(itemClickListener!=null){
            itemClickListener.onItemClick(parent, view, position
                    - getHeaderViewsCount(), id);
        }
    }
}

