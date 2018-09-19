package com.mlethe.ui;

import android.content.Context;
import android.content.res.TypedArray;
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
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SeatTableView extends View {

    private static final String NOTHING = "nothing";

    /**
     * 座位格式
     */
    private List<List<Seat>> mSeats;

    /**
     * 行号数组
     */
    private List<String> mSeatRows = new ArrayList<>();

    /**
     * 选中的座位
     */
    private List<Seat> selectSeats = new ArrayList<>();
    /**
     * 列数、最多选择个数
     */
    private int column, maxSelected = 5;

    /**
     * 空、选中、已售出 座位
     */
    private Bitmap seatAvailable, seatChecked, seatSold;

    /**
     * 概略图
     */
    private Bitmap overviewBitmap;

    /**
     * 座位宽度、高度
     */
    private int seatWidth, seatHeight;

    /**
     * 空、选中、已售出 指示器
     */
    private Drawable seatAvailablePoint, seatCheckedPoint, seatSoldPoint;

    /**
     * 座位间间距、放大倍数、座位上方的间距
     */
    private float seatSpacing, scale = 1, topSpacing;

    /**
     * 行号宽度、左边margin
     */
    private float numberWidth, leftMargin;

    /**
     * 侧边行号文字大小
     */
    private float sideTextSize;

    /**
     * 荧幕高度、宽度
     */
    private float screenHeight, screenWidth;

    /**
     * 荧幕颜色
     */
    private int screenColor;

    /**
     * 指示器高度、指示器半径
     */
    private float indicatorHeight, pointRadius;

    /**
     * 指示器文字颜色、背景颜色、指示器点选中、已售、可选的颜色
     */
    private int txtColor, indicatorColor, indicatorChecked, indicatorSold, indicatorAvailable;

    /**
     * 指示器是否使用颜色值，默认true
     */
    private boolean indicatorIsColor;

    /**
     * 指示器文字大小
     */
    private float txtSize;

    /**
     * 荧幕和指示器交换位置，默认false
     */
    private boolean indicatorScreenSwap;

    /**
     * 是否显示概览图
     */
    private boolean isShowOverview = false;

    /**
     * 概览图上方块选中、已售的颜色
     */
    private int overviewChecked, overviewSold;

    /**
     * 概览图宽度、高度、概览图白色方块的高度、宽度、概览图上方块的水平间距、垂直间距、概览图的比例、概览图padding、概览图上屏幕高度
     */
    float overviewHeight, overviewWidth, rectHeight, rectWidth, overviewSpacing, overviewScale = 4.8f, overviewPadding, rectScreenHeight;

    private Matrix tempMatrix = new Matrix();

    /**
     * 座位画笔、指示器画笔、行号画笔、荧幕画笔、概略图画笔
     */
    private Paint mPaint, indicatorPaint, rowNumberPaint, screenPaint, overviewPaint;

    private CheckListener mCheckListener;

    public SeatTableView(Context context) {
        this(context, null);
    }

    public SeatTableView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SeatTableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.SeatTableView);
        int availableId = array.getResourceId(R.styleable.SeatTableView_seat_available, R.mipmap.seat_available);
        seatAvailable = BitmapFactory.decodeResource(getResources(), availableId);
        int checkedId = array.getResourceId(R.styleable.SeatTableView_seat_checked, R.mipmap.seat_checked);
        seatChecked = BitmapFactory.decodeResource(getResources(), checkedId);
        int soldId = array.getResourceId(R.styleable.SeatTableView_seat_sold, R.mipmap.seat_sold);
        seatSold = BitmapFactory.decodeResource(getResources(), soldId);
        seatSpacing = array.getDimension(R.styleable.SeatTableView_seat_spacing, getResources().getDimension(R.dimen.dp_5));
        topSpacing = array.getDimension(R.styleable.SeatTableView_seat_top_spacing, getResources().getDimension(R.dimen.dp_10));

        overviewChecked = array.getColor(R.styleable.SeatTableView_overview_checked, Color.parseColor("#579F62"));
        overviewSold = array.getColor(R.styleable.SeatTableView_overview_sold, Color.parseColor("#E775C0"));

        txtColor = array.getColor(R.styleable.SeatTableView_txt_color, Color.GRAY);
        txtSize = array.getDimension(R.styleable.SeatTableView_txt_size, getResources().getDimension(R.dimen.sp_12));
        indicatorHeight = array.getDimension(R.styleable.SeatTableView_indicator_height, getResources().getDimension(R.dimen.dp_30));
        indicatorColor = array.getColor(R.styleable.SeatTableView_indicator_color, 0);
        pointRadius = array.getDimension(R.styleable.SeatTableView_indicator_radius, getResources().getDimension(R.dimen.dp_7_5));
        indicatorIsColor = array.getBoolean(R.styleable.SeatTableView_indicator_is_color, true);
        indicatorChecked = array.getColor(R.styleable.SeatTableView_indicator_checked, Color.parseColor("#579F62"));
        indicatorSold = array.getColor(R.styleable.SeatTableView_indicator_sold, Color.parseColor("#E775C0"));
        indicatorAvailable = array.getColor(R.styleable.SeatTableView_indicator_available, Color.parseColor("#EBEBEB"));

        numberWidth = array.getDimension(R.styleable.SeatTableView_side_width, getResources().getDimension(R.dimen.dp_15));
        sideTextSize = array.getDimension(R.styleable.SeatTableView_side_text_size, getResources().getDimension(R.dimen.sp_12));
        leftMargin = array.getDimension(R.styleable.SeatTableView_side_left_margin, getResources().getDimension(R.dimen.dp_5));

        screenWidth = array.getDimension(R.styleable.SeatTableView_screen_width, getResources().getDimension(R.dimen.dp_200));
        float tempWidth = getResources().getDimension(R.dimen.dp_100);
        if (screenWidth < tempWidth) {
            screenWidth = tempWidth;
        }
        screenHeight = array.getDimension(R.styleable.SeatTableView_screen_height, getResources().getDimension(R.dimen.dp_20));
        screenColor = array.getColor(R.styleable.SeatTableView_screen_color, Color.parseColor("#E1DFE1"));
        indicatorScreenSwap = array.getBoolean(R.styleable.SeatTableView_indicator_screen_swap, false);
        array.recycle();
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        seatWidth = seatAvailable.getWidth();
        seatHeight = seatAvailable.getHeight();

        indicatorPaint = new Paint();
        indicatorPaint.setAntiAlias(true);
        indicatorPaint.setStyle(Paint.Style.FILL);
        indicatorPaint.setTextSize(txtSize);

        rowNumberPaint = new Paint();
        rowNumberPaint.setAntiAlias(true);

        screenPaint = new Paint();
        screenPaint.setAntiAlias(true);
        screenPaint.setStyle(Paint.Style.FILL);

        overviewPaint = new Paint();
        overviewPaint.setAntiAlias(true);
        overviewPaint.setStrokeWidth(getResources().getDimension(R.dimen.dp_1));
        overviewPadding = getResources().getDimension(R.dimen.dp_3);
        rectScreenHeight = getResources().getDimension(R.dimen.dp_2);
        rectHeight = seatHeight / overviewScale;
        rectWidth = seatWidth / overviewScale;
        overviewSpacing = seatSpacing / overviewScale;
    }

    /**
     * 获取行数
     *
     * @return
     */
    private int getRowNum() {
        if (mSeats == null) {
            return 0;
        }
        return mSeats.size();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        float seatWidth = this.seatWidth * column + seatSpacing * (column - 1);
        width = width - getPaddingLeft() - getPaddingRight();
        if (seatWidth <= width) {
            scale = width / seatWidth;
        }

        overviewWidth = rectWidth * column + overviewSpacing * (column - 1) + overviewPadding * 2;
        overviewHeight = rectHeight * getRowNum() + overviewSpacing * (getRowNum() - 1) + overviewPadding * 3 + rectScreenHeight;
        overviewBitmap = Bitmap.createBitmap((int) overviewWidth, (int) overviewHeight, Bitmap.Config.ARGB_4444);
        overviewBitmap.eraseColor(Color.TRANSPARENT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getRowNum() <= 0 || column <= 0) {
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
        // 绘制概览图
        if (isShowOverview) {
            drawOverview(canvas);
        }
    }

    /**
     * 绘制概览图
     *
     * @param canvas
     */
    private void drawOverview(final Canvas canvas) {
        overviewPaint.setStyle(Paint.Style.FILL);
        overviewPaint.setColor(Color.parseColor("#7e000000"));
        canvas.drawRect(0, 0, overviewWidth, overviewHeight, overviewPaint);
        float left = (overviewWidth - (rectWidth + overviewSpacing) * column * 2 / 3 - overviewSpacing) / 2;
        float right = left + (rectWidth + overviewSpacing) * column * 2 / 3;
        float top = overviewPadding;
        float bottom = top + rectScreenHeight;
        overviewPaint.setColor(Color.BLUE);
        overviewPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(left, top, right, bottom, overviewPaint);
        getFor(new ForCallBack() {
            @Override
            public void callBack(int i, int j, Seat seat) {
                if (seat.getState() != Seat.SEAT_TYPE_NOT_AVAILABLE) {
                    float left = j * rectWidth + j * overviewSpacing + overviewPadding;
                    float right = left + rectWidth;
                    float top = i * rectHeight + i * overviewSpacing + overviewPadding * 2 + rectScreenHeight;
                    float bottom = top + rectHeight;
                    overviewPaint.setStyle(Paint.Style.FILL);
                    switch (seat.getState()) {
                        case Seat.SEAT_TYPE_SOLD:   // 已订
                            overviewPaint.setColor(overviewSold);
                            canvas.drawRect(left, top, right, bottom, overviewPaint);
                            break;
                        case Seat.SEAT_TYPE_SELECTED:   // 选中
                            overviewPaint.setColor(overviewChecked);
                            canvas.drawRect(left, top, right, bottom, overviewPaint);
                            break;
                        case Seat.SEAT_TYPE_AVAILABLE:  // 可选
                            overviewPaint.setColor(Color.WHITE);
                            canvas.drawRect(left, top, right, bottom, overviewPaint);
                            break;
                    }
                }
            }
        });
    }

    /**
     * 绘制荧幕
     *
     * @param canvas
     */
    private void drawScreen(Canvas canvas) {
        float left = (getWidth() - screenWidth) / 2;
        float top = getScreenTop();
        float moveSize = screenHeight * 0.2f;
        Path path = new Path();
        path.moveTo(left, top);
        path.lineTo(left + moveSize, top + screenHeight);
        path.lineTo(left + screenWidth - moveSize, top + screenHeight);
        path.lineTo(left + screenWidth, top);
        path.close();
        screenPaint.setColor(screenColor);
        canvas.drawPath(path, screenPaint);
    }

    /**
     * 绘制行号
     *
     * @param canvas
     */
    private void drawNumber(Canvas canvas) {
        RectF rectF = new RectF();
        rectF.top = getSeatTop() - seatSpacing / 2;
        rectF.bottom = rectF.top + (seatHeight + seatSpacing) * getRowNum() * scale - seatSpacing * scale + seatSpacing;
        rectF.left = leftMargin;
        rectF.right = numberWidth + leftMargin;
        rowNumberPaint.setColor(Color.BLACK);
        rowNumberPaint.setAlpha(100);
        canvas.drawRoundRect(rectF, numberWidth / 2 + leftMargin, numberWidth / 2, rowNumberPaint);

        rowNumberPaint.setColor(Color.WHITE);
        rowNumberPaint.setTextSize(sideTextSize);

        for (int i = 0; i < mSeatRows.size(); i++) {
            String rowNumber = mSeatRows.get(i);
            if (!rowNumber.contains(NOTHING)) {
                float textWidth = rowNumberPaint.measureText(rowNumber);
                float top = i * seatHeight * scale + i * seatSpacing * scale + getSeatTop() - seatSpacing * scale + seatSpacing / 2;
                float bottom = top + (seatHeight + seatSpacing) * scale;
                float baseLine = getBaseLine(rowNumberPaint, top, bottom);
                canvas.drawText(rowNumber, (numberWidth - textWidth) / 2 + leftMargin, baseLine, rowNumberPaint);
            }
        }
    }

    /**
     * 获取指示器开始的顶部位置
     *
     * @return
     */
    private float getIndicatorTop() {
        if (!indicatorScreenSwap) {
            return 0;
        }
        return screenHeight;
    }

    /**
     * 获取屏幕开始的顶部位置
     *
     * @return
     */
    private float getScreenTop() {
        if (indicatorScreenSwap) {
            return 0;
        }
        return indicatorHeight;
    }

    /**
     * 获取座位开始的顶部位置
     *
     * @return
     */
    private float getSeatTop() {
        return indicatorHeight + topSpacing + screenHeight;
    }

    /**
     * 绘制指示器
     *
     * @param canvas
     */
    private void drawIndicator(Canvas canvas) {
        float textY = getBaseLine(indicatorPaint, getIndicatorTop(), indicatorHeight + getIndicatorTop());
        float textWidth = indicatorPaint.measureText("已售");
        float spacing = getResources().getDimension(R.dimen.dp_10);
        float spacing1 = getResources().getDimension(R.dimen.dp_5);
        float width = pointRadius * 2 * 3 + textWidth * 3 + spacing * 2 + spacing1 * 3;
        indicatorPaint.setColor(indicatorColor);
        // 绘制背景
        canvas.drawRect(0, getIndicatorTop(), getWidth(), indicatorHeight + getIndicatorTop(), indicatorPaint);
        indicatorPaint.setColor(Color.WHITE);

        if (indicatorIsColor) {
            float y = indicatorHeight / 2 + getIndicatorTop();

            float firstPointX = (getWidth() - width) / 2 + pointRadius;
            float firstX = firstPointX + pointRadius + spacing1;
            indicatorPaint.setColor(indicatorAvailable);
            canvas.drawCircle(firstPointX, y, pointRadius, indicatorPaint);
            indicatorPaint.setColor(txtColor);
            canvas.drawText("可选", firstX, textY, indicatorPaint);

            float secondPointX = firstX + textWidth + spacing + pointRadius;
            float secondX = secondPointX + pointRadius + spacing1;
            indicatorPaint.setColor(indicatorChecked);
            canvas.drawCircle(secondPointX, y, pointRadius, indicatorPaint);
            indicatorPaint.setColor(txtColor);
            canvas.drawText("选中", secondX, textY, indicatorPaint);

            float thirdPointX = secondX + textWidth + spacing + pointRadius;
            float thirdX = thirdPointX + pointRadius + spacing1;
            indicatorPaint.setColor(indicatorSold);
            canvas.drawCircle(thirdPointX, y, pointRadius, indicatorPaint);
            indicatorPaint.setColor(txtColor);
            canvas.drawText("已售", thirdX, textY, indicatorPaint);
        } else {
            float scaleX = pointRadius * 2 / seatWidth;
            float scaleY = pointRadius * 2 / seatHeight;
            float y = getIndicatorTop() + indicatorHeight / 2 - pointRadius;
            float firstPointX = (getWidth() - width) / 2;
            float firstX = firstPointX + pointRadius * 2 + spacing1;
            tempMatrix.setTranslate(firstPointX, y);
            tempMatrix.postScale(scaleX, scaleY, firstPointX, y);
            canvas.drawBitmap(seatAvailable, tempMatrix, indicatorPaint);
            indicatorPaint.setColor(txtColor);
            canvas.drawText("可选", firstX, textY, indicatorPaint);

            float secondPointX = firstX + textWidth + spacing;
            float secondX = secondPointX + pointRadius * 2 + spacing1;
            tempMatrix.setTranslate(secondPointX, y);
            tempMatrix.postScale(scaleX, scaleY, secondPointX, y);
            canvas.drawBitmap(seatChecked, tempMatrix, indicatorPaint);
            indicatorPaint.setColor(txtColor);
            canvas.drawText("选中", secondX, textY, indicatorPaint);

            float thirdPointX = secondX + textWidth + spacing;
            float thirdX = thirdPointX + pointRadius * 2 + spacing1;
            tempMatrix.setTranslate(thirdPointX, y);
            tempMatrix.postScale(scaleX, scaleY, thirdPointX, y);
            canvas.drawBitmap(seatSold, tempMatrix, indicatorPaint);
            indicatorPaint.setColor(txtColor);
            canvas.drawText("已售", thirdX, textY, indicatorPaint);
        }

        //绘制分割线
        indicatorPaint.setStrokeWidth(1);
        indicatorPaint.setColor(Color.GRAY);
        canvas.drawLine(0, indicatorHeight + getIndicatorTop(), getWidth(), indicatorHeight + getIndicatorTop(), indicatorPaint);
    }

    private float getBaseLine(Paint p, float top, float bottom) {
        Paint.FontMetrics fontMetrics = p.getFontMetrics();
        return (bottom + top - fontMetrics.bottom - fontMetrics.top) / 2;
    }

    /**
     * 选中
     *
     * @param seat
     */
    private void checked(Seat seat) {
        seat.setState(Seat.SEAT_TYPE_SELECTED);
        selectSeats.add(seat);
        invalidate();
        if (mCheckListener != null) {
            mCheckListener.checked(seat);
        }
    }

    /**
     * 取消选中
     *
     * @param seat
     */
    private void uncheck(Seat seat) {
        seat.setState(Seat.SEAT_TYPE_AVAILABLE);
        selectSeats.remove(seat);
        invalidate();
        if (mCheckListener != null) {
            mCheckListener.uncheck(seat);
        }
    }

    private GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            final float x = event.getX();
            final float y = event.getY();
            getFor(new ForCallBack() {
                @Override
                public void callBack(int i, int j, Seat seat) {
                    float tempX = j * seatWidth * scale + j * seatSpacing * scale + getPaddingLeft();
                    float maxTempX = tempX + seatWidth * scale;
                    float tempY = i * seatHeight * scale + i * seatSpacing * scale + getSeatTop();
                    float maxTempY = tempY + seatHeight * scale;
//                    Log.e("TAG", "onSingleTapConfirmed: x->" + x + "    tempX->" + tempX + "    maxTempX->" + maxTempX + "   y->" + y + "   tempY->" + tempY + "    maxTempY->" + maxTempY + "     other->" + (seatSpacing * scale));
                    if (x >= tempX && x <= maxTempX && y >= tempY && y <= maxTempY) {
                        if (seat.getState() == Seat.SEAT_TYPE_AVAILABLE) {
                            if (selectSeats.size() < maxSelected) {
                                checked(seat);
                            } else {
                                Toast.makeText(getContext(), "最多只能选择" + maxSelected + "个", Toast.LENGTH_SHORT).show();
                            }
                        } else if (seat.getState() == Seat.SEAT_TYPE_SELECTED) {
                            uncheck(seat);
                        }
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
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    /**
     * 设置选择监听
     *
     * @param checkListener
     */
    public SeatTableView setCheckListener(CheckListener checkListener) {
        this.mCheckListener = checkListener;
        return this;
    }

    /**
     * 设置位置布局
     *
     * @param seats
     * @return
     */
    public void setSeat(List<List<Seat>> seats) {
        this.mSeats = seats;
        column = 0;
        int index = 0;
        for (List<Seat> list : seats) {
            column = Math.max(column, list.size());
            boolean flag = false;
            for (Seat seat : list) {
                if (seat != null && !seat.isEmpty()) {
                    mSeatRows.add(seat.getRow());
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                index++;
                String str = NOTHING + index;
                mSeatRows.add(str);
            }
        }
        invalidate();
    }

    /**
     * 获取选中的位置
     *
     * @return
     */
    public List<Seat> getSelectSeat() {
        return selectSeats;
    }

    /**
     * 取消选中
     *
     * @param seat
     */
    public void unselect(Seat seat) {
        for (Seat selectSeat : selectSeats) {
            if (seat.equals(selectSeat)) {
                uncheck(selectSeat);
                return;
            }
        }
    }

    /**
     * 绘制座位
     */
    private void drawSeat(final Canvas canvas) {
        getFor(new ForCallBack() {
            @Override
            public void callBack(int i, int j, Seat seat) {
                if (seat.getState() != Seat.SEAT_TYPE_NOT_AVAILABLE) {
                    float left = j * seatWidth * scale + j * seatSpacing * scale + getPaddingLeft();
                    float top = i * seatHeight * scale + i * seatSpacing * scale + getSeatTop();
                    tempMatrix.setTranslate(left, top);
                    tempMatrix.postScale(scale, scale, left, top);
                    switch (seat.getState()) {
                        case Seat.SEAT_TYPE_SOLD:   // 已订
                            canvas.drawBitmap(seatSold, tempMatrix, mPaint);
                            break;
                        case Seat.SEAT_TYPE_SELECTED:   // 选中
                            canvas.drawBitmap(seatChecked, tempMatrix, mPaint);
                            break;
                        case Seat.SEAT_TYPE_AVAILABLE:  // 可选
                            canvas.drawBitmap(seatAvailable, tempMatrix, mPaint);
                            break;
                    }
                }
            }
        });
    }

    /**
     * for循环统一处理
     *
     * @param callBack
     */
    private void getFor(ForCallBack callBack) {
        // 行
        for (int i = 0; i < getRowNum(); i++) {
            // 列
            List<Seat> seats = mSeats.get(i);
            for (int j = 0; j < seats.size(); j++) {
                callBack.callBack(i, j, seats.get(j));
            }
        }
    }

    private interface ForCallBack {
        void callBack(int i, int j, Seat seat);
    }

    public interface CheckListener {
        /**
         * 选中
         *
         * @param seat
         */
        void checked(Seat seat);

        /**
         * 取消选中
         *
         * @param seat
         */
        void uncheck(Seat seat);
    }

}
