/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

import nu.yona.app.R;
import nu.yona.app.utils.AppUtils;

public class ViewPagerIndicator extends View {
    private static final float SPACING = 2.0f;
    private final Paint backPaint = new Paint();
    private final Paint frontPaint = new Paint();
    private int scrollPosition;
    private float currentScroll;
    private float radius;
    private int count;

    public ViewPagerIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ViewPagerIndicator(Context context) {
        super(context);
        init();
    }

    public void setCount(int count) {
        this.count = count;
        invalidate();
    }

    private void init() {
        backPaint.setColor(0xffdddddd);
        backPaint.setStyle(Style.FILL);
        backPaint.setAntiAlias(true);
        frontPaint.setStyle(Style.FILL);
        frontPaint.setAntiAlias(true);
        radius = AppUtils.getDp(getContext(), 4);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (count <= 1) {
            return;
        }

        float leftOffset = getWidth() / 2f - (count - 1) * radius * SPACING;
        float topOffset = getHeight() / 2f;

        for (int i = 0; i < count; i++) {
            canvas.drawCircle(leftOffset + i * radius * SPACING * 2, topOffset, radius, backPaint);
        }

        canvas.drawCircle(leftOffset + (scrollPosition + currentScroll) * radius * SPACING * 2, topOffset, radius, frontPaint);
    }

    public void onScrolled(int position, float positionOffset) {
        scrollPosition = position;
        currentScroll = positionOffset;
        if (scrollPosition == 0) {
            frontPaint.setColor(getResources().getColor(R.color.darkish_pink));
        } else if (scrollPosition == 1) {
            frontPaint.setColor(getResources().getColor(R.color.pea));
        } else if (scrollPosition == 2) {
            frontPaint.setColor(getResources().getColor(R.color.mid_blue));
        } else {
            frontPaint.setColor(getResources().getColor(R.color.mango));
        }
        invalidate();
    }
}
