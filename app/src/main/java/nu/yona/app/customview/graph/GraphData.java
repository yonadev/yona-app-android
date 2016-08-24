/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.customview.graph;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by bhargavsuthar on 23/08/16.
 */
public class GraphData {

    private float startPoint;
    private float endPoint;
    private Paint paint;

    public GraphData(float xpoint, float ypoint, Paint paint) {
        this.startPoint = xpoint;
        this.endPoint = ypoint;
        this.paint = paint;
    }

    public void draw(Canvas canvas, float top, float bottom, float percent) {
        if (paint.getColor() == GraphUtils.COLOR_PINK) {
            float end = startPoint + (endPoint - startPoint) * percent;
            RectF rectFill = new RectF(startPoint, top, end, bottom);
            canvas.drawRect(rectFill, paint);
        } else {
            RectF rectFill = new RectF(startPoint, top, endPoint, bottom);
            canvas.drawRect(rectFill, paint);
        }
    }

    public float getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(float startPoint) {
        this.startPoint = startPoint;
    }

    public float getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(float endPoint) {
        this.endPoint = endPoint;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }
}
