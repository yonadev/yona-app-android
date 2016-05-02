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
import android.widget.TextView;

import java.util.HashMap;

import nu.yona.app.R;
import nu.yona.app.utils.AppUtils;


/**
 * The type Yona font utils.
 */
class YonaFontUtils {

    /**
     * The constant ANDROID_SCHEMA.
     */
    public static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";
    //font Style TypeFace
    private static final int ROBOTO_LIGHT = 10;
    private static final int ROBOTO_MEDIUM = 11;
    private static final int ROBOTO_BOLD = 12;
    private static final int ROBOTO_NORMAL = 13;
    private static final int OSWALD_LIGHT = 14;
    private static final HashMap<String, Typeface> fontCache = new HashMap<>();

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
            case ROBOTO_LIGHT: // extra light, equals @integer/font_style_extra_light
                return getTypeface("roboto-light.ttf", context);

            case ROBOTO_MEDIUM:
                return getTypeface("roboto-medium.ttf", context);

            case ROBOTO_BOLD: // bold
                return getTypeface("roboto-bold.ttf", context);

            case OSWALD_LIGHT:
                return getTypeface("oswald-light.ttf", context);

            case ROBOTO_NORMAL: // regular
            default:
                return getTypeface("roboto-regular.ttf", context);
        }
    }

    private static Typeface getTypeface(String fontname, Context context) {
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