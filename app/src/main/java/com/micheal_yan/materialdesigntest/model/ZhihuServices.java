package com.micheal_yan.materialdesigntest.model;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by micheal-yan on 2017/4/3.
 */

public interface ZhihuServices {

    public static final String URL = "http://news-at.zhihu.com/api/3/";

    @GET("stories/latest")
    Observable<ZhihuModel> getLatestStories();

    @GET("news/{stroyId}")
    Observable<ZhihuDetailModel> getStoryDetail(@Path("stroyId") int id);
}
