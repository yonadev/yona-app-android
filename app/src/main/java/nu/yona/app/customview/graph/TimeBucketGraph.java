/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.customview.graph;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

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
    private float animEndPoint;
    private float greenEndPoint;

    private List<Animator> animList;
    private List<Animator> viewAnimList;

    private float xGStartPoint;
    private float xGEndPoint;


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

        animList = new ArrayList<>();
        viewAnimList = new ArrayList<Animator>();
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
        float xGreenEndPoint = fullWidth + height;
        float yEndPoint = yStartPoint + height;

        //Drawing main Rectangle of Grey
        RectF myRectum = new RectF(xStartPoint, yStartPoint, xEndPoint, yEndPoint);
        canvas.drawRect(myRectum, linePaint);

        float txtHeight = yEndPoint + txtHeightMarginTop;

        canvas.drawText(String.valueOf((int) txtStartValue), xStartPoint, txtHeight, getFontStyle());

        String textlenth = String.valueOf(txtEndValue);
        int useItemCount = textlenth.length();
        canvas.drawText(String.valueOf((int) txtEndValue), xEndPoint - ((getWidthOfText(String.valueOf(txtEndValue), getFontStyle())) - ((useItemCount + 5) * scaleFactor)), txtHeight, getFontStyle());

        //Filling usage of time
        Paint mDrawRange = new Paint();
        if (mDifference < 0) {
            mDrawRange.setColor(GraphUtils.COLOR_PINK);
            canvas.drawText(String.valueOf(0), mFillEndRange - getWidthOfText("0", getFontStyle()), txtHeight, getFontStyle());
        } else {
            mDrawRange.setColor(GraphUtils.COLOR_GREEN);
        }

        Paint greenBarPaint = new Paint();
        greenBarPaint.setColor(GraphUtils.COLOR_GREEN);

        if (mDifference < 0) {
            //when there is no over min usage

            //first draw the green bar
            RectF greenRectFill = new RectF(mFillEndRange, xStartPoint, greenEndPoint, yEndPoint);
            canvas.drawRect(greenRectFill, greenBarPaint);
            //second draw the pink bar
            RectF pinkRectFill = new RectF(animEndPoint, yStartPoint, mFillEndRange, yEndPoint);
            canvas.drawRect(pinkRectFill, mDrawRange);
        } else {
            //when full width and mfillEndRange is equl then no animation else do animation
            if (mFillEndRange == getWidth()) {
                RectF rectFill = new RectF(xStartPoint, yStartPoint, mFillEndRange, yEndPoint);
                canvas.drawRect(rectFill, mDrawRange);
            } else {
                RectF rectFill = new RectF(xStartPoint, yStartPoint, animEndPoint, yEndPoint);
                canvas.drawRect(rectFill, mDrawRange);
            }
        }

    }

    public void startAnimation() {
        // now we add them all to the anim list
        AnimatorSet animSet = new AnimatorSet();

        if (mFillEndRange == getWidth()) {
            return;
        }

        Animator animGreen = ObjectAnimator.ofFloat(this, "greenEndPoint", this.getWidth(), mFillEndRange).setDuration(500);
        viewAnimList.add(animGreen);

        if (!(mDifference < 0)) {
            Animator anim = ObjectAnimator.ofFloat(this, "animEndPoint", this.getWidth(), mFillEndRange).setDuration(1000);
            viewAnimList.add(anim);
        } else {
            Animator anim = ObjectAnimator.ofFloat(this, "animEndPoint", mFillEndRange, 0.0f).setDuration(2000);
            viewAnimList.add(anim);
        }

        animSet.playTogether(viewAnimList);
        animList.add(animSet);

        AnimatorSet menuAnimSet = new AnimatorSet();
        menuAnimSet.playSequentially(animList);
        menuAnimSet.start();
    }

    public void setAnimEndPoint(float endPoint) {
        this.animEndPoint = endPoint;
        invalidate();
    }

    public void setGreenEndPoint(float endPoint) {
        this.greenEndPoint = endPoint;
        invalidate();
    }

    private float getWidthOfText(String text, Paint paint) {
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.width();
    }
}
