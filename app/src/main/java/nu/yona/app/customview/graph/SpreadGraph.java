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
import android.graphics.Typeface;
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
    private final int mMinPerParts = 15;
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
        float height = scaleFactor * 2;

        float heightOfbar = GraphUtils.convertSizeToDeviceDependent(mContext, graphHeight);
        //first bar
        float mXStart = 0, mYStart = heightOfbar; // basically (X1, Y1)

        float right = fullWidth; // width (distance from X1 to X2)
        float bottom = heightOfbar; // height (distance from Y1 to Y2)

        mStartPoint = 0;
        mMiddlePoint = (fullWidth / 2);

        float spreadtime = fullWidth;

        float mPartSize = spreadtime / mNoParts;

        //todraw text from height
        float heightDraw = bottom + (20 * scaleFactor);

        //draw graphics of sun and moon
        Bitmap moonBitmap = drawableToBitmap(ContextCompat.getDrawable(mContext, R.drawable.icon_moon));
        float bitmapWidth = moonBitmap.getWidth() / 2;
        mCanvas.drawBitmap(moonBitmap, mStartPoint, bottom, null);
        mCanvas.drawBitmap(drawableToBitmap(ContextCompat.getDrawable(mContext, R.drawable.icn_sun)), mMiddlePoint - bitmapWidth, bottom, null);


        Typeface timeFrameTypeFace = Typeface.createFromAsset(mContext.getAssets(), "fonts/" + "roboto-regular.ttf");
        Paint mTextPaint = new Paint();
        mTextPaint.setColor(GraphUtils.COLOR_BULLET_DOT);
        mTextPaint.setTextSize(scaleFactor * 14);
        mTextPaint.setStrokeWidth(8);
        mTextPaint.setTypeface(timeFrameTypeFace);

        float textPoint = (mMiddlePoint / 2) / 2;
        mCanvas.drawText(mContext.getString(R.string.four_hours), textPoint, heightDraw, mTextPaint);
        float textPoint2 = textPoint * 2 + ((textPoint / 2));
        mCanvas.drawText(mContext.getString(R.string.eight_hours), textPoint2, heightDraw, mTextPaint);
        float textPoint3 = textPoint * 5;
        mCanvas.drawText(mContext.getString(R.string.sixteen_hours), textPoint3 - bitmapWidth, heightDraw, mTextPaint);
        float textPoint4 = textPoint * 6 + ((textPoint / 2));
        mCanvas.drawText(mContext.getString(R.string.twenty_hours), textPoint4 - bitmapWidth, heightDraw, mTextPaint);
        float textPoint5 = textPoint * 7 + ((textPoint / 2));
        mCanvas.drawBitmap(drawableToBitmap(ContextCompat.getDrawable(mContext, R.drawable.icon_moon)), textPoint5, bottom, null);

        if (mListZoneSpread != null && mListZoneSpread.size() > 0) {
            float currentStartPos;
            float currentEndPos;
            Paint barGraphPaint = new Paint();
            barGraphPaint.setStyle(Paint.Style.STROKE);
            barGraphPaint.setStrokeWidth(5);
            for (TimeZoneSpread timeZoneSpread : mListZoneSpread) {
                currentStartPos = (float) timeZoneSpread.getIndex() * mPartSize;
                Path barPath = new Path();
                if (timeZoneSpread.getColor() == GraphUtils.COLOR_PINK || timeZoneSpread.getColor() == GraphUtils.COLOR_BLUE) {
                    currentEndPos = timeZoneSpread.getUsedValue();
                    barGraphPaint.setColor(timeZoneSpread.getColor());
                } else if (timeZoneSpread.getColor() == GraphUtils.COLOR_GREEN) {
                    currentEndPos = startEndPoint;
                    barGraphPaint.setColor(GraphUtils.COLOR_BULLET_DOT);
                } else {
                    currentEndPos = startEndPoint;
                    barGraphPaint.setColor(timeZoneSpread.getColor());
                }
                float newXPos = mXStart + currentStartPos;
                barPath.moveTo(newXPos, mYStart);
                float noPartsHeight = heightOfbar / 15;
                barPath.lineTo(currentStartPos, mYStart - (currentEndPos * noPartsHeight));
                canvas.drawPath(barPath, barGraphPaint);
            }

        }

    }
}
