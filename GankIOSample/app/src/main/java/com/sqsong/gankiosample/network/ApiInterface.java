package com.sqsong.gankiosample.network;

import com.sqsong.gankiosample.model.GankBean;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by 青松 on 2016/11/24.
 */

public interface ApiInterface {

    String BASE_URL = "http://gank.io/api/data/";

    @GET("Android/10/{index}")
    Observable<GankBean> getAndroidData(@Path("index") int pageIndex);

    @GET("iOS/10/{index}")
    Observable<GankBean> getIOSData(@Path("index") int pageIndex);

    @GET("前端/10/{index}")
    Observable<GankBean> getWebData(@Path("index") int pageIndex);

}
