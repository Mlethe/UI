package com.mlethe.widget.banner;

import com.mlethe.widget.observer.ViewHolder;

/**
 * Created by Mlethe on 2018/5/29.
 */
public abstract class BannerAdapter {
    /**
     * 获取根据位置获取ViewPager里面的子布局
     * @param position
     */
    public abstract int getLayoutId(int position);

    /**
     * 绑定数据
     * @param position
     * @return
     */
    public abstract void convert(ViewHolder holder, int position);

    /**
     * 获取轮播的数量
     * @return
     */
    public abstract int getCount();

    /**
     * 根据位置获取广告位的描述
     * @param position
     * @return
     */
    public String getBannerDesc(int position) {
        return null;
    }

}
