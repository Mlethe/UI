package com.mlethe.widget.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mlethe.widget.R;
import com.mlethe.widget.observer.ViewHolder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义BannerView
 * Created by Mlethe on 2018/5/29.
 */
public class BannerView extends RelativeLayout implements ViewPager.OnPageChangeListener {
    // 轮播的ViewPager
    private BannerViewPager mBannerVp;
    // 底部容器
    private RelativeLayout mBannerBv;
    // 轮播的描述
    private AppCompatTextView mBannerDescTv;
    // 点的容器
    private LinearLayout mDotContainerView;
    // 默认图片
    private AppCompatImageView mBannerDefaultImage;
    // 自定义的BannerAdapter
    private BannerAdapter mAdapter;
    // 当前position
    private int currentItem;
    // 自定义的页面切换的Scroller
    private BannerScroller mScroller;
    // 内存优化界面复用 - 复用的界面
    private List<View> mConvertViews;

    // 当前点的位置
    private int mCurrentPosition = 0;

    // 点的显示位置 默认右边
    private int mDotGravity = 1;
    // 点的形状（圆形和方形） 默认圆形
    private int mDotIndicatorType = 0;
    // 点选中的Drawable
    private Drawable mIndicatorFocusDrawable;
    // 点默认的Drawable
    private Drawable mIndicatorNormalDrawable;
    // 点的宽度
    private int mDotWidth = 8;
    // 点的高度
    private int mDotHeight = 8;
    // 点的间距
    private int mDotDistance = 8;
    // 底部容器颜色  默认透明
    private int mBottomBackground = Color.TRANSPARENT;
    // 底部容器高度
    private int mBottomHeight = 28;
    // 是否自动轮播
    private boolean mIsAutoPlay = true;
    // 轮播间隔时间
    private int mDelayTime = 2000;
    // 动画持续的时间
    private int mScrollTime = 950;
    // 描述文字大小
    private float mDescTextSize = 12;
    // 描述文字颜色
    private int mDescTextColor = Color.WHITE;
    // 默认图片
    private int mDefaultImage;
    // 是否使用默认图片
    private boolean mIsDefaultImage = true;

    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 把布局加载到这个View里面
        inflate(context, R.layout.ui_banner_layout, this);
        initAttribute(context, attrs);
        initView();
    }

    /**
     * 初始化自定义属性
     */
    private void initAttribute(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BannerView);
        mBottomBackground = typedArray.getColor(R.styleable.BannerView_bottomBackground, mBottomBackground);
        mBottomHeight = (int) typedArray.getDimension(R.styleable.BannerView_bottomHeight, dip2px(mBottomHeight));
        // 获取点的显示位置
        mDotGravity = typedArray.getInt(R.styleable.BannerView_dotGravity, mDotGravity);
        // 获取点的形状
        mDotIndicatorType = typedArray.getInt(R.styleable.BannerView_dotIndicatorType, mDotIndicatorType);
        mIndicatorFocusDrawable = typedArray.getDrawable(R.styleable.BannerView_dotIndicatorFocus);
        if (mIndicatorFocusDrawable == null) {
            // 如果在布局文件中没有配置点的颜色  有一个默认值
            mIndicatorFocusDrawable = new ColorDrawable(Color.WHITE);
        }
        mIndicatorNormalDrawable = typedArray.getDrawable(R.styleable.BannerView_dotIndicatorNormal);
        if (mIndicatorNormalDrawable == null) {
            // 如果在布局文件中没有配置点的颜色  有一个默认值
            mIndicatorNormalDrawable = new ColorDrawable(Color.parseColor("#FFEEEEEE"));
        }
        mDotWidth = (int) typedArray.getDimension(R.styleable.BannerView_dotWidth, dip2px(mDotWidth));
        mDotHeight = (int) typedArray.getDimension(R.styleable.BannerView_dotHeight, dip2px(mDotHeight));
        mDotDistance = (int) typedArray.getDimension(R.styleable.BannerView_dotDistance, dip2px(mDotDistance));
        mDotIndicatorType = typedArray.getInt(R.styleable.BannerView_dotIndicatorType, mDotIndicatorType);
        mIsAutoPlay = typedArray.getBoolean(R.styleable.BannerView_isAutoPlay, mIsAutoPlay);
        mDelayTime = typedArray.getInt(R.styleable.BannerView_delayTime, mDelayTime);
        mScrollTime = typedArray.getInt(R.styleable.BannerView_scrollTime, mScrollTime);
        mDescTextSize = typedArray.getDimension(R.styleable.BannerView_descTextSize, sp2px(mDescTextSize));
        mDescTextColor = typedArray.getColor(R.styleable.BannerView_descTextColor, mDescTextColor);
        mDefaultImage = typedArray.getResourceId(R.styleable.BannerView_bannerDefaultImage, R.mipmap.no_banner);
        mIsDefaultImage = typedArray.getBoolean(R.styleable.BannerView_isDefaultImage, mIsDefaultImage);
        typedArray.recycle();
    }

    /**
     * 初始化view
     */
    private void initView() {
        mBannerVp = findViewById(R.id.banner_vp);
        mBannerBv = findViewById(R.id.banner_bv);
        mBannerDescTv = findViewById(R.id.banner_desc_tv);
        mDotContainerView = findViewById(R.id.dot_container);
        mBannerDefaultImage = findViewById(R.id.banner_default_image);
        mBannerBv.setBackgroundColor(mBottomBackground);
        ViewGroup.LayoutParams params = mBannerBv.getLayoutParams();
        params.height = mBottomHeight;
        mBannerBv.setLayoutParams(params);
        mBannerDescTv.setTextColor(mDescTextColor);
        mBannerDescTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mDescTextSize);
        if (mIsDefaultImage) {
            mBannerDefaultImage.setImageResource(mDefaultImage);
        }
    }

    /**
     * 初始化点的指示器
     */
    private void initDotIndicator() {
        mDotContainerView.removeAllViews();
        // 获取广告位的数量
        int count = mAdapter.getCount();
        // 让点的位置在右边
        mDotContainerView.setGravity(getDotGravity());
        for (int i = 0; i < count; i++) {
            // 不断的往点的指示器添加圆点
            DotIndicatorView indicatorView = new DotIndicatorView(getContext());
            // 设置大小
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(mDotWidth, mDotHeight);
            // 设置左右的间距
            if (i != 0) {
                layoutParams.leftMargin = mDotDistance;
            }
            indicatorView.setLayoutParams(layoutParams);
            // 设置颜色
            if (i == 0) {
                // 选中位置
                drawFocusIndicator(indicatorView);
            } else {
                // 未选中的
                indicatorView.setDrawable(mIndicatorNormalDrawable, mDotIndicatorType);
            }
            mDotContainerView.addView(indicatorView);
        }
    }

    /**
     * 获取点的位置
     * @return
     */
    private int getDotGravity() {
        if (mDotGravity == 0) {
            return Gravity.CENTER;
        } else if (mDotGravity == -1) {
            return Gravity.LEFT;
        } else if (mDotGravity == 1) {
            return Gravity.RIGHT;
        }
        return Gravity.RIGHT;
    }

    /**
     * 把dip转为px
     * @param dip
     * @return
     */
    private int dip2px(int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
    }

    /**
     * 把sp转为px
     * @param sp
     * @return
     */
    private float sp2px(float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    /**
     * 设置适配器
     * @param adapter
     */
    public void setAdapter(BannerAdapter adapter){
        if (adapter != null && adapter.getCount() > 0) {
            this.mAdapter = adapter;
            mBannerDefaultImage.setVisibility(GONE);
            mBannerVp.setVisibility(VISIBLE);
            mBannerBv.setVisibility(VISIBLE);
            // 初始化点的指示器
            initDotIndicator();
            // 初始化广告的描述
            mBannerDescTv.setText(mAdapter.getBannerDesc(0));
            // 配置BannerViewPager
            setBannerVpConfig();
        } else {
            if (mIsDefaultImage) {
                mBannerDefaultImage.setImageResource(mDefaultImage);
            }
            mBannerDefaultImage.setVisibility(VISIBLE);
            mBannerVp.setVisibility(GONE);
            mBannerBv.setVisibility(GONE);
        }
    }

    /**
     * BannerViewPager设置
     */
    private void setBannerVpConfig() {
        mConvertViews = new ArrayList<>();
        mBannerVp.setAdapter(new BannerPagerAdapter());
        mBannerVp.setAutoPlay(mIsAutoPlay);
        mBannerVp.setDelayTime(mDelayTime);
        // BannerViewPager切换回调
        mBannerVp.addOnPageChangeListener(this);
        // 改变ViewPager切换的速率
        // duration 持续的时间  局部变量
        // 改变 mScroller private 通过
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            // 设置参数  第一个object->当前属性在那个类  第二个参数代表要设置的值
            mScroller = new BannerScroller(getContext());
            // 设置为强制改变private
            field.setAccessible(true);
            field.set(mBannerVp, mScroller);
            mScroller.setScrollerDuration(mScrollTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mBannerVp.startRoll();
    }

    /**
     * 页面切换的回调
     * @param position
     */
    private void pageSelected(int position) {
        // 把之前亮着的点 设置为默认
        DotIndicatorView oldIndicatorView = (DotIndicatorView) mDotContainerView.getChildAt(mCurrentPosition);
        oldIndicatorView.setDrawable(mIndicatorNormalDrawable, mDotIndicatorType);
        // 把当前位置的点 点亮
        mCurrentPosition = position % mAdapter.getCount();
        DotIndicatorView currentIndicatorView = (DotIndicatorView) mDotContainerView.getChildAt(mCurrentPosition);
        drawFocusIndicator(currentIndicatorView);
        // 设置广告的描述
        String bannerDesc = mAdapter.getBannerDesc(mCurrentPosition);
        mBannerDescTv.setText(bannerDesc);
    }

    /**
     * 画选中的指示器
     */
    private void drawFocusIndicator(DotIndicatorView indicatorView) {
        if (mDotIndicatorType == DotIndicatorView.CIRCLE_LINE) {
            indicatorView.setDrawable(mIndicatorFocusDrawable, DotIndicatorView.CIRCLE);
        } else if (mDotIndicatorType == DotIndicatorView.SQUARE_LINE) {
            indicatorView.setDrawable(mIndicatorFocusDrawable, DotIndicatorView.SQUARE);
        } else {
            indicatorView.setDrawable(mIndicatorFocusDrawable, mDotIndicatorType);
        }
    }

    /**
     * 设置轮播点击事件
     * @param listener
     * @return
     */
    public BannerView setBannerListener(OnBannerListener listener) {
        this.mListener = listener;
        return this;
    }

    /**
     * 设置轮播动画时间
     * @param scrollTime
     * @return
     */
    public BannerView setScrollTime(int scrollTime) {
        this.mScrollTime = scrollTime;
        return this;
    }

    /**
     * 设置轮播时间
     * @param time
     * @return
     */
    public BannerView setDelayTime(int time) {
        this.mDelayTime = time;
        return this;
    }

    /**
     * 设置是否自动轮播
     * @param isAutoPlay
     * @return
     */
    public BannerView setAutoPlay(boolean isAutoPlay) {
        this.mIsAutoPlay = isAutoPlay;
        return this;
    }

    /**
     * 启动自动轮播（Fragment show的时候调用）
     * @return
     */
    public void start() {
        mBannerVp.startRoll();
    }

    /**
     * 停止自动轮播（Fragment hide的时候调用）
     * @return
     */
    public void stop() {
        mBannerVp.stopRoll();
    }

    /**
     * 给ViewPager设置适配器
     */
    private class BannerPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            // 为了实现无限循环
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            // 官方推荐这么写  源码
            return view == object;
        }

        /**
         * 创建ViewPager条目回调的方法
         * @param container
         * @param position
         * @return
         */
        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            final int realPosition = position % mAdapter.getCount();
            // Adapter设计模式是为了完全让用自定义
            int layoutId = mAdapter.getLayoutId(realPosition);
            View view = getConvertView();
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(layoutId, container, false);
            }
            if (mListener != null) {
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.OnBannerClick(realPosition);
                    }
                });
            }
            // position 0 -> 2的31次方
            ViewHolder holder = new ViewHolder(view);
            mAdapter.convert(holder, realPosition);
            // 添加ViewPager里面
            container.addView(view);
            return  view;
        }

        /**
         * 销毁条目回调的方法
         * @param container
         * @param position
         * @param object
         */
        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
            mConvertViews.add((View) object);
            object = null;
        }
    }

    /**
     * 获取复用界面
     * @return
     */
    private View getConvertView() {
        for (View convertView : mConvertViews) {
            if (convertView.getParent() == null) {
                return convertView;
            }
        }
        return null;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentItem = position;
        // 监听当前选中的位置
        pageSelected(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case 0://No operation
                if (currentItem == 0) {
                    mBannerVp.setCurrentItem(mAdapter.getCount(), false);
                }
                break;
            case 1://start Sliding
                 if (currentItem == 0) {
                    mBannerVp.setCurrentItem(mAdapter.getCount(), false);
                }
                break;
            case 2://end Sliding
                break;
        }
    }

    private OnBannerListener mListener;

    public interface OnBannerListener {
        void OnBannerClick(int position);
    }
}
