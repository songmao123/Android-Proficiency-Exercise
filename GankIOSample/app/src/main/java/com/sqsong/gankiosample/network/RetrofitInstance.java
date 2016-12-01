package com.sqsong.gankiosample.network;

import com.sqsong.gankiosample.BaseApplication;
import com.sqsong.gankiosample.BuildConfig;
import com.sqsong.gankiosample.db.DatabaseManager;
import com.sqsong.gankiosample.model.GankBean;
import com.sqsong.gankiosample.model.GankData;
import com.sqsong.gankiosample.ui.GankPostFragment;
import com.sqsong.gankiosample.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by 青松 on 2016/11/24.
 */
public class RetrofitInstance {

    private static ApiInterface apiService = null;
    private static Retrofit retrofit = null;
    private static OkHttpClient okHttpClient = null;

    private void init() {
        initOkHttp();
        initRetrofit();
        apiService = retrofit.create(ApiInterface.class);
    }

    private RetrofitInstance() {
        init();
    }

    public static RetrofitInstance getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final RetrofitInstance INSTANCE = new RetrofitInstance();
    }

    private static void initOkHttp() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            // https://drakeet.me/retrofit-2-0-okhttp-3-0-config
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            builder.addInterceptor(loggingInterceptor);
        }
        // 缓存 http://www.jianshu.com/p/93153b34310e
        File cacheFile = new File(BaseApplication.cacheDir, "/NetCache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 50);
        Interceptor cacheInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (!Util.isNetworkConnected(BaseApplication.getAppContext())) {
                    request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
                }
                Response response = chain.proceed(request);
                if (Util.isNetworkConnected(BaseApplication.getAppContext())) {
                    int maxAge = 0;
                    // 有网络时 设置缓存超时时间0个小时
                    response.newBuilder().header("Cache-Control", "public, max-age=" + maxAge).build();
                } else {
                    // 无网络时，设置超时为4周
                    int maxStale = 60 * 60 * 24 * 28;
                    response.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale).build();
                }
                return response;
            }
        };
        builder.cache(cache).addInterceptor(cacheInterceptor);
        //设置超时
        builder.connectTimeout(15, TimeUnit.SECONDS);
        builder.readTimeout(20, TimeUnit.SECONDS);
        builder.writeTimeout(20, TimeUnit.SECONDS);
        //错误重连
        builder.retryOnConnectionFailure(true);
        okHttpClient = builder.build();
    }

    private static void initRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(ApiInterface.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    public Observable<GankBean> getObservable(int type, int pageIndex) {
        if (type == GankPostFragment.TYPE_ANDROID) {
            return apiService.getAndroidData(pageIndex);
        } else if (type == GankPostFragment.TYPE_IOS) {
            return apiService.getIOSData(pageIndex);
        } else {
            return apiService.getWebData(pageIndex);
        }
    }

    public Observable<List<GankData>> getPostObservable(int type, int pageIndex) {
        return getObservable(type, pageIndex).map(new Func1<GankBean, List<GankData>>() {
            @Override
            public List<GankData> call(GankBean gankBean) {
                return gankBean == null ? null : gankBean.getResults();
            }
        }).doOnNext(new Action1<List<GankData>>() {
            @Override
            public void call(List<GankData> gankDatas) {
                // network data cache to database
                DatabaseManager.getInstance(BaseApplication.getAppContext()).insertGankData(gankDatas);
            }
        });
    }

}
