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
import android.util.Log;
import android.widget.TextView;

import nu.yona.app.R;


public class YonaFontUtils {

    public static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";

    public static void applyCustomFont(TextView customFontTextView, Context context, AttributeSet attrs) {
        TypedArray attributeArray = context.obtainStyledAttributes(
                attrs, R.styleable.YonaFontTextView);

        String fontName = attributeArray.getString(R.styleable.YonaFontTextView_font);

        // check if a special textStyle was used (e.g. extra bold)
        int textStyle = attributeArray.getInt(R.styleable.YonaFontTextView_textStyle, 0);

        Log.i(YonaFontUtils.class.getName(), "textStyle..." + textStyle);
        // if nothing extra was used, fall back to regular android:textStyle parameter
        if (textStyle == 0) {
            textStyle = attrs.getAttributeIntValue(ANDROID_SCHEMA, "textStyle", Typeface.NORMAL);
            Log.i(YonaFontUtils.class.getName(), "textStyle..2.." + textStyle);
        }

        Typeface customFont = selectTypeface(context, textStyle);
        customFontTextView.setTypeface(customFont);

        attributeArray.recycle();
    }

    private static Typeface selectTypeface(Context context, int textStyle) {
            /*
            information about the TextView textStyle:
            http://developer.android.com/reference/android/R.styleable.html#TextView_textStyle
            */
        Log.i(YonaFontUtils.class.getName(), "TypeFace bold.." + Typeface.BOLD);
        Log.i(YonaFontUtils.class.getName(), "TypeFace normal.." + Typeface.NORMAL);
        switch (textStyle) {
            case Typeface.BOLD: // bold
                Log.i(YonaFontUtils.class.getName(), "Bold selected");
                return FontCache.getTypeface("roboto-bold.ttf", context);

            case 10: // extra light, equals @integer/font_style_extra_light
                Log.i(YonaFontUtils.class.getName(), "light roboto selected");
                return FontCache.getTypeface("roboto-light.ttf", context);

            case Typeface.NORMAL: // regular
            default:
                Log.i(YonaFontUtils.class.getName(), "Regular selected");
                return FontCache.getTypeface("roboto-Regular.ttf", context);
        }
    }
}