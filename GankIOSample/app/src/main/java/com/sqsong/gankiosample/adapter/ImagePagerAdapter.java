package com.sqsong.gankiosample.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.sqsong.gankiosample.R;
import com.sqsong.gankiosample.util.Util;

import java.util.List;

/**
 * Created by 青松 on 2016/11/24.
 */

public class ImagePagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<String> mImageList;
    private LayoutInflater mInflater;

    public ImagePagerAdapter(Context context, List<String> imageList) {
        this.mContext = context;
        this.mImageList = imageList;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mInflater.inflate(R.layout.layout_display_image, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Util.dip2px(188));
        view.setLayoutParams(params);

        // 图片宽度为屏幕宽度
        int imageWidth = Util.getScreenWidth();
        // 根据比例计算出需要获取的图片的高度
        int imageHeight = imageWidth * 188 / 360;
        // 根据七牛api获取相应尺寸的图片
        String imageUrl = new StringBuffer().append(mImageList.get(position))
                .append("?imageView2/2/w/")
                .append(imageWidth)
                .append("/h/")
                .append(imageHeight).toString();
        Glide.with(mContext).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(new GlideDrawableImageViewTarget(imageView));
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mImageList == null ? 0 : mImageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
