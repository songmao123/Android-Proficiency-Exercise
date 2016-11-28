package com.sqsong.gankiosample.adapter;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.sqsong.gankiosample.R;

/**
 * Created by 青松 on 2016/11/24.
 */

public class GankViewHolder {

    public TextView desc_tv;
    public View border_view;
    public TextView author_tv;
    public ViewPager viewpager;
    public TextView publish_time_tv;
    public LinearLayout indicator_ll;

    public BaseViewHolder viewHolder;

    public GankViewHolder(BaseViewHolder viewHolder) {
        this.viewHolder = viewHolder;
        findCommonViews();
    }

    private void findCommonViews() {
        desc_tv = viewHolder.getView(R.id.desc_tv);
        author_tv = viewHolder.getView(R.id.author_tv);
        border_view = viewHolder.getView(R.id.border_view);
        publish_time_tv = viewHolder.getView(R.id.publish_time_tv);
    }

    public void findWithImageViews() {
        viewpager = viewHolder.getView(R.id.viewpager);
        indicator_ll = viewHolder.getView(R.id.indicator_ll);
    }

}
