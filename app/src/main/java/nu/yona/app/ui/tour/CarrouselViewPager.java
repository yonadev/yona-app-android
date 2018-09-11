/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.tour;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Interpolator;

import java.lang.reflect.Field;

import nu.yona.app.utils.AppUtils;

/**
 * Created by bhargavsuthar on 19/05/16.
 */
public class CarrouselViewPager extends ViewPager {

    private CarrouselScroller mScroller = null;
    private float mStartDragX;
    private OnSwipeOutListener mListener;

    /**
     * Instantiates a new Carrousel view pager.
     *
     * @param context the context
     */
    public CarrouselViewPager(Context context) {
        super(context);
        postInitViewPager();
    }

    /**
     * Instantiates a new Carrousel view pager.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public CarrouselViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        postInitViewPager();
    }

    /**
     * Override the Scroller instance with our own class so we can change the
     * duration
     */
    private void postInitViewPager() {
        try {
            Field scroller = ViewPager.class.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            Field interpolator = ViewPager.class.getDeclaredField("sInterpolator");
            interpolator.setAccessible(true);

            mScroller = new CarrouselScroller(getContext(),
                    (Interpolator) interpolator.get(null));
            scroller.set(this, mScroller);
        } catch (Exception e) {
            AppUtils.reportException(CarrouselViewPager.class.getSimpleName(), e, Thread.currentThread());
        }
    }

    public void setOnSwipeOutListener(OnSwipeOutListener listener) {
        mListener = listener;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartDragX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mStartDragX < x) {
                    mListener.onSwipeOutAtStart();
                } else if (mStartDragX > x) {
                    mListener.onSwipeOutAtEnd();
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public interface OnSwipeOutListener {
        public void onSwipeOutAtStart();

        public void onSwipeOutAtEnd();
    }

}