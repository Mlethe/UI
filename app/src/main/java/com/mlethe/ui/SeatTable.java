package com.mlethe.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class SeatTable extends View {

    /**
     * 座位格式
     */
    private int[][] mSeats;
    /**
     * 行数、列数
     */
    private int row, column;

    private Paint mPaint = new Paint();

    /**
     * 空座位、选中、已售出
     */
    private Bitmap seatEmpty, seatSelect, seatSelected;

    /**
     * 座位已售、座位已经选中、座位可选、座位不可用
     */
    private static final int SEAT_TYPE_SOLD = 1, SEAT_TYPE_SELECTED = 2, SEAT_TYPE_AVAILABLE = 3, SEAT_TYPE_NOT_AVAILABLE = 4;

    /**
     * 座位间空隙、放大倍数
     */
    private float spacing, scale = 2;

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
        int [][] seats = {{1,1,1,1,2,1,1,1,2,1,2},{1,1,0,1,2,1,1,1,2,1,2},{1,1,0,1,2,1,1,1,2,0,2},{1,1,0,1,2,1,1,1,2,0,2},{1,1,0,1,2,1,1,1,2,0,2},{1,1,0,1,2,1,1,1,2,0,2},{1,1,0,1,2,1,1,1,2,0,2},{1,1,0,1,2,1,1,1,2,0,2},{1,1,0,1,2,1,1,1,2,1,2}};
        this.mSeats = seats;
        this.row = getRow();
        this.column = getColumn();
        seatEmpty = BitmapFactory.decodeResource(getResources(), R.mipmap.seat_empty);
        spacing = getResources().getDimension(R.dimen.dp_2);
    }

    /**
     * 获取行数
     * @return
     */
    private int getRow() {
        return mSeats.length;
    }

    /**
     * 获取列数
     * @return
     */
    private int getColumn(){
        int column = 0;
        for (int i = 0; i < mSeats.length; i++) {
            int[] row = mSeats[i];
            column = Math.max(column, row.length);
        }
        return column;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawSeat(canvas);
    }

    /**
     * 设置位置布局
     * @param seats
     * @return
     */
    public SeatTable setSeat(int[][] seats) {
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
    private void drawSeat(Canvas canvas) {
        // 行
        for (int i = 0; i < mSeats.length; i++) {
            // 列
            int[] row = mSeats[i];
            for (int j = 0; j < row.length; j++) {
                float left = j * seatEmpty.getWidth() + j * spacing;
                float top = i * seatEmpty.getHeight() + i * spacing;
                canvas.drawBitmap(seatEmpty, left, top, mPaint);
            }
        }
    }

    private int getSeatType(int row, int column) {
        return 0;
    }

}
