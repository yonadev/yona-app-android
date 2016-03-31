/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class YonaFontButton extends Button {

    public YonaFontButton(Context context) {
        super(context);

        YonaFontUtils.applyCustomFont(this, context, null);
    }

    public YonaFontButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        YonaFontUtils.applyCustomFont(this, context, attrs);
    }

    public YonaFontButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        YonaFontUtils.applyCustomFont(this, context, attrs);
    }
}