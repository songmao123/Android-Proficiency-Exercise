package com.sqsong.gankiosample.ui;

import com.sqsong.gankiosample.BaseApplication;
import com.sqsong.gankiosample.db.DatabaseManager;
import com.sqsong.gankiosample.model.GankData;
import com.sqsong.gankiosample.network.RetrofitInstance;
import com.sqsong.gankiosample.util.Util;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class WebFragment extends BaseFragment {

    @Override
    public Observable<List<GankData>> getObservable() {
        if (Util.isNetworkConnected(BaseApplication.getAppContext())) {
            return networkObservable();
        } else {
            return cacheObservable();
        }
    }

    private Observable<List<GankData>> networkObservable() {
        return RetrofitInstance.getInstance().getWebData(mPageIndex)
                .flatMap(saveDataFunc1);
    }

    private Observable<List<GankData>> cacheObservable() {
        return Observable.create(new Observable.OnSubscribe<List<GankData>>() {
            @Override
            public void call(Subscriber<? super List<GankData>> subscriber) {
                List<GankData> gankDatas = DatabaseManager.getInstance(getContext()).queryWebDataWithLimit(mPageIndex, 10);
                subscriber.onNext(gankDatas);
                subscriber.onCompleted();
            }
        });
    }

}
