/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.customview.graph;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

import nu.yona.app.R;
import nu.yona.app.api.model.TimeZoneSpread;

/**
 * Created by bhargavsuthar on 08/06/16.
 */
public class SpreadGraph extends BaseView {

    private final int mNoParts = 96;
    private final float startEndPoint = 0.3f;
    private final int graphHeight = 60;
    private List<TimeZoneSpread> mListZoneSpread;
    private Canvas mCanvas;
    private float mStartPoint;
    private float mMiddlePoint;

    /**
     * Instantiates a new Spread Graph graph.
     *
     * @param context the context
     */
    public SpreadGraph(Context context) {
        super(context);
        init();
    }

    /**
     * Instantiates a new Spread Graph graph.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public SpreadGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Instantiates a new Spread Graph graph.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     */
    public SpreadGraph(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Instantiates a new Spread Graph graph.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     * @param defStyleRes  the def style res
     */
    public SpreadGraph(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mListZoneSpread = new ArrayList<TimeZoneSpread>();
    }

    /**
     * Chart value pre.
     *
     * @param mListZoneSpread the m list zone spread
     */
    public void chartValuePre(List<TimeZoneSpread> mListZoneSpread) {
        this.mListZoneSpread = mListZoneSpread;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.mCanvas = canvas;
        float fullWidth = canvas.getWidth();

        float heightOfbar = GraphUtils.convertSizeToDeviceDependent(mContext, graphHeight);
        //first bar
        float mXStart = 0, mYStart = heightOfbar; // basically (X1, Y1)

        float bottom = heightOfbar; // height (distance from Y1 to Y2)

        mStartPoint = 0;
        mMiddlePoint = (fullWidth / 2);

        float spreadtime = fullWidth;

        float mPartSize = spreadtime / mNoParts;

        //todraw text from height
        float heightDraw = bottom + (GraphUtils.MARGIN_TOP * scaleFactor);

        //draw graphics of sun and moon
        Bitmap moonBitmap = drawableToBitmap(ContextCompat.getDrawable(mContext, R.drawable.icon_moon));
        float bitmapWidth = moonBitmap.getWidth() / 2;
        mCanvas.drawBitmap(moonBitmap, mStartPoint - (5 * scaleFactor), bottom + (5 * scaleFactor), null);
        mCanvas.drawBitmap(drawableToBitmap(ContextCompat.getDrawable(mContext, R.drawable.icn_sun)), mMiddlePoint - bitmapWidth, bottom + (5 * scaleFactor), null);


        float textPoint = (mMiddlePoint / 2) / 2;
        mCanvas.drawText(mContext.getString(R.string.four_hours), textPoint, heightDraw + scaleFactor, getFontStyle());
        float textPoint2 = textPoint * 2 + ((textPoint / 2));
        mCanvas.drawText(mContext.getString(R.string.eight_hours), textPoint2, heightDraw + scaleFactor, getFontStyle());
        float textPoint3 = textPoint * 5;
        mCanvas.drawText(mContext.getString(R.string.sixteen_hours), textPoint3 - bitmapWidth, heightDraw + scaleFactor, getFontStyle());
        float textPoint4 = textPoint * 6 + ((textPoint / 2));
        mCanvas.drawText(mContext.getString(R.string.twenty_hours), textPoint4 - bitmapWidth, heightDraw + scaleFactor, getFontStyle());
        float textPoint5 = textPoint * 7 + ((textPoint / 2));
        mCanvas.drawBitmap(drawableToBitmap(ContextCompat.getDrawable(mContext, R.drawable.icon_moon)), textPoint5, bottom + (5 * scaleFactor), null);

        if (mListZoneSpread != null && mListZoneSpread.size() > 0) {
            float currentStartPos;
            float currentEndPos;
            Paint barGraphPaint = new Paint();
            barGraphPaint.setStyle(Paint.Style.STROKE);
            barGraphPaint.setStrokeWidth(5);
            boolean skipThis;
            for (TimeZoneSpread timeZoneSpread : mListZoneSpread) {
                skipThis = false;
                currentStartPos = (float) timeZoneSpread.getIndex() * mPartSize;
                Path barPath = new Path();
                if (timeZoneSpread.getColor() == GraphUtils.COLOR_PINK || timeZoneSpread.getColor() == GraphUtils.COLOR_BLUE) {
                    currentEndPos = timeZoneSpread.getUsedValue();
                    barGraphPaint.setColor(timeZoneSpread.getColor());
                } else if (timeZoneSpread.getColor() == GraphUtils.COLOR_GREEN) {
                    if (timeZoneSpread.getUsedValue() == 15) {
                        currentEndPos = startEndPoint;
                        barGraphPaint.setColor(GraphUtils.COLOR_BULLET_DOT);
                    } else {
                        currentEndPos = startEndPoint;
                        barGraphPaint.setColor(GraphUtils.COLOR_BLUE);
                    }
                } else if (timeZoneSpread.getUsedValue() != 15 && timeZoneSpread.getColor() == GraphUtils.COLOR_BULLET_LIGHT_DOT) {
                    currentEndPos = startEndPoint;
                    barGraphPaint.setColor(timeZoneSpread.getColor());
                    skipThis = true;
                } else {
                    currentEndPos = startEndPoint;
                    barGraphPaint.setColor(timeZoneSpread.getColor());
                }
                if (!skipThis) {
                    float newXPos = mXStart + currentStartPos;
                    barPath.moveTo(newXPos + 2, mYStart);
                    float noPartsHeight = heightOfbar / 15;
                    barPath.lineTo(currentStartPos + 2, mYStart - (currentEndPos * noPartsHeight) - 1);
                    canvas.drawPath(barPath, barGraphPaint);
                }
            }

        }

    }
}
