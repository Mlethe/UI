package com.mlethe.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

public class SeatTable extends View {

    /**
     * 座位格式
     */
    private List<List<Seat>> mSeats;
    /**
     * 行数、列数
     */
    private int row, column;

    /**
     * 空、选中、已售出 座位
     */
    private Bitmap seatEmpty, seatSelect, seatSelected;
    /**
     * 空、选中、已售出 指示器
     */
    private Drawable seatEmptyPoint, seatSelectPoint, seatSelectedPoint;

    /**
     * 座位已售、座位已经选中、座位可选、座位不可用
     */
    private static final int SEAT_TYPE_SOLD = 1, SEAT_TYPE_SELECTED = 2, SEAT_TYPE_AVAILABLE = 3, SEAT_TYPE_NOT_AVAILABLE = 4;

    /**
     * 座位间间距、放大倍数、指示器高度、指示器和座位的间距、指示器大小、行号宽度、荧幕高度、荧幕与屏幕边缘间距
     */
    private float seatSpacing, scale = 1, indicatorHeight, indicatorSpacing, pointRadius, numberWidth, screenHeight, screenSpacing;

    private Matrix tempMatrix = new Matrix();

    /**
     * 座位画笔、指示器画笔、行号画笔、荧幕画笔
     */
    private Paint mPaint, indicatorPaint, rowNumberPaint, screenPaint;

    public SeatTable(Context context) {
        this(context, null);
    }

