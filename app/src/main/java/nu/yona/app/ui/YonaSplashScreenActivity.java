/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.ui.pincode.PasscodeActivity;
import nu.yona.app.ui.signup.OTPActivity;
import nu.yona.app.ui.tour.YonaCarrouselActivity;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.PreferenceConstant;

/**
 * Created by bhargavsuthar on 19/05/16.
 */
public class YonaSplashScreenActivity extends BaseActivity {

    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (!YonaApplication.getUserPreferences().getBoolean(PreferenceConstant.STEP_TOUR, false)) {
                    startNewActivity(YonaCarrouselActivity.class);
                } else if (!YonaApplication.getUserPreferences().getBoolean(PreferenceConstant.STEP_REGISTER, false)) {
                    startNewActivity(LaunchActivity.class);
                } else if (YonaApplication.getUserPreferences().getBoolean(PreferenceConstant.STEP_REGISTER, false)
                        && !YonaApplication.getUserPreferences().getBoolean(PreferenceConstant.STEP_OTP, false)) {
                    startNewActivity(OTPActivity.class);
                } else if (!YonaApplication.getUserPreferences().getBoolean(PreferenceConstant.STEP_PASSCODE, false)) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(AppConstant.TITLE_BACKGROUND_RESOURCE, R.drawable.triangle_shadow_grape);
                    bundle.putInt(AppConstant.COLOR_CODE, ContextCompat.getColor(YonaSplashScreenActivity.this, R.color.grape));
                    startNewActivity(bundle, PasscodeActivity.class);
                } else if (!TextUtils.isEmpty(YonaApplication.getUserPreferences().getString(PreferenceConstant.YONA_PASSCODE, ""))) {
                    startNewActivity(YonaActivity.class);
                }
            }
        }, SPLASH_TIME_OUT);
    }


}
