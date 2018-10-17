package com.mlethe.widget.banner;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Mlethe on 2018/5/29.
 */
public class BannerViewPager extends ViewPager {

    // 发送消息的msgWhat
    private final int SCROLL_MSG = 0x0011;

    // 页面切换间隔时间（毫秒）
    private int mCutDownTime = 2000;

    // 发送消息的Handler
    private Handler mHandler;
    // Handler是否发送消息 true 发送  false 否
    private boolean isSend = false;

    // 是否自动轮播
    private boolean isAutoPlay = true;

    public BannerViewPager(@NonNull Context context) {
        this(context, null);
    }

    public BannerViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        Log.i(tag, ev.getAction() + "--" + isAutoPlay);
        if (isAutoPlay) {
            int action = ev.getAction();
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL
                    || action == MotionEvent.ACTION_OUTSIDE) {
                startRoll();
            } else if (action == MotionEvent.ACTION_DOWN) {
                stopRoll();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 页面切换间隔时间（毫秒）
     * @param time
     */
    public void setDelayTime(int time) {
        this.mCutDownTime = time;
    }

    /**
     * 设置是否自动轮播
     * @param isAutoPlay
     */
    public void setAutoPlay(boolean isAutoPlay) {
        this.isAutoPlay = isAutoPlay;
    }

    /**
     * 实现自动轮播
     */
    public void startRoll(){
        if (mHandler != null && isAutoPlay) {
            if (!isSend) {
                // 消息  延迟时间  让用户自定义  默认值 350
                mHandler.sendEmptyMessageDelayed(SCROLL_MSG, mCutDownTime);
                this.isSend = true;
            }
        }
    }

    /**
     * 停止轮播
     */
    public void stopRoll() {
        if (mHandler != null && isSend) {
            // 清除消息
            mHandler.removeMessages(SCROLL_MSG);
            isSend = false;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        if (mHandler == null && !isSend) {
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    // 每隔*s后切换到下一页
                    setCurrentItem(getCurrentItem() + 1);
                    // 清除消息
                    stopRoll();
                    // 不断循环执行
                    startRoll();
                }
            };
        }
        // 绑定activity的生命周期管理
        ((Activity)getContext()).getApplication().registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
        super.onAttachedToWindow();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == View.VISIBLE){    // 可见
            // 开启轮播
            startRoll();
        } else if (visibility == INVISIBLE || visibility == GONE){  // 不可见
            // 停止轮播
            stopRoll();
        }
    }

    /**
     * 销毁Handler  停止发送  解决内存泄漏
     */
    @Override
    protected void onDetachedFromWindow() {
        // 销毁Handler的生命周期
        stopRoll();
        mHandler = null;
        // 解除绑定
        ((Activity)getContext()).getApplication().unregisterActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
        super.onDetachedFromWindow();
    }

    private Application.ActivityLifecycleCallbacks mActivityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            if (activity == getContext()) {
                startRoll();
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {
            if (activity == getContext()) {
                stopRoll();
            }
        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    };

}
