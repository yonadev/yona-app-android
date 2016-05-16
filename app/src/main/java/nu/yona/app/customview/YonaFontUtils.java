/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.LruCache;
import android.widget.TextView;

import nu.yona.app.R;
import nu.yona.app.utils.AppUtils;


/**
 * The type Yona font utils.
 */
class YonaFontUtils {

    //font Style TypeFace
    private static LruCache<String, Typeface> fontCache;

    /**
     * Apply custom font.
     *
     * @param customFontTextView the custom font text view
     * @param context            the context
     * @param attrs              the attrs
     */
    public static void applyCustomFont(TextView customFontTextView, Context context, AttributeSet attrs) {
        TypedArray attributeArray = context.obtainStyledAttributes(
                attrs, R.styleable.YonaFontTextView);

        // check if a special textStyle was used (e.g. extra bold)
        int textStyle = attributeArray.getInt(R.styleable.YonaFontTextView_textStyle, 0);

        Typeface customFont = selectTypeface(context, textStyle);
        customFontTextView.setTypeface(customFont);

        attributeArray.recycle();
    }

    private static Typeface selectTypeface(Context context, int textStyle) {
            /*
            information about the TextView textStyle:
            http://developer.android.com/reference/android/R.styleable.html#TextView_textStyle
            */
        switch (textStyle) {
            case R.integer.roboto_light: // extra light, equals @integer/font_style_extra_light
                return getTypeface("roboto-light.ttf", context);

            case R.integer.roboto_medium:
                return getTypeface("roboto-medium.ttf", context);

            case R.integer.roboto_bold: // bold
                return getTypeface("roboto-bold.ttf", context);

            case R.integer.oswald_light:
                return getTypeface("oswald-light.ttf", context);

            case R.integer.roboto_normal: // regular
            default:
                return getTypeface("roboto-regular.ttf", context);
        }
    }

    private static Typeface getTypeface(String fontname, Context context) {
        if (fontCache == null) {
            fontCache = new LruCache<>(context.getResources().getInteger(R.integer.total_font_count));
        }
        Typeface typeface = fontCache.get(fontname);

        if (typeface == null) {
            try {
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + fontname);
            } catch (Exception e) {
                AppUtils.throwException(YonaFontUtils.class.getSimpleName(), e, Thread.currentThread(), null);
                return null;
            }

            fontCache.put(fontname, typeface);
        }

        return typeface;
    }
}