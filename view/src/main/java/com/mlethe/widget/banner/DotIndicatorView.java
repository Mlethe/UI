package com.mlethe.widget.banner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * 圆的指示器
 * Created by Mlethe on 2018/5/29.
 */
public class DotIndicatorView extends View {

    public static final int CIRCLE = 0;
    public static final int SQUARE = 1;
    public static final int CIRCLE_LINE = 2;
    public static final int SQUARE_LINE = 3;

    private Drawable drawable;
    // 指示器形状  默认圆形
    private int indicatorType = CIRCLE;

    public DotIndicatorView(Context context) {
        this(context, null);
    }

    public DotIndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DotIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置Drawable和指示器形状
     *
     * @param drawable
     */
    public void setDrawable(Drawable drawable, int indicatorType) {
        this.drawable = drawable;
        this.indicatorType = indicatorType;
        // 重新绘制
        invalidate();
    }

    /**
     * 设置Drawable
     * @param drawable
     */
    public void setDrawable(Drawable drawable) {
        setDrawable(drawable, CIRCLE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (drawable != null) {
            if (indicatorType == SQUARE) { // 方形
                drawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                drawable.draw(canvas);
            } else if (indicatorType == CIRCLE_LINE) {  // 空心圆
                if (drawable instanceof ColorDrawable) {
                    int width = getMeasuredWidth();
                    int height = getMeasuredHeight();
                    if (width > height) {
                        width = height;
                    }
                    int centre = width/2; // 获取圆心的x坐标
                    int roundWidth = dip2px(1); // 线条宽度
                    int radius = centre - roundWidth / 2; // 圆环的半径
                    Paint paint = new Paint();
                    paint.setColor(((ColorDrawable) drawable).getColor());//线的颜色
                    paint.setStyle(Paint.Style.STROKE);//修改为画线模式  FILL 是填充模式，STROKE 是画线模式（即勾边模式），FILL_AND_STROKE 既画线又填充。它的默认值是 FILL，填充模式。
                    paint.setStrokeWidth(roundWidth); // 线条宽度
                    paint.setAntiAlias(true);//抗锯齿开   Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    canvas.drawCircle(centre, centre, radius, paint);
                } else {
                    drawCircle(canvas);
                }
            } else if (indicatorType == SQUARE_LINE) {  // 空心方形
                int width = getMeasuredWidth();
                int height = getMeasuredHeight();
                int roundWidth = dip2px(1); // 线条宽度
                Paint paint = new Paint();
                paint.setColor(((ColorDrawable) drawable).getColor());//线的颜色
                paint.setStyle(Paint.Style.STROKE);//修改为画线模式  FILL 是填充模式，STROKE 是画线模式（即勾边模式），FILL_AND_STROKE 既画线又填充。它的默认值是 FILL，填充模式。
                paint.setStrokeWidth(roundWidth); // 线条宽度
                paint.setAntiAlias(true);//抗锯齿开   Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                // 画矩形(Rect)
                Rect rect = new Rect(0, 0, width, height);
                canvas.drawRect(rect, paint);
            } else {
                drawCircle(canvas);
            }
        }
    }

    /**
     * 画圆
     * @param canvas
     */
    private void drawCircle(Canvas canvas) {
        // 画圆
        Bitmap bitmap = drawableToBitmap(drawable);
        // 把Bitmap变为圆形
        Bitmap circleBitmap = getCircleBitmap(bitmap);
        // 把圆形的Bitmap绘制到画布上
        canvas.drawBitmap(circleBitmap, 0, 0, null);
        // 内存优化  回收Bitmap
        circleBitmap.recycle();
    }

    /**
     * 获取圆形Bitmap
     *
     * @param bitmap
     * @return
     */
    private Bitmap getCircleBitmap(Bitmap bitmap) {
        // 创建一个bitmap
        Bitmap circleBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(circleBitmap);
        Paint paint = new Paint();
        // 设置抗锯齿
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        // 设置防抖动
        paint.setDither(true);
        // 在画布上画个圆
        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, getMeasuredWidth() / 2, paint);
        // 取圆和Bitmap的交集
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        // 再把原来的Bitmap绘制到新的圆上面
        canvas.drawBitmap(bitmap,0,0,paint);
        // 内存优化  回收Bitmap
        bitmap.recycle();
        return circleBitmap;
    }

    /**
     * 从Drawable中获得Bitmap
     *
     * @param drawable
     * @return
     */
    private Bitmap drawableToBitmap(Drawable drawable) {
        // 如果是BitmapDrawable类型
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        // 其他类型 ColorDrawable
        // 创建一个什么也没有的Bitmap
        Bitmap outBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        // 创建一个画布
        Canvas canvas = new Canvas(outBitmap);
        // 把Drawable画到bitmap上
        drawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
        drawable.draw(canvas);
        return outBitmap;
    }

    /**
     * 把dip转为px
     * @param dip
     * @return
     */
    private int dip2px(int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
    }
}
