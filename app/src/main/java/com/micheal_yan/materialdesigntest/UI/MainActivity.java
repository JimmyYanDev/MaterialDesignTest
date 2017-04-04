package com.micheal_yan.materialdesigntest.UI;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.micheal_yan.materialdesigntest.R;
import com.micheal_yan.materialdesigntest.model.ZhihuModel;
import com.micheal_yan.materialdesigntest.model.ZhihuServices;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNav;
    private FloatingActionButton mFab;

    private Retrofit mClient;
    private List<ZhihuModel.StoriesBean> data = new ArrayList<>();
    private MyRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // 刷新逻辑处理
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData(0);
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(MainActivity.this, "数据已更新", Toast.LENGTH_SHORT).show();
            }
        });

        // 悬浮按钮点击事件处理
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.scrollToPosition(0);
            }
        });

        // 导航栏内容点击事件处理
        mNav = (NavigationView) findViewById(R.id.nav_view);
        mNav.setCheckedItem(R.id.nav_home);
        mNav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDrawerLayout.closeDrawer(GravityCompat.START, true);
                return true;
            }
        });

        // Toolbar处理
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        // 设置标题要在setSupportActionBar之前才有效
        mToolbar.setTitle("Title");
        mToolbar.setSubtitle("subtitle");
        mToolbar.setLogo(R.mipmap.ic_launcher_round);
        setSupportActionBar(mToolbar);
        // 设置导航图标Navi Menu.png要在setSupportActionBar之后
//        mToolbar.setNavigationIcon(R.drawable.navi_button);
//        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mDrawerLayout.openDrawer(GravityCompat.START, true);
//            }
//        });
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.navi_button);
        }

        // 使用Retrofit获取数据
        mClient = new Retrofit.Builder()
                .baseUrl(ZhihuServices.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        adapter = new MyRecyclerViewAdapter(data);
        getData(data.size());

        // recyclerView处理
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 获取首页数据
     * @param pos 数据更新显示位置
     */
    private void getData(final int pos) {
        mClient.create(ZhihuServices.class)
                .getLatestStories()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ZhihuModel>() {
                    @Override
                    public void call(ZhihuModel zhihuModel) {
                        data.clear();
                        data.addAll(pos, zhihuModel.getStories());
                        Log.e(TAG, "loading...");
                        adapter.notifyItemRangeChanged(pos, data.size());
                        recyclerView.scrollToPosition(pos+1);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(MainActivity.this, "获取信息失败！", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.backup:
                Toast.makeText(this, "You clicked Backup", Toast.LENGTH_SHORT).show();
                break;
            case R.id.delete:
                Toast.makeText(this, "You clicked Delete", Toast.LENGTH_SHORT).show();
                break;
            case R.id.settings:
                Toast.makeText(this, "You clicked Settings", Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START, true);
                break;
            default:
        }
        return true;
    }
}
