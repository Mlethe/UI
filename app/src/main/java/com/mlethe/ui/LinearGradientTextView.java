package com.mlethe.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class LinearGradientTextView extends AppCompatTextView {

    private int mTranslate;
    private float DELTAX = 20;
    private LinearGradient linearGradient;

    public LinearGradientTextView(Context context) {
        this(context, null);
    }

    public LinearGradientTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LinearGradientTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Paint paint = new Paint();
        String text = getText().toString();
        float textWith = paint.measureText(text);
        // 3个文字的宽度
        int gradientSize = (int) (textWith / text.length() * 3);
        // 从左边-gradientSize开始，即左边距离文字gradientSize开始渐变
        linearGradient = new LinearGradient(-gradientSize, 0, 0, 0, new int[]{0x22ffffff, 0xffffffff, 0x22ffffff}, null, Shader.TileMode.CLAMP);
        paint.setShader(linearGradient);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mTranslate += DELTAX;
        float textWidth = getPaint().measureText(getText().toString());
        if(mTranslate > textWidth + 1 || mTranslate < 1){
            DELTAX = - DELTAX;
        }
        Matrix mMatrix = new Matrix();
        mMatrix.setTranslate(mTranslate, 0);
        linearGradient.setLocalMatrix(mMatrix);
        postInvalidateDelayed(50);
    }
}
