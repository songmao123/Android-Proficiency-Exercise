package com.sqsong.gankiosample.adapter;

import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sqsong.gankiosample.R;
import com.sqsong.gankiosample.model.GankData;
import com.sqsong.gankiosample.util.Util;
import com.sqsong.gankiosample.view.CirclePagerIndicator;
import com.sqsong.gankiosample.view.FixedSpeedScroller;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by 青松 on 2016/11/24.
 */

public class GankDataAdapter extends BaseMultiItemQuickAdapter<GankData, BaseViewHolder> {

    private static final int AUTO_SCROLL_DURATION = 3000;
    private Handler mHandler = new Handler();

    public GankDataAdapter(List<GankData> data) {
        super(data);
        this.mData = data;
        addItemType(GankData.TYPE_WITH_IMAGE, R.layout.item_gank_with_image);
        addItemType(GankData.TYPE_WITHOUT_IMAGE, R.layout.item_gank_without_image);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, GankData gankData) {
        GankViewHolder viewHelper = new GankViewHolder(baseViewHolder);
        switch (baseViewHolder.getItemViewType()) {
            case GankData.TYPE_WITH_IMAGE:
                viewHelper.findWithImageViews();
                processWithImageData(viewHelper, gankData);
                break;
            case GankData.TYPE_WITHOUT_IMAGE:
                setGankData(viewHelper, gankData);
                break;
        }
    }

    /**
     * set the item data with image
     */
    private void processWithImageData(final GankViewHolder viewHelper, GankData gankData) {
        setGankData(viewHelper, gankData);

        final List<String> imageList = gankData.getImages();
        if (imageList == null) return;

        ImagePagerAdapter imageAdapter = new ImagePagerAdapter(mContext, imageList);
        viewHelper.viewpager.setAdapter(imageAdapter);

        viewHelper.indicator_ll.removeAllViews();
        if (imageList.size() > 1) {
            viewHelper.indicator_ll.setVisibility(View.VISIBLE);
            final CirclePagerIndicator indicator = new CirclePagerIndicator(mContext);
            indicator.setCircleCount(imageList.size());
            indicator.setNormalColor(mContext.getResources().getColor(R.color.colorNormalIndicator));
            indicator.setFocusColor(mContext.getResources().getColor(R.color.colorWhite));
            // add circle indicator
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            viewHelper.indicator_ll.addView(indicator, lp);
            viewHelper.viewpager.addOnPageChangeListener(new ViewPagerPageChangeListener(indicator));

            // add image loop
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int currentItem = viewHelper.viewpager.getCurrentItem();
                    viewHelper.viewpager.setCurrentItem((currentItem + 1) % (imageList.size()));
                    mHandler.postDelayed(this, AUTO_SCROLL_DURATION);
                }
            }, AUTO_SCROLL_DURATION);
            setViewPagerScrollDuration(viewHelper.viewpager);
        } else {
            viewHelper.indicator_ll.setVisibility(View.GONE);
        }
    }

    /** set the common data(without image) */
    private void setGankData(GankViewHolder viewHelper, GankData gankData) {
        viewHelper.desc_tv.setText(gankData.getDesc());
        viewHelper.author_tv.setText("via " + gankData.getWho());
        String publishTime = Util.getNormalFormatTime(gankData.getPublishedAt());
        viewHelper.publish_time_tv.setText(publishTime);

        // The next item without image, then set the bottom border visible;
        if (viewHelper.border_view == null) {
            return;
        }
        int position = viewHelper.viewHolder.getAdapterPosition() + 1;
        if (position < getData().size() && gankData.getItemType() == GankData.TYPE_WITHOUT_IMAGE
                && (getData().get(position).getItemType() == GankData.TYPE_WITHOUT_IMAGE)) {
            viewHelper.border_view.setVisibility(View.VISIBLE);
        } else {
            viewHelper.border_view.setVisibility(View.GONE);
        }
    }

    /** set the duration of viewpager scroll to current page to next page */
    public void setViewPagerScrollDuration(ViewPager viewPager) {
        try {
            Field mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);
            FixedSpeedScroller mScroller = new FixedSpeedScroller(
                    viewPager.getContext(), new AccelerateInterpolator());
            mScroller.setmDuration(400);
            mField.set(viewPager, mScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ViewPagerPageChangeListener implements ViewPager.OnPageChangeListener {

        private CirclePagerIndicator mIndicator;

        public ViewPagerPageChangeListener(CirclePagerIndicator indicator) {
            this.mIndicator = indicator;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            mIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

}
