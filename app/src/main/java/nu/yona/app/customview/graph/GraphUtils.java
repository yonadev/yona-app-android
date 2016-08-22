/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.customview.graph;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by bhargavsuthar on 07/06/16.
 */
public class GraphUtils {

    /**
     * The constant HEIGHT_BAR.
     */
    public static int HEIGHT_BAR = 20;
    /**
     * The constant MARGIN_TOP.
     */
    public static int MARGIN_TOP = 20;
    /**
     * The constant COLOR_PINK.
     */
    public static int COLOR_PINK = 0xffe8308a;
    /**
     * The constant COLOR_BLUE.
     */
    public static int COLOR_BLUE = 0xff1d71b8;
    /**
     * The constant COLOR_GREEN.
     */
    public static int COLOR_GREEN = 0xff8ab518;
    /**
     * The constant COLOR_BACKGROUD_GRAPH.
     */
    public static int COLOR_BACKGROUD_GRAPH = 0xfff7f7f7;
    /**
     * The constant COLOR_WHITE_THREE.
     */
    public static int COLOR_WHITE_THREE = 0xffe7e7e7;
    /**
     * The constant COLOR_WHITE_THREE_TWO.
     */
    public static int COLOR_WHITE_THREE_TWO = 0x80ffffff;
    /**
     * The constant COLOR_BULLET_DOT.
     */
    public static int COLOR_BULLET_DOT = 0xffd5d5d5;
    /**
     * The constant COLOR_BULLET_LIGHT_DOT.
     */
    public static int COLOR_BULLET_LIGHT_DOT = 0xfff3f3f3;
    /**
     * The constant COLOR_TEXT.
     */
    public static int COLOR_TEXT = 0x802f2f2f;
    /**
     * The constant TEXT_SIZE.
     */
    public static int TEXT_SIZE = 12;
    /**
     * The constant LETTER_SPACING.
     */
    public static float LETTER_SPACING = 0.01f;

    /**
     * Get the size of pixels depend on device
     *
     * @param context the context
     * @param value   the value
     * @return int int
     */
    public static int convertSizeToDeviceDependent(Context context, int value) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return ((dm.densityDpi * value) / 160);
    }
}
