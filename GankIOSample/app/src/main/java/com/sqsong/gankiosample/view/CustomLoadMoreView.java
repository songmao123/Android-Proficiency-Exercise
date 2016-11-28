package com.sqsong.gankiosample.view;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.sqsong.gankiosample.R;

/**
 * Created by sqsong on 16-11-24.
 */

public class CustomLoadMoreView extends LoadMoreView {
    @Override
    public int getLayoutId() {
        return R.layout.layout_load_more_view;
    }

    @Override
    public boolean isLoadEndGone() {
        return false;
    }

    @Override
    protected int getLoadingViewId() {
        return R.id.loading_view;
    }

    @Override
    protected int getLoadFailViewId() {
        return R.id.loading_failure_view;
    }

    @Override
    protected int getLoadEndViewId() {
        return R.id.loading_complete_view;
    }
}
