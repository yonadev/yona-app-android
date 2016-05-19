/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.tour;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * Created by bhargavsuthar on 19/05/16.
 */
public class CarrouselScroller extends Scroller {

    private double mScrollFactor = 1;
    private int mDuration = 5000;

    public CarrouselScroller(Context context) {
        super(context);
    }

    public CarrouselScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public CarrouselScroller(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
    }

    /**
     * Set the Factor duration
     */
    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, (int) (mDuration * mScrollFactor));
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, (int) (mDuration * mScrollFactor));
    }
}


