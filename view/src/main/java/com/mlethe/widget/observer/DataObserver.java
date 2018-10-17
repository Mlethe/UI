package com.mlethe.widget.observer;

/**
 * Created by Mlethe on 2018/3/13.
 */

public abstract class DataObserver {
    public abstract void onChanged();
    public abstract void onItemRangeChanged(int positionStart, int itemCount, Object payload);
    public abstract void onItemRangeInserted(int positionStart, int itemCount);
    public abstract void onItemRangeRemoved(int positionStart, int itemCount);
}
