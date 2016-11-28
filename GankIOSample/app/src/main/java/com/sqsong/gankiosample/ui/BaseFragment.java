package com.sqsong.gankiosample.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.sqsong.gankiosample.R;
import com.sqsong.gankiosample.adapter.GankDataAdapter;
import com.sqsong.gankiosample.db.DatabaseManager;
import com.sqsong.gankiosample.model.GankBean;
import com.sqsong.gankiosample.model.GankData;
import com.sqsong.gankiosample.view.CustomLoadMoreView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * The BaseFragment process the common datas, it's child fragment:{@link AndroidFragment}, {@link IOSFragment}, {@link WebFragment}.
 */
public abstract class BaseFragment extends LazyLoadFragment implements SwipeRefreshLayout.OnRefreshListener,
        BaseQuickAdapter.RequestLoadMoreListener {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipe_layout;

    private boolean isPrepared;
    protected int mPageIndex = 1;
    private GankDataAdapter mAdapter;
    private List<GankData> mGankDatas = new ArrayList<>();

    protected abstract Observable<List<GankData>> getObservable();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base, null);
        initView(view);
        initEvents();
        isPrepared = true;
        lazyLoad();
        return view;
    }

    private void initView(View view) {
        swipe_layout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
    }

    private void initEvents() {
        swipe_layout.post(new Runnable() {
            @Override
            public void run() {
                swipe_layout.setRefreshing(true);
            }
        });
        swipe_layout.setOnRefreshListener(this);
        mAdapter = new GankDataAdapter(mGankDatas);
        mAdapter.setEnableLoadMore(true);
        mAdapter.setOnLoadMoreListener(this);
        mAdapter.setLoadMoreView(new CustomLoadMoreView());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(mClickListener);
    }

    private OnItemClickListener mClickListener = new OnItemClickListener() {

        @Override
        public void SimpleOnItemClick(BaseQuickAdapter baseQuickAdapter, View view, int position) {
            GankData gankData = (GankData) baseQuickAdapter.getData().get(position);
            String url = gankData.getUrl();
            Intent intent = new Intent(getActivity(), WebViewActivity.class);
            intent.putExtra(WebViewActivity.WEB_URL, url);
            startActivity(intent);
        }

    };

    @Override
    public void onRefresh() {
        mPageIndex = 1;
        getGankDatas();
    }

    @Override
    protected void onInvisible() {
        super.onInvisible();
        isPrepared = false;
    }

    @Override
    protected void lazyLoad() {
        if(!isPrepared || !isVisible){
            return;
        }
        getGankDatas();
    }

    private void getGankDatas() {
        getObservable().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<List<GankData>>() {
                @Override
                public void call(List<GankData> gankDatas) {
                    // when the first time load data from database, because of the speed is very fast,
                    // the SwipeRefreshLayout refresh mark can not set to false, so we need to set it's mark to false when the layout load complete.
                    swipe_layout.post(new Runnable() {
                        @Override
                        public void run() {
                            swipe_layout.setRefreshing(false);
                        }
                    });
                    processGankData(gankDatas);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    int code = ((HttpException) throwable).code();
                    Toast.makeText(getContext(), "Error Code: " + code, Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void processGankData(List<GankData> gankData) {
        if (gankData == null || gankData.size() < 1) {
            if (mPageIndex != 1) {
                mAdapter.loadMoreEnd();
            }
            return;
        }
        if (mPageIndex == 1) {
            mAdapter.setNewData(gankData);
        } else {
            mAdapter.loadMoreComplete();
            mAdapter.addData(gankData);
        }
    }

    @Override
    public void onLoadMoreRequested() {
        mPageIndex++;
        getGankDatas();
    }

    /**
     * save network data to database
     */
    protected Func1<GankBean, Observable<List<GankData>>> saveDataFunc1 = new Func1<GankBean, Observable<List<GankData>>>() {
        @Override
        public Observable<List<GankData>> call(GankBean gankBean) {
            if (gankBean != null) {
                final List<GankData> results = gankBean.getResults();
                return Observable.create(new Observable.OnSubscribe<List<GankData>>() {
                    @Override
                    public void call(Subscriber<? super List<GankData>> subscriber) {
                        DatabaseManager.getInstance(getContext()).insertGankData(results);
                        subscriber.onNext(results);
                        subscriber.onCompleted();
                    }
                });
            }
            return null;
        }
    };

}
