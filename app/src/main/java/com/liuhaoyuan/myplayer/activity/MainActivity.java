package com.liuhaoyuan.myplayer.activity;

import android.Manifest;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.ChangeClipBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeScroll;
import android.transition.ChangeTransform;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.liuhaoyuan.myplayer.APP;
import com.liuhaoyuan.myplayer.R;
import com.liuhaoyuan.myplayer.TestActivity;
import com.liuhaoyuan.myplayer.fragment.DownloadFragment;
import com.liuhaoyuan.myplayer.fragment.FavoriteFragment;
import com.liuhaoyuan.myplayer.fragment.HistoryFragment;
import com.liuhaoyuan.myplayer.fragment.LocalFragment;
import com.liuhaoyuan.myplayer.fragment.MusicFragment;
import com.liuhaoyuan.myplayer.fragment.ThemeFragment;
import com.liuhaoyuan.myplayer.fragment.VideoFragment;
import com.liuhaoyuan.myplayer.utils.ConstantValues;
import com.liuhaoyuan.myplayer.utils.MusicUtils;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private int mCurrentFragmentId;
    private SearchView mSearchView;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private HashMap<Integer, Fragment> fragmentHashMap = new HashMap<>();
    private MyBroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        int currentTheme = APP.getCurrentTheme();
        APP application = (APP) getApplication();
        int currentTheme = application.getCurrentTheme();
        setTheme(currentTheme);

        // 允许使用transitions
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

// 设置一个exit transition
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(new AutoTransition());//new Slide()  new Fade()
            getWindow().setEnterTransition(new AutoTransition());
            getWindow().setSharedElementEnterTransition(new AutoTransition());
            getWindow().setSharedElementExitTransition(new AutoTransition());
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_music);
        mCurrentFragmentId = R.id.nav_music;

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        MusicFragment musicFragment = (MusicFragment) getFragment(R.id.nav_music);
        transaction.replace(R.id.fl_main, musicFragment);
        transaction.commit();
        navigationView.setCheckedItem(R.id.nav_music);
        setTitle(R.string.navigation_menu_music);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE
                );
            }
        }
        mReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter=new IntentFilter(ConstantValues.ACTION_CHANGE_THEME);
        registerReceiver(mReceiver,intentFilter);
    }

    private class MyBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            recreate();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver!=null){
            unregisterReceiver(mReceiver);
        }
    }

    private Fragment getFragment(int fragmentId) {
        Fragment fragment = fragmentHashMap.get(fragmentId);
        if (fragment == null) {
            switch (fragmentId) {
                case R.id.nav_music:
                    fragment = new MusicFragment();
                    fragmentHashMap.put(R.id.nav_music, fragment);
                    break;
                case R.id.nav_video:
                    fragment = new VideoFragment();
                    fragmentHashMap.put(R.id.nav_video, fragment);
                    break;
                case R.id.nav_local:
                    fragment = new LocalFragment();
                    fragmentHashMap.put(R.id.nav_local, fragment);
                    break;
                case R.id.nav_favorite:
                    fragment = new FavoriteFragment();
                    fragmentHashMap.put(R.id.nav_favorite, fragment);
                    break;
                case R.id.nav_history:
                    fragment=new HistoryFragment();
                    fragmentHashMap.put(R.id.nav_history,fragment);
                    break;
                case R.id.nav_theme:
                    fragment=new ThemeFragment();
                    fragmentHashMap.put(R.id.nav_theme,fragment);
                    break;
            }
        }
        return fragment;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        if (mCurrentFragmentId == R.id.nav_music || mCurrentFragmentId == R.id.nav_video) {
            menu.findItem(R.id.action_search).setVisible(true);
            // Get the SearchView and set the searchable configuration
            final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
            // Assumes current activity is the searchable activity
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            mSearchView.setIconifiedByDefault(true);
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                    intent.putExtra(ConstantValues.SEARCH_STRING, query);
                    intent.putExtra(ConstantValues.FRAGMENT_ID, mCurrentFragmentId);
                    startActivity(intent);
                    menu.findItem(R.id.action_search).collapseActionView();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        } else {
            menu.findItem(R.id.action_search).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, TestActivity.class));
            return true;
        } else if (id == R.id.action_play) {
            if (MusicUtils.HAS_PLAYLIST) {
                MusicUtils.playMusic(this, 0, false, null);
            } else {
                CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main);
                final Snackbar snackbar = Snackbar.make(coordinatorLayout, "当前播放列表为空,请先添加歌曲", Snackbar.LENGTH_LONG);
                snackbar.setAction("知道了", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });
                snackbar.show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (mCurrentFragmentId != id) {
            Fragment fragment = getFragment(id);
            if (fragment != null) {
                switch (id) {
                    case R.id.nav_music:
                        setTitle(R.string.navigation_menu_music);
                        break;
                    case R.id.nav_video:
                        setTitle(R.string.navigation_menu_video);
                        break;
                    case R.id.nav_local:
                        setTitle(R.string.navigation_menu_local);
                        break;
                    case R.id.nav_favorite:
                        setTitle(R.string.navigation_menu_favorite);
                        break;
                    case R.id.nav_history:
                        setTitle(R.string.navigation_menu_history);
                        break;
                    case R.id.nav_theme:
                        setTitle(R.string.navigation_menu_theme);
                        break;
                    default:
                        break;
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fl_main, fragment);
                transaction.commit();

                getSupportActionBar().invalidateOptionsMenu();
                mCurrentFragmentId = id;
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