    public SeatTable(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SeatTable(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        mPaint = new Paint();
        seatEmpty = BitmapFactory.decodeResource(getResources(), R.mipmap.seat_empty);
        seatSpacing = getResources().getDimension(R.dimen.dp_2);

        indicatorHeight = getResources().getDimension(R.dimen.dp_30);
        pointRadius = getResources().getDimension(R.dimen.dp_7_5);
        indicatorSpacing = getResources().getDimension(R.dimen.dp_10);
        indicatorPaint = new Paint();
        indicatorPaint.setAntiAlias(true);
        indicatorPaint.setStyle(Paint.Style.FILL);
        indicatorPaint.setTextSize(getResources().getDimension(R.dimen.sp_12));

        rowNumberPaint = new Paint();
        rowNumberPaint.setAntiAlias(true);
        numberWidth = getResources().getDimension(R.dimen.dp_20);

        screenPaint = new Paint();
        screenPaint.setAntiAlias(true);
        screenPaint.setStyle(Paint.Style.FILL);
        screenHeight = getResources().getDimension(R.dimen.dp_20);
        screenSpacing = getResources().getDimension(R.dimen.dp_100);
    }

    /**
     * 获取行数
     * @return
     */
    private int getRow() {
        if (mSeats == null) {
            return 0;
        }
        return mSeats.size();
    }

    /**
     * 获取列数
     * @return
     */
    private int getColumn(){
        int column = 0;
        if (mSeats != null) {
            for (int i = 0; i < mSeats.size(); i++) {
                List<Seat> row = mSeats.get(i);
                column = Math.max(column, row.size());
            }
        }
        return column;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        float seatWidth = seatEmpty.getWidth() * column + seatSpacing * column;
        width = width - getPaddingLeft() - getPaddingRight();
        if (seatWidth <= width) {
            scale = width / seatWidth;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (row <= 0 || column <= 0) {
            return;
        }
        // 绘制指示器
        drawIndicator(canvas);
        // 绘制荧幕
        drawScreen(canvas);
        // 绘制座位
        drawSeat(canvas);
        // 绘制行号
        drawNumber(canvas);
    }

    /**
     * 绘制荧幕
     * @param canvas
     */
    private void drawScreen(Canvas canvas) {
        float screenWidth = getWidth() - screenSpacing * 2;
        float left = screenSpacing;
        float top = getScreenTop();
        Path path = new Path();
        path.moveTo(left, top);
        path.lineTo(left + 20, top + screenHeight);
        path.lineTo(left + screenWidth - 20, top + screenHeight);
        path.lineTo(left + screenWidth, top);
        path.close();
        screenPaint.setColor(Color.BLUE);
        canvas.drawPath(path, screenPaint);
    }

    /**
     * 绘制行号
     * @param canvas
     */
    private void drawNumber(Canvas canvas) {
        RectF rectF = new RectF();
        rectF.top = getSeatTop();
        rectF.bottom = rectF.top + (seatEmpty.getHeight() + seatSpacing) * row * scale - seatSpacing  * scale;
        rectF.left = 0;
        rectF.right = numberWidth;
        rowNumberPaint.setColor(Color.BLACK);
        rowNumberPaint.setAlpha(100);
        canvas.drawRoundRect(rectF, numberWidth / 2, numberWidth / 2, rowNumberPaint);

        rowNumberPaint.setColor(Color.WHITE);
        rowNumberPaint.setTextSize(getResources().getDimension(R.dimen.sp_12));
        int index = 0;
        for (int i = 0; i < row; i++) {
            List<Seat> column = mSeats.get(i);
            boolean flag = false;
            for (Seat seat : column) {
                if (seat.getRow() > 0 || seat.getColumn() > 0) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                index++;
                String str = String.valueOf(index);
                float textWidth = rowNumberPaint.measureText(str);
                float top = i * seatEmpty.getHeight() * scale + i * seatSpacing * scale + getSeatTop() - seatSpacing * scale;
                float bottom = top + (seatEmpty.getHeight() + seatSpacing) * scale;
                float baseLine = getBaseLine(rowNumberPaint, top, bottom);
                canvas.drawText(str, (numberWidth - textWidth) / 2, baseLine, rowNumberPaint);
            }
        }
    }

    /**
     * 获取指示器开始的顶部位置
     * @return
     */
    private float getIndicatorTop() {
        return 0;
    }

    /**
     * 获取屏幕开始的顶部位置
     * @return
     */
    private float getScreenTop() {
        return indicatorHeight;
    }

    /**
     * 获取座位开始的顶部位置
     * @return
     */
    private float getSeatTop(){
        return indicatorHeight + indicatorSpacing + screenHeight;
    }

    /**
     * 绘制指示器
     * @param canvas
     */
    private void drawIndicator(Canvas canvas) {
        float textY = getBaseLine(indicatorPaint, getIndicatorTop(), indicatorHeight);
        float textWidth = indicatorPaint.measureText("已售");
        float spacing = getResources().getDimension(R.dimen.dp_10);
        float spacing1 = getResources().getDimension(R.dimen.dp_5);
        float y = indicatorHeight / 2;
        float width = pointRadius * 2 * 3 + textWidth * 3 + spacing * 2 + spacing1 * 3;
        indicatorPaint.setColor(Color.WHITE);
        // 绘制背景
        canvas.drawRect(0, getIndicatorTop(), getWidth(), indicatorHeight + getIndicatorTop(), indicatorPaint);

        float firstPointX = (getWidth() - width) / 2 + pointRadius;
        float firstX = firstPointX + pointRadius + spacing1;
        indicatorPaint.setColor(Color.parseColor("#4E5362"));
        canvas.drawCircle(firstPointX, y, pointRadius, indicatorPaint);
        indicatorPaint.setColor(Color.GRAY);
        canvas.drawText("可选", firstX, textY, indicatorPaint);

        float secondPointX = firstX + textWidth + spacing + pointRadius;
        float secondX = secondPointX + pointRadius + spacing1;
        indicatorPaint.setColor(Color.parseColor("#5A83FF"));
        canvas.drawCircle(secondPointX, y, pointRadius, indicatorPaint);
        indicatorPaint.setColor(Color.GRAY);
        canvas.drawText("选中", secondX, textY, indicatorPaint);

        float thirdPointX = secondX + textWidth + spacing + pointRadius;
        float thirdX = thirdPointX + pointRadius + spacing1;
        indicatorPaint.setColor(Color.parseColor("#8D94A9"));
        canvas.drawCircle(thirdPointX, y, pointRadius, indicatorPaint);
        indicatorPaint.setColor(Color.GRAY);
        canvas.drawText("已售", thirdX, textY, indicatorPaint);

        //绘制分割线
        indicatorPaint.setStrokeWidth(1);
        indicatorPaint.setColor(Color.GRAY);
        canvas.drawLine(0, indicatorHeight + getIndicatorTop(), getWidth(), indicatorHeight + getIndicatorTop(), indicatorPaint);
    }

    private float getBaseLine(Paint p, float top, float bottom) {
        Paint.FontMetrics fontMetrics = p.getFontMetrics();
        return (bottom + top - fontMetrics.bottom - fontMetrics.top) / 2;
    }

    private GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            final float x = event.getX();
            final float y = event.getY();
            getFor(new ForCallBack() {
                @Override
                public void callBack(int i, int j, Seat column) {
                    float tempX = j * seatEmpty.getWidth() * scale + j * seatSpacing * scale + getPaddingLeft();
                    float maxTempX = tempX + seatEmpty.getWidth() * scale;
                    float tempY = i * seatEmpty.getHeight() * scale + i * seatSpacing  * scale + getSeatTop();
                    float maxTempY = tempY + seatEmpty.getHeight() * scale;
                    Log.e("TAG", "onSingleTapConfirmed: x->" + x + "    tempX->" + tempX + "    maxTempX->" + maxTempX + "   y->" + y + "   tempY->" + tempY + "    maxTempY->" + maxTempY + "     other->" + (seatSpacing * scale));
                    if (x >= tempX && x <= maxTempX && y >= tempY && y <= maxTempY) {
                        Log.d("TAG", "onSingleTapConfirmed1: " + column.getRow() + "排" + column.getColumn() + "号");
                    }
                }
            });
            return super.onSingleTapConfirmed(event);
        }
    });

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        gestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN :
                break;
            case MotionEvent.ACTION_MOVE :
                break;
            case MotionEvent.ACTION_UP :
                break;
        }
        return true;
    }

    /**
     * 设置位置布局
     * @param seats
     * @return
     */
    public SeatTable setSeat(List<List<Seat>> seats) {
        this.mSeats = seats;
        this.row = getRow();
        this.column = getColumn();
        return this;
    }

    /**
     * 重绘
     */
    public void redraw(){
        invalidate();
    }

    /**
     * 绘制座位
     */
    private void drawSeat(final Canvas canvas) {
        getFor(new ForCallBack() {
            @Override
            public void callBack(int i, int j, Seat column) {
                if (column.getRow() > 0 && column.getColumn() > 0) {
                    float left = j * seatEmpty.getWidth() * scale + j * seatSpacing * scale + getPaddingLeft();
                    float top = i * seatEmpty.getHeight() * scale + i * seatSpacing  * scale + getSeatTop();
                    tempMatrix.setTranslate(left, top);
                    tempMatrix.postScale(scale, scale, left, top);
                    canvas.drawBitmap(seatEmpty, tempMatrix, mPaint);
                }
            }
        });
    }

    /**
     * for循环统一处理
     * @param callBack
     */
    private void getFor(ForCallBack callBack) {
        // 行
        for (int i = 0; i < row; i++) {
            // 列
            List<Seat> column = mSeats.get(i);
            for (int j = 0; j < column.size(); j++) {
                callBack.callBack(i, j, column.get(j));
            }
        }
    }

    private interface ForCallBack{
        void callBack(int i, int j, Seat column);
    }

    private int getSeatType(int row, int column) {
        return 0;
    }

}
