package com.sqsong.gankiosample.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.sqsong.gankiosample.BaseApplication;
import com.sqsong.gankiosample.R;
import com.sqsong.gankiosample.adapter.GankDataAdapter;
import com.sqsong.gankiosample.db.DatabaseManager;
import com.sqsong.gankiosample.model.GankData;
import com.sqsong.gankiosample.network.RetrofitInstance;
import com.sqsong.gankiosample.util.Util;
import com.sqsong.gankiosample.view.CustomLoadMoreView;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class GankPostFragment extends LazyLoadFragment implements SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {

    public static final int TYPE_ANDROID = 0; // Android
    public static final int TYPE_IOS = 1; // iOS
    public static final int TYPE_WEB = 2; // Web
    private static final String FRAGMENT_TYPE = "fragment_type";

    private RecyclerView recyclerView;

    private SwipeRefreshLayout swipe_layout;
    private int mType;
    private boolean isPrepared;
    protected int mPageIndex = 1;
    private GankDataAdapter mAdapter;
    private List<GankData> mGankDatas = new ArrayList<>();
    private CompositeSubscription mSubscription;

    public static GankPostFragment newInstance(int type) {
        GankPostFragment fragment = new GankPostFragment();
        Bundle args = new Bundle();
        args.putInt(FRAGMENT_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getInt(FRAGMENT_TYPE, TYPE_ANDROID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base, container, false);
        init(view);
        lazyLoad();
        return view;
    }

    private void init(View view) {
        isPrepared = true;
        mSubscription = new CompositeSubscription();
        mAdapter = new GankDataAdapter(mGankDatas);

        initView(view);
        initEvents();
    }

    private void initView(View view) {
        swipe_layout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
    }

    private void initEvents() {
        swipe_layout.setRefreshing(true);
        swipe_layout.setOnRefreshListener(this);
        mAdapter.setEnableLoadMore(true);
        mAdapter.setOnLoadMoreListener(this);
        mAdapter.setLoadMoreView(new CustomLoadMoreView());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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
        fetchGankData();
    }

    @Override
    public void onLoadMoreRequested() {
        mPageIndex++;
        fetchGankData();
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
        fetchGankData();
    }

    private Observable<List<GankData>> getDataObservable() {
        if (Util.isNetworkConnected(BaseApplication.getAppContext())) { // network data
            return RetrofitInstance.getInstance().getPostObservable(mType, mPageIndex);
        } else { // db cache data
            return DatabaseManager.getInstance(getContext()).getCachePostObservable(mType, mPageIndex, 10);
        }
    }

    private void fetchGankData() {
        mSubscription.add(getDataObservable().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<List<GankData>>() {
            @Override
            public void call(List<GankData> gankDatas) {
                processGankData(gankDatas);
            }
        }));
    }

    private void processGankData(List<GankData> gankData) {
        swipe_layout.setRefreshing(false);
        if (gankData == null || gankData.size() < 1) {
            if (mPageIndex == 1) {
                setEmptyView();
            } else {
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

    private void setEmptyView() {
        mAdapter.setEmptyView(LayoutInflater.from(getContext()).inflate(R.layout.layout_empty_post, null));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSubscription != null && mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }

}
