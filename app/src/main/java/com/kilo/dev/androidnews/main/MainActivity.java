package com.kilo.dev.androidnews.main;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.kilo.dev.androidnews.Global;
import com.kilo.dev.androidnews.R;
import com.kilo.dev.androidnews.common.BaseActivity;
import com.kilo.dev.androidnews.net.HomePageData;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.kilo.dev.androidnews.Appconfig.getMainDataA;
import static com.kilo.dev.androidnews.Appconfig.getMainDataB;
import static com.kilo.dev.androidnews.Appconfig.urlHostCm;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_main_list)
    RecyclerView rvMainList;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.layout_refresh)
    SmartRefreshLayout layoutRefresh;

    private int currentHomepage = 0;
    private NewsCardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvMainList.smoothScrollToPosition(0);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initData();

    }

    private void initData() {
        EasyHttp.get(getMainDataA + currentHomepage + getMainDataB)
                .baseUrl(urlHostCm)
                .readTimeOut(8 * 1000)//局部定义读超时
                .writeTimeOut(8 * 1000)
                .connectTimeout(8 * 1000)
//                    .params("name","张三")
                .timeStamp(true)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        ToastUtils.showShort(e.getMessage());
                    }

                    @Override
                    public void onSuccess(String response) {
                        if (!TextUtils.isEmpty(response)) {
                            Global.getInstance().setCurrentHomePageData(gson.fromJson(response, HomePageData.class));
                            initList();
                            currentHomepage += 20;
                        }
                    }
                });
    }

    private void initList() {
        rvMainList.setLayoutManager(new LinearLayoutManager(mActivity));
        adapter = new NewsCardAdapter(mActivity);
        rvMainList.setAdapter(adapter);
        layoutRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                loadMoreData(0);
            }
        });
        layoutRefresh.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                loadMoreData(currentHomepage);
            }
        });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        //开始轮播
        if (adapter != null) {
            adapter.getHeaderBanner().startAutoPlay();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //结束轮播
        if (adapter != null) {
            adapter.getHeaderBanner().stopAutoPlay();
        }
    }

    private void loadMoreData(int page) {
        EasyHttp.get(getMainDataA + page + getMainDataB)
                .baseUrl(urlHostCm)
                .readTimeOut(8 * 1000)//局部定义读超时
                .writeTimeOut(8 * 1000)
                .connectTimeout(8 * 1000)
//                    .params("name","张三")
                .timeStamp(true)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        ToastUtils.showShort(e.getMessage());
                    }

                    @Override
                    public void onSuccess(String response) {
                        if (!TextUtils.isEmpty(response)) {
                            if (page == 0) {
                                layoutRefresh.finishRefresh();
                                Global.getInstance().setCurrentHomePageData(gson.fromJson(response, HomePageData.class));
                                currentHomepage = 20;
                                Snackbar.make(rvMainList, "已刷新", Snackbar.LENGTH_SHORT).show();
                            } else {
                                layoutRefresh.finishLoadmore();
                                Global.getInstance().getCurrentHomePageData().getT1348647909107()
                                        .addAll(gson.fromJson(response, HomePageData.class).getT1348647909107());
                                adapter.notifyDataSetChanged();
                                currentHomepage += 20;
                            }
                        }
                    }
                });
    }
}
