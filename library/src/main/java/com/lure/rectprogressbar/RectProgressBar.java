package com.lure.rectprogressbar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import static com.lure.rectprogressbar.Utils.measureSize;

/**
 * @author lxt <lxt352@gmail.com>
 * @since 2017/11/3.
 */

public class RectProgressBar extends View {

    private OnInvalidatedListener listener;

    private ValueAnimator animator;

    private Paint paint;

    private Rect[] rects;

    private int[] rectangles;

    private float[] scaleValue;

    private int number;

    private int itemColor;

    private int rectWidth = 20;

    private int rectHeight = 80;

    private int width;

    private int height;

    private int rectMargin = 5;

    private final OnInvalidatedListener settledListener = new OnInvalidatedListener() {
        @Override
        public void onInvalidated() {
            invalidate();
        }
    };

    public RectProgressBar(Context context) {
        this(context, null);
    }

    public RectProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RectProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RectProgressBar);
        number = typedArray.getInt(R.styleable.RectProgressBar_rectNumber, 6);
        itemColor = typedArray.getColor(R.styleable.RectProgressBar_rectColor,
                getResources().getColor(android.R.color.white));
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int desiredWidth = number * rectWidth + rectMargin * (number - 1);
        int desiredHeight = rectHeight;
        width = measureSize(desiredWidth, widthMeasureSpec);
        height = measureSize(desiredHeight, heightMeasureSpec);
        width = width > desiredWidth ? desiredWidth : width;
        height = height > desiredHeight ? desiredHeight : height;
        setMeasuredDimension(width, height);
        initPaint();
    }

    private void initPaint() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(itemColor);
        rectangles = new int[number];
        scaleValue = new float[number];
        rects = new Rect[number];
        int leftX, rightX;
        for (int i = 0; i < number; i++) {
            leftX = i * (rectWidth + rectMargin);
            rightX = leftX + rectWidth;
            rects[i] = new Rect(leftX, 0, rightX, height);
        }
    }

    int[] colors = new int[]{getResources().getColor(android.R.color.black),
            getResources().getColor(android.R.color.holo_green_dark),
            getResources().getColor(android.R.color.holo_orange_dark),
            getResources().getColor(android.R.color.holo_purple),
            getResources().getColor(android.R.color.holo_blue_bright),
            getResources().getColor(android.R.color.holo_red_dark)
    };

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < rectangles.length; i++) {
            canvas.save();
            Rect rect = rects[i];
            canvas.scale(1, scaleValue[i], rect.left, rect.centerY());
            paint.setColor(colors[i]);
            canvas.drawRect(rect, paint);
            canvas.restore();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        for (int i = 0; i < rectangles.length; i++) {
            final int index = i;
            animator = ValueAnimator.ofFloat(0.4f, 1f, 0.4f)
                    .setDuration(1000);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setStartDelay(i * 100);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    scaleValue[index] = (float) animation.getAnimatedValue();
                    if (listener != null)
                        listener.onInvalidated();
                }
            });
            animator.start();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (listener == null)
            listener = settledListener;
        setAnimatorState(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (listener != null)
            listener = null;
        setAnimatorState(false);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        setAnimatorState(visibility == VISIBLE);
    }

    private void setAnimatorState(boolean animate) {
        if (animator != null)
            if (animate)
                animator.start();
            else
                animator.cancel();
    }

    private interface OnInvalidatedListener {

        void onInvalidated();
    }
}
