/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.customview.graph;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;

/**
 * Created by bhargavsuthar on 07/06/16.
 */
public class TimeBucketGraph extends BaseView {

    private int mTotalActivityBeyondGoal;
    private int mTotalMinTarget;
    private int mTotalActivityDurationMin;
    private float mFillEndRange;
    private float txtStartValue;
    private float txtEndValue;
    private float mDifference;
    //equal parts
    private float mVolume = 0;

    /**
     * Instantiates a new Time bucket graph.
     *
     * @param context the context
     */
    public TimeBucketGraph(Context context) {
        super(context);
    }

    /**
     * Instantiates a new Time bucket graph.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public TimeBucketGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Instantiates a new Time bucket graph.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     */
    public TimeBucketGraph(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Instantiates a new Time bucket graph.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     * @param defStyleRes  the def style res
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TimeBucketGraph(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Graph arguments.
     *
     * @param totalActivityBeyondGoal      the total activity beyond goal
     * @param totalMinTarget               the total min target
     * @param totalActivityDurationMinutes the total activity duration minutes
     */
    public void graphArguments(int totalActivityBeyondGoal, int totalMinTarget, int totalActivityDurationMinutes) {
        mTotalActivityBeyondGoal = totalActivityBeyondGoal;
        mTotalActivityDurationMin = totalActivityDurationMinutes;
        mTotalMinTarget = totalMinTarget;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int fullWidth = getWidth();
        float height = scaleFactor * GraphUtils.HEIGHT_BAR;
        float txtHeightMarginTop = scaleFactor * GraphUtils.MARGIN_TOP;

        //using mDifference to check wheather its beyond time or not
        mDifference = mTotalMinTarget - mTotalActivityDurationMin;

        //if beyond time then its start value should be difference value else its zero(0)
        if (mDifference < 0) {
            txtStartValue = mDifference;
        } else {
            txtStartValue = 0;
        }

        //end point should be total minutes of goal
        txtEndValue = mTotalMinTarget;

        //goint to divide into equal part of width
        if (mTotalMinTarget > 0 && !(mDifference < 0)) {
            mVolume = (float) fullWidth / mTotalMinTarget;
        } else {
            mVolume = (float) fullWidth / mTotalActivityDurationMin;
        }

        if (mDifference < 0) {
            mFillEndRange = mVolume * mTotalActivityBeyondGoal;
        } else {
            mFillEndRange = mDifference * mVolume;
        }


        float xStartPoint = 0, yStartPoint = 0;

        float xEndPoint = fullWidth;
        float yEndPoint = yStartPoint + height;

        //Drawing main Rectangle of Grey
        RectF myRectum = new RectF(xStartPoint, yStartPoint, xEndPoint, yEndPoint);
        canvas.drawRect(myRectum, linePaint);

        float txtHeight = yEndPoint + txtHeightMarginTop;


        Typeface timeFrameTypeFace = Typeface.createFromAsset(mContext.getAssets(), "fonts/" + "roboto-regular.ttf");
        Paint mTextPaint = new Paint();
        mTextPaint.setColor(GraphUtils.COLOR_TEXT);
        mTextPaint.setTextSize(scaleFactor * GraphUtils.TEXT_SIZE);
        mTextPaint.setStrokeWidth(8);
        mTextPaint.setTypeface(timeFrameTypeFace);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mTextPaint.setLetterSpacing(GraphUtils.LETTER_SPACING);
        }

        canvas.drawText(String.valueOf((int) txtStartValue), xStartPoint, txtHeight, mTextPaint);

        String textlenth = String.valueOf(txtEndValue);
        int useItemCount = textlenth.length();
        canvas.drawText(String.valueOf((int) txtEndValue), xEndPoint - ((getWidthOfText(String.valueOf(txtEndValue), mTextPaint)) - ((useItemCount + 2) * scaleFactor)), txtHeight, mTextPaint);

        //Filling usage of time
        Paint mDrawRange = new Paint();
        float fillStartPoint;
        if (mDifference < 0) {
            mDrawRange.setColor(GraphUtils.COLOR_PINK);
            canvas.drawText(String.valueOf(0), mFillEndRange - getWidthOfText("0", mTextPaint), txtHeight, mTextPaint);
        } else {
            mDrawRange.setColor(GraphUtils.COLOR_GREEN);
        }

        RectF rectFill = new RectF(xStartPoint, yStartPoint, mFillEndRange, yEndPoint);
        canvas.drawRect(rectFill, mDrawRange);

    }

    private float getWidthOfText(String text, Paint paint) {
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.width();
    }
}
