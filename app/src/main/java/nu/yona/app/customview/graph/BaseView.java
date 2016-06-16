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
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by bhargavsuthar on 08/06/16.
 */
public class BaseView extends View {

    /**
     * The M context.
     */
    protected Context mContext;
    /**
     * The Line paint.
     */
    protected Paint linePaint;
    /**
     * The Scale factor.
     */
    protected float scaleFactor;
    /**
     * The M width.
     */
    protected float mWidth;
    /**
     * The M height.
     */
    protected float mHeight;

    /**
     * Instantiates a new Base view.
     *
     * @param context the context
     */
    public BaseView(Context context) {
        super(context);
        mContext = context;
        initialize();
    }

    /**
     * Instantiates a new Base view.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public BaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initialize();
    }

    /**
     * Instantiates a new Base view.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     */
    public BaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initialize();
    }

    /**
     * Instantiates a new Base view.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     * @param defStyleRes  the def style res
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BaseView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        initialize();
    }


    /**
     * Initialize.
     */
    private void initialize() {

        /**
         * to take account of different display densities. All of our drawing is done in pixels,
         * but the different screens on Android devices have different numbers of pixels per inch (ppi).
         * So if we set our text to be 14 pixels high, it will be drawn a different size on different devices.
         * We don't want that, so we increase the text size in proportion to the device's ppi.
         */
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        scaleFactor = metrics.density;
        mWidth = getWidth();
        mHeight = getHeight();

        linePaint = new Paint();
        linePaint.setStrokeWidth(1);
        linePaint.setColor(GraphUtils.COLOR_WHITE_THREE);

    }

    protected int convertSizeToDeviceDependent(int value) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        return ((dm.densityDpi * value) / 160);
    }

    /**
     * Drawable to bitmap bitmap.
     *
     * @param drawable the drawable
     * @return the bitmap
     */
    protected static Bitmap drawableToBitmap(Drawable drawable) {
        return ((BitmapDrawable) drawable).getBitmap();
    }

}
