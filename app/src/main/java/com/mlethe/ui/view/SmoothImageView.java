package com.mlethe.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class SmoothImageView extends AppCompatImageView {

    private Bitmap mBitmap;
    private Matrix mMatrix;
    private Paint mPaint;
    private GestureDetector mGestureDetector;
    private float mImageWidth, mImageHeight;

    public SmoothImageView(Context context) {
        this(context, null);
    }

    public SmoothImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmoothImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mMatrix = new Matrix();
        MatrixTouchListener mListener = new MatrixTouchListener();
        setOnTouchListener(mListener);
        mGestureDetector = new GestureDetector(getContext(), new GestureListener(mListener));
        //背景设置为balck
        setBackgroundColor(Color.BLACK);
        //将缩放类型设置为CENTER_INSIDE，表示把图片居中显示,并且宽高最大值为控件宽高
        setScaleType(ScaleType.FIT_CENTER);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        //设置完图片后，获取该图片的坐标变换矩阵
        mMatrix.set(getImageMatrix());
        float[] values = new float[9];
        mMatrix.getValues(values);
        //图片宽度为屏幕宽度除缩放倍数
        mImageWidth = getWidth() / values[Matrix.MSCALE_X];
        mImageHeight = (getHeight() - values[Matrix.MTRANS_Y] * 2) / values[Matrix.MSCALE_Y];
    }

    private class MatrixTouchListener implements OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return false;
        }
    }

    private class GestureListener implements GestureDetector.OnGestureListener {
        public GestureListener(MatrixTouchListener mListener) {
        }

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }
    }
}
