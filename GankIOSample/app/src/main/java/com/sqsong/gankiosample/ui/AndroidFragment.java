package com.sqsong.gankiosample.ui;

import com.sqsong.gankiosample.BaseApplication;
import com.sqsong.gankiosample.db.DatabaseManager;
import com.sqsong.gankiosample.model.GankData;
import com.sqsong.gankiosample.network.RetrofitInstance;
import com.sqsong.gankiosample.util.Util;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class AndroidFragment extends BaseFragment {

    @Override
    public Observable<List<GankData>> getObservable() {
        // network connect, get data from server, or get database cache data.
        if (Util.isNetworkConnected(BaseApplication.getAppContext())) {
            return networkObservable();
        } else {
            return cacheObservable();
        }
        /*return Observable.concat(networkObservable(), cacheObservable()).first(new Func1<List<GankData>, Boolean>() {
            @Override
            public Boolean call(List<GankData> gankDatas) {
                return gankDatas != null;
            }
        });*/
    }

    private Observable<List<GankData>> networkObservable() {
        return RetrofitInstance.getInstance().getAndroidData(mPageIndex)
                .flatMap(saveDataFunc1);
    }

    private Observable<List<GankData>> cacheObservable() {
        return Observable.create(new Observable.OnSubscribe<List<GankData>>() {
            @Override
            public void call(Subscriber<? super List<GankData>> subscriber) {
                List<GankData> gankDatas = DatabaseManager.getInstance(getContext()).queryAndroidDataWithLimit(mPageIndex, 10);
                subscriber.onNext(gankDatas);
                subscriber.onCompleted();
            }
        });
    }

}
