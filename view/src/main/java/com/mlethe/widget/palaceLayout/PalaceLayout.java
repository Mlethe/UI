package com.mlethe.widget.palaceLayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.mlethe.widget.R;
import com.mlethe.widget.observer.DataObserver;
import com.mlethe.widget.observer.LayoutAdapter;

import java.util.ArrayList;
import java.util.List;

public class PalaceLayout extends ViewGroup {

    private List<List<View>> mChildViews;

    // 列间距
    private int mColumnInterval = 0;
    // 行间距
    private int mRowInterval = 0;

    // 每行item的个数
    private int mSpanCount = 1;

    private LayoutAdapter mAdapter;

    private DataSetObserver mDataObserver = new DataSetObserver();

    public PalaceLayout(Context context) {
        this(context, null);
    }

    public PalaceLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PalaceLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mChildViews = new ArrayList<>();
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PalaceLayout);
        mColumnInterval = (int) array.getDimension(R.styleable.PalaceLayout_palaceColumnInterval, mColumnInterval);
        mRowInterval = (int) array.getDimension(R.styleable.PalaceLayout_palaceRowInterval, mRowInterval);
        mSpanCount = array.getInteger(R.styleable.PalaceLayout_palaceSpanCount, mSpanCount);
        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mChildViews.clear();

        int childCount = getChildCount();

        // 获取宽度
        int width = MeasureSpec.getSize(widthMeasureSpec);
        // 高度需要计算
        int height = getPaddingTop() + getPaddingBottom();

        ArrayList<View> childViews = new ArrayList<>();
        mChildViews.add(childViews);
        // 子view高度不一致的情况下
        int maxHeight = 0;
        int itemWidth = (width  - getPaddingLeft() - getPaddingRight() - (mColumnInterval * (mSpanCount - 1)))/mSpanCount;
        // 1、for循环测量子view
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            LayoutParams lp = childView.getLayoutParams();
            lp.width = itemWidth;
            childView.setLayoutParams(lp);
            // 执行之后就可以获取子view的宽高， 因为会调用子view的onMeasure()
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            // margin值  LayoutParams 没有margin值  就用系统的MarginLayoutParams
            MarginLayoutParams params = (MarginLayoutParams) childView.getLayoutParams();
            // 2、根据子view计算和指定自己的宽高
            // 什么时候需要换行，一行子view个数等于mSpanCount的时候换行  考虑  子view的margin
            // 子view的高度
            int childHeight = childView.getMeasuredHeight() + params.topMargin + params.bottomMargin;
            if (i != 0 && i%mSpanCount == 0) {    // 换行
                // 需要换行,累加高度
                height += maxHeight + mRowInterval;
                maxHeight = childHeight;
                childViews = new ArrayList<>();
                mChildViews.add(childViews);
            } else {
                maxHeight = Math.max(childHeight, maxHeight);
            }
            childViews.add(childView);
        }
        height += maxHeight;
        // 设置自己的宽高
        setMeasuredDimension(width, height);
    }

    /**
     * 使用系统的margin
     * @param attrs
     * @return
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left, top = getPaddingTop(), right, bottom;
        for (List<View> childViews : mChildViews) {
            int maxHeight = 0;
            left = getPaddingLeft();
            for (int i = 0; i < childViews.size(); i++) {
                View childView = childViews.get(i);
                if (childView.getVisibility() == GONE) {
                    continue;
                }
                MarginLayoutParams params = (MarginLayoutParams) childView.getLayoutParams();
                maxHeight = Math.max(params.topMargin + childView.getMeasuredHeight() + params.bottomMargin, maxHeight);
                left += params.leftMargin + (i == 0 ? 0 : mColumnInterval);
                int childTop = top + params.topMargin;
                right = left + childView.getMeasuredWidth() + params.rightMargin;
                bottom = top + childView.getMeasuredHeight() + params.bottomMargin;
//                Log.e("TAG", "height->" + childView.getMeasuredHeight() + "    left->" + left + "    right->" + right + "    top->" + top + "    bottom->" + bottom);
                // 摆放
                childView.layout(left, childTop, right, bottom);
                // left叠加
                left += childView.getMeasuredWidth() + params.rightMargin;
            }
            // 不断的叠加top值
            top += maxHeight + mRowInterval;
        }
    }

    public void setAdapter(LayoutAdapter adapter) {
        if (adapter == null) {
            throw new NullPointerException("LayoutAdapter is null");
        }
        this.mAdapter = adapter;
        adapter.registerDataSetObserver(mDataObserver);
        // 添加子view
        addChildView();
    }

    private class DataSetObserver extends DataObserver {
        @Override
        public void onChanged() {
            if (mAdapter != null) {
                // 添加子view
                addChildView();
            }
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {

        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {

        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {

        }
    }

    /**
     * 添加子view
     */
    private void addChildView() {
        // 清空所有子view
        removeAllViews();
        // 获取数量
        int childCount = mAdapter.getCount();
        for (int i = 0; i < childCount; i++) {
            // 通过位置获取View
            View childView = mAdapter.getView(i, this);
            addView(childView);
        }
    }
}
