package com.candy.android.custom.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.candy.android.R;

/**
 * Created by bruce on 15-12-10.
 */
public class DotIndicator extends View implements ViewPager.OnPageChangeListener {

    private static final int DEFAULT_RADIUS = 5;
    private static final int DEFAULT_DOT_SPACING = 5;
    private static final int DEFAULT_COLOR = Color.BLACK;
    private static final int DEFAULT_COLOR_NORMAL = Color.GRAY;

    private static final int EXTRA_PADDING = 2;

    private int mTotalPages;
    private int mCurrentPage;
    private int mRadius;
    private int mDotSpacing;

    private Paint mFilledCirclePaint;
    private Paint mHollowCirclePaint;

    private int mCircleColorSelected;
    private int mCircleColorNormal;

    public DotIndicator(Context context) {
        super(context);

        init();
    }

    public DotIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray arr = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.DotIndicator,
                0, 0);

        try {
            mRadius = arr.getDimensionPixelOffset(R.styleable.DotIndicator_radius, DEFAULT_RADIUS);
            mDotSpacing = arr.getDimensionPixelOffset(R.styleable.DotIndicator_dotSpacing, DEFAULT_DOT_SPACING);
            mCircleColorSelected = arr.getColor(R.styleable.DotIndicator_circleColorSelected, DEFAULT_COLOR);
            mCircleColorNormal = arr.getColor(R.styleable.DotIndicator_circleColorNormal, DEFAULT_COLOR_NORMAL);
        } finally {
            arr.recycle();
        }

        init();
    }

    @Override
    public void onPageSelected(int position) {
        setCurrentPage(position);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // Nothing to do
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // Nothing to do
    }

    public void setViewPager(final ViewPager viewPager) {
        if (viewPager == null) {
            throw new NullPointerException("ViewPager is null");
        }

        final PagerAdapter adapter = viewPager.getAdapter();
        if (adapter == null) {
            throw new IllegalArgumentException("ViewPager must have a PagerAdapter set");
        }

        // Add this class as a listener to the ViewPager to get notified when the page changes
        viewPager.addOnPageChangeListener(this);

        // Initialize number of pages
        setTotalPages(adapter.getCount());

        // Add an observer to get notified when the PagerAdapter dataset changes
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                setTotalPages(adapter.getCount());
            }
        });
    }

    public int getTotalPages() {
        return mTotalPages;
    }

    public void setTotalPages(int totalPages) {
        mTotalPages = totalPages;
        invalidate();
        requestLayout();
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }

    public void setCurrentPage(int currentPage) {
        mCurrentPage = currentPage;
        invalidate();
    }

    public int getRadius() {
        return mRadius;
    }

    public void setRadius(int radius) {
        mRadius = radius;
        invalidate();
        requestLayout();
    }

    public int getCircleColorSelected() {
        return mCircleColorSelected;
    }

    public void setCircleColorSelected(int color) {
        mCircleColorSelected = color;
        invalidate();
    }

    public int getDotSpacing() {
        return mDotSpacing;
    }

    public void setDotSpacing(int space) {
        mDotSpacing = space;
        invalidate();
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int space = mDotSpacing;
        int radius = mRadius;

        int x = radius + getPaddingLeft();
        int y = radius + getPaddingTop();
        for (int ndx = 0; ndx < mTotalPages; ndx++) {
            if (ndx == mCurrentPage) {
                canvas.drawCircle(x, y, radius, mFilledCirclePaint);
            } else {
                canvas.drawCircle(x, y, radius, mHollowCirclePaint);
            }
            x = x + (radius * 2) + space;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            if (mTotalPages == 0) {
                width = 0;
            } else {
                int padding = getPaddingLeft() + getPaddingRight() + EXTRA_PADDING;
                width = mRadius * 2 * mTotalPages + (mTotalPages - 1) * mDotSpacing + padding;
            }
        } else if (getLayoutParams().width == ViewGroup.LayoutParams.MATCH_PARENT) {
            width = View.MeasureSpec.getSize(widthMeasureSpec);
        } else {
            width = getLayoutParams().width;
        }

        int height;
        if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            int padding = getPaddingTop() + getPaddingBottom() + EXTRA_PADDING;
            height = mRadius * 2 + padding;
        } else if (getLayoutParams().height == ViewGroup.LayoutParams.MATCH_PARENT) {
            height = View.MeasureSpec.getSize(heightMeasureSpec);
        } else {
            height = getLayoutParams().height;
        }

        setMeasuredDimension(width, height);
    }

    private void init() {
        mFilledCirclePaint = new Paint();
        mFilledCirclePaint.setColor(mCircleColorSelected);
        mFilledCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mFilledCirclePaint.setAntiAlias(true);

        mHollowCirclePaint = new Paint();
        mHollowCirclePaint.setColor(mCircleColorNormal);
        mHollowCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mHollowCirclePaint.setAntiAlias(true);

        if (isInEditMode()) {
            mTotalPages = 5;
            mCurrentPage = 2;
        }
    }
}