package com.mlethe.ui;

import android.animation.Animator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
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
     * 座位宽度、高度、高宽比
     */
    private float seatWidth, seatHeight, aspectRatio;

    /**
     * 座位默认宽度、高度
     */
    private float seatDefaultW, seatDefaultH;

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
     * 荧幕颜色、荧幕背景颜色
     */
    private int screenColor, screenBackgroundColor;

    /**
     * 荧幕文字
     */
    private String screenText = "";

    /**
     * 指示器高度、指示器半径
     */
    private float indicatorHeight, pointRadius;

    /**
     * 指示器文字颜色、背景颜色、指示器点选中、已售、可选的颜色
     */
    private int txtColor, indicatorBackgroundColor, indicatorChecked, indicatorSold, indicatorAvailable;

    /**
     * 指示器点选中、已售、可选的文字
     */
    private String indicatorCheckedText, indicatorSoldText, indicatorAvailableText;

    /**
     * 指示器是否使用颜色值，默认false
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
     * 是否显示概览图、是否绘制概览图
     */
    private boolean isShowOverview = false, isDrawOverviewBitmap = true;

    /**
     * 概览图上方块选中、已售的颜色
     */
    private int overviewChecked, overviewSold;

    /**
     * 概览图宽度、高度、概览图白色方块的高度、宽度、概览图上方块的水平间距、垂直间距、概览图的比例、概览图padding、概览图上屏幕高度
     */
    float overviewHeight, overviewWidth, rectHeight, rectWidth, overviewSpacing, overviewScale = 4.8f, overviewPadding, rectScreenHeight;

    /**
     * 标识是否正在缩放、是否是第一次缩放
     */
    boolean isScaling, firstScale = true;
    private float mScaleX, mScaleY;

    private Matrix tempMatrix = new Matrix();
    private Matrix matrix = new Matrix();

    private float[] m = new float[9];

    /**
     * 座位画笔、指示器画笔、行号画笔、荧幕画笔、概略图画笔、红色边框画笔
     */
    private Paint mPaint, indicatorPaint, rowNumberPaint, screenPaint, overviewPaint, redBorderPaint;

    /**
     * 红色边框大小
     */
    private float redBorderSize;

    private CheckListener mCheckListener;
    private boolean pointer;
    private float downX, downY;
    private boolean isOnClick = false;
    private Handler handler = new Handler();
    private Runnable hideOverviewRunnable = new Runnable() {
        @Override
        public void run() {
            isShowOverview = false;
            invalidate();
        }
    };
    private float lastX, lastY;

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
        indicatorBackgroundColor = array.getColor(R.styleable.SeatTableView_indicator_background_color, 0);
        pointRadius = array.getDimension(R.styleable.SeatTableView_indicator_radius, getResources().getDimension(R.dimen.dp_7_5));
        indicatorIsColor = array.getBoolean(R.styleable.SeatTableView_indicator_is_color, false);
        indicatorChecked = array.getColor(R.styleable.SeatTableView_indicator_checked, Color.parseColor("#579F62"));
        indicatorSold = array.getColor(R.styleable.SeatTableView_indicator_sold, Color.parseColor("#E775C0"));
        indicatorAvailable = array.getColor(R.styleable.SeatTableView_indicator_available, Color.parseColor("#EBEBEB"));
        indicatorCheckedText = array.getString(R.styleable.SeatTableView_indicator_checked_text);
        if (indicatorCheckedText == null) {
            indicatorCheckedText = "";
        }
        indicatorSoldText = array.getString(R.styleable.SeatTableView_indicator_sold_text);
        if (indicatorSoldText == null) {
            indicatorSoldText = "";
        }
        indicatorAvailableText = array.getString(R.styleable.SeatTableView_indicator_available_text);
        if (indicatorAvailableText == null) {
            indicatorAvailableText = "";
        }

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
        screenBackgroundColor = array.getColor(R.styleable.SeatTableView_screen_background_color, 0);
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
        aspectRatio = seatHeight / seatWidth;

        indicatorPaint = new Paint();
        indicatorPaint.setAntiAlias(true);
        indicatorPaint.setStyle(Paint.Style.FILL);
        indicatorPaint.setTextSize(txtSize);

        rowNumberPaint = new Paint();
        rowNumberPaint.setAntiAlias(true);

        screenPaint = new Paint();
        screenPaint.setAntiAlias(true);
        screenPaint.setStyle(Paint.Style.FILL);
        screenPaint.setTextSize(getResources().getDimension(R.dimen.sp_12));

        overviewPaint = new Paint();
        overviewPaint.setAntiAlias(true);
        redBorderPaint = new Paint();
        redBorderPaint.setAntiAlias(true);
        redBorderSize = getResources().getDimension(R.dimen.dp_1);
        redBorderPaint.setStrokeWidth(redBorderSize);
        redBorderPaint.setStyle(Paint.Style.STROKE);
        redBorderPaint.setColor(Color.RED);
        overviewPaint.setStrokeWidth(getResources().getDimension(R.dimen.dp_1));
        overviewPadding = getResources().getDimension(R.dimen.dp_5);
        rectScreenHeight = getResources().getDimension(R.dimen.dp_2);
        overviewSpacing = seatSpacing / overviewScale;

        matrix.postTranslate(getPaddingLeft(), getSeatTop());
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
        width = width - getPaddingLeft() - getPaddingRight();
        seatDefaultW = (width - seatSpacing * (column - 1)) / column;
        seatDefaultH = seatDefaultW * aspectRatio;
        scale = seatDefaultW / seatWidth;

        rectHeight = seatDefaultH / overviewScale;
        rectWidth = seatDefaultW / overviewScale;
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
        // 绘制座位
        drawSeat(canvas);
        // 绘制行号
        drawNumber(canvas);
        // 绘制指示器
        drawIndicator(canvas);
        // 绘制荧幕
        drawScreen(canvas);
        // 绘制概览图
        if (isShowOverview) {
            if (isDrawOverviewBitmap) {
                drawOverview();
            }
            canvas.drawBitmap(overviewBitmap, 0, getPaddingTop(), overviewPaint);
            // 绘制红色框
            drawRedOverview(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        float x = event.getX();
        float y = event.getY();
        gestureDetector.onTouchEvent(event);
        scaleGestureDetector.onTouchEvent(event);

        int pointerCount = event.getPointerCount();
        if (pointerCount > 1) {
            pointer = true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pointer = false;
                downX = x;
                downY = y;
                isShowOverview = true;
                handler.removeCallbacks(hideOverviewRunnable);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isScaling && !isOnClick) {
                    float downDX = Math.abs(x - downX);
                    float downDY = Math.abs(y - downY);
                    if ((downDX > 10 || downDY > 10) && !pointer) {
                        float dx = x - lastX;
                        float dy = y - lastY;
                        matrix.postTranslate(dx, dy);
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                handler.postDelayed(hideOverviewRunnable, 1500);

                autoScale();
                float downDX = Math.abs(x - downX);
                float downDY = Math.abs(y - downY);
                if ((downDX > 10 || downDY > 10) && !pointer) {
                    autoScroll();
                }
                break;
        }

        isOnClick = false;
        lastY = y;
        lastX = x;
        return true;
    }

    /**
     * 自动回弹
     * 整个大小不超过控件大小的时候:
     * 往左边滑动,自动回弹到行号右边
     * 往右边滑动,自动回弹到右边
     * 往上,下滑动,自动回弹到顶部
     * <p>
     * 整个大小超过控件大小的时候:
     * 往左侧滑动,回弹到最右边,往右侧滑回弹到最左边
     * 往上滑动,回弹到底部,往下滑动回弹到顶部
     */
    private void autoScroll() {
        float currentSeatBitmapWidth = seatWidth * getMatrixScaleX();
        float currentSeatBitmapHeight = seatHeight * getMatrixScaleY();
        float moveYLength = 0;
        float moveXLength = 0;

        //处理左右滑动的情况
        if (currentSeatBitmapWidth < getWidth()) {
            if (getTranslateX() < 0 || getMatrixScaleX() < numberWidth + seatSpacing) {
                //计算要移动的距离

                if (getTranslateX() < 0) {
                    moveXLength = (-getTranslateX()) + numberWidth + seatSpacing;
                } else {
                    moveXLength = numberWidth + seatSpacing - getTranslateX();
                }

            }
        } else {

            if (getTranslateX() < 0 && getTranslateX() + currentSeatBitmapWidth > getWidth()) {

            } else {
                //往左侧滑动
                if (getTranslateX() + currentSeatBitmapWidth < getWidth()) {
                    moveXLength = getWidth() - (getTranslateX() + currentSeatBitmapWidth);
                } else {
                    //右侧滑动
                    moveXLength = -getTranslateX() + numberWidth + seatSpacing;
                }
            }

        }

        float startYPosition = getSeatTop() * getMatrixScaleY();

        //处理上下滑动
        if (currentSeatBitmapHeight + getSeatTop() < getHeight()) {

            if (getTranslateY() < startYPosition) {
                moveYLength = startYPosition - getTranslateY();
            } else {
                moveYLength = -(getTranslateY() - (startYPosition));
            }

        } else {

            if (getTranslateY() < 0 && getTranslateY() + currentSeatBitmapHeight > getHeight()) {

            } else {
                //往上滑动
                if (getTranslateY() + currentSeatBitmapHeight < getHeight()) {
                    moveYLength = getHeight() - (getTranslateY() + currentSeatBitmapHeight);
                } else {
                    moveYLength = -(getTranslateY() - (startYPosition));
                }
            }
        }

        Point start = new Point();
        start.x = (int) getTranslateX();
        start.y = (int) getTranslateY();

        Point end = new Point();
        end.x = (int) (start.x + moveXLength);
        end.y = (int) (start.y + moveYLength);

        moveAnimate(start, end);

    }

    private void autoScale() {

        if (getMatrixScaleX() > 2.2) {
            zoomAnimate(getMatrixScaleX(), 2.0f);
        } else if (getMatrixScaleX() < 0.98) {
            zoomAnimate(getMatrixScaleX(), 1.0f);
        }
    }

    private float zoom;

    private void zoom(float zoom) {
        float z = zoom / getMatrixScaleX();
        matrix.postScale(z, z, mScaleX, mScaleY);
        invalidate();
    }

    private void move(Point p) {
        float x = p.x - getTranslateX();
        float y = p.y - getTranslateY();
        matrix.postTranslate(x, y);
        invalidate();
    }

    private void moveAnimate(Point start, Point end) {
        ValueAnimator valueAnimator = ValueAnimator.ofObject(new MoveEvaluator(), start, end);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        MoveAnimation moveAnimation = new MoveAnimation();
        valueAnimator.addUpdateListener(moveAnimation);
        valueAnimator.setDuration(400);
        valueAnimator.start();
    }

    private void zoomAnimate(float cur, float tar) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(cur, tar);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        ZoomAnimation zoomAnim = new ZoomAnimation();
        valueAnimator.addUpdateListener(zoomAnim);
        valueAnimator.addListener(zoomAnim);
        valueAnimator.setDuration(400);
        valueAnimator.start();
    }

    class MoveAnimation implements ValueAnimator.AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            Point p = (Point) animation.getAnimatedValue();
            move(p);
        }
    }

    class MoveEvaluator implements TypeEvaluator {

        @Override
        public Object evaluate(float fraction, Object startValue, Object endValue) {
            Point startPoint = (Point) startValue;
            Point endPoint = (Point) endValue;
            int x = (int) (startPoint.x + fraction * (endPoint.x - startPoint.x));
            int y = (int) (startPoint.y + fraction * (endPoint.y - startPoint.y));
            return new Point(x, y);
        }
    }

    class ZoomAnimation implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            zoom = (Float) animation.getAnimatedValue();
            zoom(zoom);
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }

        @Override
        public void onAnimationStart(Animator animation) {
        }

    }

    /**
     * 绘制概览图
     */
    private void drawOverview() {
        final Canvas canvas = new Canvas(overviewBitmap);
        overviewPaint.setStyle(Paint.Style.FILL);
        overviewPaint.setColor(Color.parseColor("#7e000000"));
        overviewBitmap.eraseColor(Color.TRANSPARENT);
        canvas.drawRect(0, getOverviewTop(), overviewWidth, overviewHeight + getOverviewTop(), overviewPaint);

        float left = (overviewWidth - (rectWidth + overviewSpacing) * column * 2 / 3 - overviewSpacing) / 2;
        float right = left + (rectWidth + overviewSpacing) * column * 2 / 3;
        float top = overviewPadding + getOverviewTop();
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
                    float top = i * rectHeight + i * overviewSpacing + overviewPadding * 2 + rectScreenHeight + getOverviewTop();
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
     * 绘制红色框
     * @param canvas
     */
    private void drawRedOverview(Canvas canvas) {
        float left = overviewPadding - redBorderSize * 2;
        float right = overviewWidth - overviewPadding + redBorderSize * 2;
        float top = overviewPadding * 2 + rectScreenHeight - redBorderSize * 2;
        float bottom = overviewHeight - overviewPadding + redBorderSize * 2;
        canvas.drawRect(left, top, right, bottom, redBorderPaint);
    }

    /**
     * 绘制荧幕
     *
     * @param canvas
     */
    private void drawScreen(Canvas canvas) {
        float top = getScreenTop();
        float bottom = top + screenHeight;

        // 绘制背景
        screenPaint.setColor(screenBackgroundColor);
        canvas.drawRect(0, top, getWidth(), bottom, screenPaint);
        float left = (getWidth() - screenWidth) / 2;

        float moveSize = screenHeight * 0.2f;
        Path path = new Path();
        path.moveTo(left, top);
        path.lineTo(left + moveSize, top + screenHeight);
        path.lineTo(left + screenWidth - moveSize, top + screenHeight);
        path.lineTo(left + screenWidth, top);
        path.close();
        screenPaint.setColor(screenColor);
        canvas.drawPath(path, screenPaint);

        float baseLine = getBaseLine(screenPaint, top, top + screenHeight);
        float textWidth = screenPaint.measureText(screenText);
        float x = (getWidth() - textWidth) / 2;
        screenPaint.setColor(Color.BLACK);
        canvas.drawText(screenText, x, baseLine, screenPaint);
    }

    /**
     * 绘制行号
     *
     * @param canvas
     */
    private void drawNumber(Canvas canvas) {
        float translateY = getTranslateY();
        float scaleY = getMatrixScaleY();

        RectF rectF = new RectF();
        rectF.top = translateY - seatSpacing / 2;
        rectF.bottom = rectF.top + seatHeight * getRowNum() * scale * scaleY + seatSpacing * scaleY * getRowNum();
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
                float top = i * seatHeight * scale * scaleY + i * seatSpacing * scaleY + translateY;
                float bottom = top + seatHeight * scale * scaleY;
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
            return getPaddingTop();
        }
        return screenHeight + getScreenTop();
    }

    /**
     * 获取屏幕开始的顶部位置
     *
     * @return
     */
    private float getScreenTop() {
        if (indicatorScreenSwap) {
            return getPaddingTop();
        }
        return indicatorHeight + getIndicatorTop();
    }

    /**
     * 获取概览图顶部位置
     *
     * @return
     */
    private float getOverviewTop() {
        return getPaddingTop();
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
        float top = getIndicatorTop();
        float bottom = top + indicatorHeight;
        float textY = getBaseLine(indicatorPaint, top, bottom);
        float availableTextWidth = indicatorPaint.measureText(indicatorAvailableText);
        float checkedTextWidth = indicatorPaint.measureText(indicatorCheckedText);
        float soldTextWidth = indicatorPaint.measureText(indicatorSoldText);
        float spacing = getResources().getDimension(R.dimen.dp_10);
        float spacing1 = getResources().getDimension(R.dimen.dp_5);
        float width = pointRadius * 2 * 3 + availableTextWidth + checkedTextWidth + soldTextWidth + spacing * 2 + spacing1 * 3;
        indicatorPaint.setColor(indicatorBackgroundColor);
        // 绘制背景
        canvas.drawRect(0, top, getWidth(), bottom, indicatorPaint);
        indicatorPaint.setColor(Color.WHITE);

        if (indicatorIsColor) {
            float y = indicatorHeight / 2 + top;

            float firstPointX = (getWidth() - width) / 2 + pointRadius;
            float firstX = firstPointX + pointRadius + spacing1;
            indicatorPaint.setColor(indicatorAvailable);
            canvas.drawCircle(firstPointX, y, pointRadius, indicatorPaint);
            indicatorPaint.setColor(txtColor);
            canvas.drawText(indicatorAvailableText, firstX, textY, indicatorPaint);

            float secondPointX = firstX + availableTextWidth + spacing + pointRadius;
            float secondX = secondPointX + pointRadius + spacing1;
            indicatorPaint.setColor(indicatorChecked);
            canvas.drawCircle(secondPointX, y, pointRadius, indicatorPaint);
            indicatorPaint.setColor(txtColor);
            canvas.drawText(indicatorCheckedText, secondX, textY, indicatorPaint);

            float thirdPointX = secondX + checkedTextWidth + spacing + pointRadius;
            float thirdX = thirdPointX + pointRadius + spacing1;
            indicatorPaint.setColor(indicatorSold);
            canvas.drawCircle(thirdPointX, y, pointRadius, indicatorPaint);
            indicatorPaint.setColor(txtColor);
            canvas.drawText(indicatorSoldText, thirdX, textY, indicatorPaint);
        } else {
            float scaleX = pointRadius * 2 / seatWidth;
            float scaleY = pointRadius * 2 * aspectRatio / seatHeight;
            float y = top + indicatorHeight / 2 - pointRadius * aspectRatio;
            float firstPointX = (getWidth() - width) / 2;
            float firstX = firstPointX + pointRadius * 2 + spacing1;
            tempMatrix.setTranslate(firstPointX, y);
            tempMatrix.postScale(scaleX, scaleY, firstPointX, y);
            canvas.drawBitmap(seatAvailable, tempMatrix, indicatorPaint);
            indicatorPaint.setColor(txtColor);
            canvas.drawText(indicatorAvailableText, firstX, textY, indicatorPaint);

            float secondPointX = firstX + availableTextWidth + spacing;
            float secondX = secondPointX + pointRadius * 2 + spacing1;
            tempMatrix.setTranslate(secondPointX, y);
            tempMatrix.postScale(scaleX, scaleY, secondPointX, y);
            canvas.drawBitmap(seatChecked, tempMatrix, indicatorPaint);
            indicatorPaint.setColor(txtColor);
            canvas.drawText(indicatorCheckedText, secondX, textY, indicatorPaint);

            float thirdPointX = secondX + checkedTextWidth + spacing;
            float thirdX = thirdPointX + pointRadius * 2 + spacing1;
            tempMatrix.setTranslate(thirdPointX, y);
            tempMatrix.postScale(scaleX, scaleY, thirdPointX, y);
            canvas.drawBitmap(seatSold, tempMatrix, indicatorPaint);
            indicatorPaint.setColor(txtColor);
            canvas.drawText(indicatorSoldText, thirdX, textY, indicatorPaint);
        }

        //绘制分割线
        indicatorPaint.setStrokeWidth(1);
        indicatorPaint.setColor(Color.GRAY);
        canvas.drawLine(0, indicatorHeight + getIndicatorTop(), getWidth(), indicatorHeight + getIndicatorTop(), indicatorPaint);
    }

    /**
     * 获取基线
     *
     * @param p
     * @param top
     * @param bottom
     * @return
     */
    private float getBaseLine(Paint p, float top, float bottom) {
        Paint.FontMetrics fontMetrics = p.getFontMetrics();
        return (bottom + top - fontMetrics.bottom - fontMetrics.top) / 2;
        /*Paint.FontMetrics fontMetrics = p.getFontMetrics();
        float dy = (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent;
//        float dy = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        return (bottom - top) / 2 + dy + top;*/
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

    /**
     * x轴上平移的值
     *
     * @return
     */
    private float getTranslateX() {
        matrix.getValues(m);
        return m[2];
    }

    /**
     * y轴上的平移的值
     *
     * @return
     */
    private float getTranslateY() {
        matrix.getValues(m);
        return m[5];
    }

    /**
     * y轴上的缩放比例
     *
     * @return
     */
    private float getMatrixScaleY() {
        matrix.getValues(m);
        return m[4];
    }

    /**
     * 缩放的x值
     *
     * @return
     */
    private float getMatrixScaleX() {
        matrix.getValues(m);
        return m[0];
    }

    private ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.OnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            isScaling = true;

            float scaleFactor = detector.getScaleFactor();
            if (getMatrixScaleY() * scaleFactor > 3) {
                scaleFactor = 3 / getMatrixScaleY();
            }
            if (firstScale) {
                mScaleX = detector.getCurrentSpanX();
                mScaleY = detector.getCurrentSpanY();
                firstScale = false;
            }

            if (getMatrixScaleY() * scaleFactor < 0.5) {
                scaleFactor = 0.5f / getMatrixScaleY();
            }
            matrix.postScale(scaleFactor, scaleFactor, mScaleX, mScaleY);
            invalidate();
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            return false;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
            isScaling = false;
            firstScale = true;
        }
    });

    private GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            final float x = event.getX();
            final float y = event.getY();
            final float scaleX = getMatrixScaleX();
            final float scaleY = getMatrixScaleY();
            getFor(new ForCallBack() {
                @Override
                public void callBack(int i, int j, Seat seat) {
                    float tempX = j * seatWidth * scale * scaleX + j * seatSpacing * scaleX + getTranslateX();
                    float maxTempX = tempX + seatWidth * scale * scaleX;
                    float tempY = i * seatHeight * scale * scaleY + i * seatSpacing * scaleY + getTranslateY();
                    float maxTempY = tempY + seatHeight * scale * scaleY;
//                    Log.e("TAG", "onSingleTapConfirmed: x->" + x + "    tempX->" + tempX + "    maxTempX->" + maxTempX + "   y->" + y + "   tempY->" + tempY + "    maxTempY->" + maxTempY + "     other->" + (seatSpacing * scale));
                    if (x >= tempX && x <= maxTempX && y >= tempY && y <= maxTempY) {
                        if (seat.getState() == Seat.SEAT_TYPE_AVAILABLE) {
                            if (selectSeats.size() < maxSelected) {
                                checked(seat);
                            } else {
                                if (mCheckListener != null) {
                                    mCheckListener.overstep(maxSelected);
                                } else {
                                    Toast.makeText(getContext(), "最多只能选择" + maxSelected + "个", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else if (seat.getState() == Seat.SEAT_TYPE_SELECTED) {
                            uncheck(seat);
                        }
                    }

                    float currentScaleY = getMatrixScaleY();

                    if (currentScaleY < 1.7) {
                        mScaleX = x;
                        mScaleY = y;
                        zoomAnimate(currentScaleY, 1.9f);
                    }
                }
            });
            return super.onSingleTapConfirmed(event);
        }
    });

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
     * 设置荧幕文字
     *
     * @param text
     * @return
     */
    public SeatTableView setScreenText(String text) {
        this.screenText = text;
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
                    float scaleX = getMatrixScaleX();
                    float scaleY = getMatrixScaleY();
//                    Log.e("TAG", "callBack: scaleX->" + scaleX + "     scaleY->" + scaleY);
                    float left = j * seatWidth * scale * scaleX + j * seatSpacing * scaleX + getTranslateX();
                    float top = i * seatHeight * scale * scaleY + i * seatSpacing * scaleY + getTranslateY();
//                    Log.e("TAG", "callBack: left->" + left + "    top->" + top + "      scale->" + scale + "    seatWidth->" + seatWidth + "    seatDefaultW->" + seatDefaultW);
                    tempMatrix.setTranslate(left, top);
                    tempMatrix.postScale(scale * scaleX, scale * scaleY, left, top);
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

        /**
         * 超出最大值
         */
        void overstep(int max);
    }

}
