package com.mlethe.widget.observer;

import android.content.Context;
import android.support.annotation.IdRes;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Mlethe on 2018/4/29.
 */
public class ViewHolder {

    // 用来存放子View减少findViewById的次数
    private SparseArray<View> mViews;

    protected View mItemView;

    public ViewHolder(View itemView) {
        this.mItemView = itemView;
        mViews = new SparseArray<>();
    }

    /**
     * 设置TextView文本
     */
    public ViewHolder setText(int viewId, CharSequence text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    /**
     * 设置TextView文本
     * @param viewId
     * @param value
     * @return
     */
    public ViewHolder setText(int viewId, int value) {
        setText(viewId, String.valueOf(value));
        return this;
    }

    /**
     * 设置TextView文本
     * @param viewId
     * @param value
     * @return
     */
    public ViewHolder setText(int viewId, float value) {
        setText(viewId, String.valueOf(value));
        return this;
    }

    /**
     * 设置TextView文本
     * @param viewId
     * @param value
     * @return
     */
    public ViewHolder setText(int viewId, double value) {
        setText(viewId, String.valueOf(value));
        return this;
    }

    /**
     * 设置TextView文本
     * @param viewId
     * @param value
     * @return
     */
    public ViewHolder setText(int viewId, long value) {
        setText(viewId, String.valueOf(value));
        return this;
    }

    /**
     * 通过id获取view
     */
    public <T extends View> T getView(@IdRes int viewId) {
        // 先从缓存中找
        View view = mViews.get(viewId);
        if (view == null) {
            // 直接从ItemView中找
            view = mItemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 设置View的Visibility
     */
    public ViewHolder setViewVisibility(int viewId, int visibility) {
        getView(viewId).setVisibility(visibility);
        return this;
    }

    /**
     * 获取View的Visibility
     * @param viewId
     * @return
     */
    public int getViewVisibility(int viewId) {
        return getView(viewId).getVisibility();
    }

    /**
     * 设置ImageView的资源
     */
    public ViewHolder setImageResource(int viewId, int resourceId) {
        ImageView imageView = getView(viewId);
        imageView.setImageResource(resourceId);
        return this;
    }

    /**
     * 设置View的点击事件
     * @param viewId
     * @param listener
     * @return
     */
    public ViewHolder setOnClickListener(int viewId, View.OnClickListener listener) {
        getView(viewId).setOnClickListener(listener);
        return this;
    }

    /**
     * 设置View的长按事件
     * @param viewId
     * @param listener
     * @return
     */
    public ViewHolder setOnLongClickListener(int viewId, View.OnLongClickListener listener) {
        getView(viewId).setOnLongClickListener(listener);
        return this;
    }

    /**
     * 设置图片通过路径,这里稍微处理得复杂一些，因为考虑加载图片的第三方可能不太一样
     * 也可以直接写死
     */
    public ViewHolder setImageByUrl(int viewId, HolderImageLoader imageLoader) {
        ImageView imageView = getView(viewId);
        if (imageLoader == null) {
            throw new NullPointerException("imageLoader is null!");
        }
        imageLoader.displayImage(imageView.getContext(), imageView, imageLoader.getImagePath());
        return this;
    }

    /**
     * 图片加载，这里稍微处理得复杂一些，因为考虑加载图片的第三方可能不太一样
     * 也可以不写这个类
     */
    public static abstract class HolderImageLoader {
        private String mImagePath;

        public HolderImageLoader(String imagePath) {
            this.mImagePath = imagePath;
        }

        public String getImagePath() {
            return mImagePath;
        }

        public abstract void displayImage(Context context, ImageView imageView, String imagePath);
    }

}
