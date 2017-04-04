package com.micheal_yan.materialdesigntest.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.micheal_yan.materialdesigntest.R;
import com.micheal_yan.materialdesigntest.model.ZhihuDetailModel;
import com.micheal_yan.materialdesigntest.model.ZhihuServices;
import com.micheal_yan.materialdesigntest.util.HtmlUtil;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by micheal-yan on 2017/4/3.
 */

public class DetailActivity extends AppCompatActivity {

    public static final String STORIE_TITLE = "STORIE_TITLE";
    public static final String STORIE_ID = "STORIE_ID";
    public static final String STORIE_IMAGE = "STORIE_IMAGE";

    private Retrofit mRetrofit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        String storyTitle = intent.getStringExtra(STORIE_TITLE);
        String storyImage = intent.getStringExtra(STORIE_IMAGE);
        int storyId = intent.getIntExtra(STORIE_ID, -1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        ImageView imageView = (ImageView) findViewById(R.id.detail_image_view);
        final WebView webView = (WebView) findViewById(R.id.detail_content_text);
        final WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);  //支持js
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // 详情页面数据绑定处理
        collapsingToolbarLayout.setTitle(storyTitle);
        Glide.with(this).load(storyImage).into(imageView);
        mRetrofit = new Retrofit.Builder()
                .baseUrl(ZhihuServices.URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mRetrofit.create(ZhihuServices.class)
                .getStoryDetail(storyId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .map(new Func1<ZhihuDetailModel, String>() {
                    @Override
                    public String call(ZhihuDetailModel zhihuDetailModel) {
                        return HtmlUtil.createHtmlData(zhihuDetailModel.getBody(), zhihuDetailModel.getCss(), zhihuDetailModel.getJs());
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String htmlData) {
                        webView.loadData(htmlData, HtmlUtil.MIME_TYPE, HtmlUtil.ENCODING);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(DetailActivity.this, "数据加载失败ヽ(≧Д≦)ノ", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}
