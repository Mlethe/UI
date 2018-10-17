package com.mlethe.widget.observer;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 布局的adapter
 * Created by Mlethe on 2018/3/12.
 */

public abstract class LayoutAdapter<T> {

    protected Context mContext;

    protected List<T> mData;

    private int mLayoutId;

    // 多布局支持
    private MultiTypeSupport mMultiTypeSupport;

    private DataObservable mObserver = new DataObservable();

    protected LayoutAdapter(Context context, List<T> data, @LayoutRes int layoutId){
        this.mContext = context;
        this.mData = data;
        this.mLayoutId = layoutId;
    }

    /**
     * 多布局支持
     */
    protected LayoutAdapter(Context context, List<T> data, MultiTypeSupport<T> multiTypeSupport){
        this.mContext = context;
        this.mData = data;
        this.mMultiTypeSupport = multiTypeSupport;
    }

    // 1、有多少条目
    public int getCount() {
        return mData.size();
    }

    // 2、getView通过position
    public View getView(final int position, ViewGroup parent) {
        // 多布局支持
        if (mMultiTypeSupport != null) {
            mLayoutId = mMultiTypeSupport.getLayoutId(mData.get(position), position);
        }
        View view = LayoutInflater.from(mContext).inflate(mLayoutId, parent, false);
        if (mItemClickListener != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onItemClick(position);
                }
            });
        }
        if (mLongClickListener != null) {
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return mLongClickListener.onLongClick(position);
                }
            });
        }
        // 返回ViewHolder
        ViewHolder holder = new ViewHolder(view);
        convert(holder, mData.get(position), position);
        return holder.mItemView;
    }

    // 3、观察者模式及时通知更新
    public void unregisterDataSetObserver(DataObserver observer){
        mObserver.unregisterObserver(observer);
    }

    public void registerDataSetObserver(DataObserver observer){
        mObserver.registerObserver(observer);
    }

    public final void notifyDataSetChanged(){
        if (mObserver != null){
            mObserver.notifyChanged();
        }
    }

    public final void notifyItemChanged(int position){
        if (mObserver != null){
            mObserver.notifyItemRangeChanged(position, 1);
        }
    }

    public abstract void convert(ViewHolder holder, T item, int position);

    private OnItemClickListener mItemClickListener;
    private OnLongClickListener mLongClickListener;

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    public void setOnLongClickListener(OnLongClickListener longClickListener) {
        this.mLongClickListener = longClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnLongClickListener {
        boolean onLongClick(int position);
    }

    /**
     * 多布局支持接口
     */
    public interface MultiTypeSupport<T> {
        // 根据当前位置或者条目数据返回布局
        public int getLayoutId(T item, int position);
    }

    private class DataObservable extends Observable<DataObserver> {
        public void notifyChanged() {
            // since onChanged() is implemented by the app, it could do anything, including
            // removing itself from {@link mObservers} - and that could cause problems if
            // an iterator is used on the ArrayList {@link mObservers}.
            // to avoid such problems, just march thru the list in the reverse order.
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onChanged();
            }
        }
        public void notifyItemRangeChanged(int positionStart, int itemCount) {
            notifyItemRangeChanged(positionStart, itemCount, null);
        }

        public void notifyItemRangeChanged(int positionStart, int itemCount, Object payload) {
            // since onItemRangeChanged() is implemented by the app, it could do anything, including
            // removing itself from {@link mObservers} - and that could cause problems if
            // an iterator is used on the ArrayList {@link mObservers}.
            // to avoid such problems, just march thru the list in the reverse order.
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onItemRangeChanged(positionStart, itemCount, payload);
            }
        }

        public void notifyItemRangeInserted(int positionStart, int itemCount) {
            // since onItemRangeInserted() is implemented by the app, it could do anything,
            // including removing itself from {@link mObservers} - and that could cause problems if
            // an iterator is used on the ArrayList {@link mObservers}.
            // to avoid such problems, just march thru the list in the reverse order.
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onItemRangeInserted(positionStart, itemCount);
            }
        }

        public void notifyItemRangeRemoved(int positionStart, int itemCount) {
            // since onItemRangeRemoved() is implemented by the app, it could do anything, including
            // removing itself from {@link mObservers} - and that could cause problems if
            // an iterator is used on the ArrayList {@link mObservers}.
            // to avoid such problems, just march thru the list in the reverse order.
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onItemRangeRemoved(positionStart, itemCount);
            }
        }
    }
}
