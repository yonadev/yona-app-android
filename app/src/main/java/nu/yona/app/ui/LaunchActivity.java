/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.ui.login.LoginActivity;
import nu.yona.app.ui.pincode.PasscodeActivity;
import nu.yona.app.ui.pincode.PinActivity;
import nu.yona.app.ui.signup.OTPActivity;
import nu.yona.app.ui.signup.SignupActivity;
import nu.yona.app.ui.tour.TourActivity;
import nu.yona.app.utils.PreferenceConstant;

/**
 * Created by kinnarvasa on 25/03/16.
 */
public class LaunchActivity extends BaseActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch_layout);
        if (!YonaApplication.getUserPreferences().getBoolean(PreferenceConstant.STEP_TOUR, false)) {
            startNewActivity(TourActivity.class);
        } else if (!YonaApplication.getUserPreferences().getBoolean(PreferenceConstant.STEP_REGISTER, false)) {
            // continue on same page.
        } else if (!YonaApplication.getUserPreferences().getBoolean(PreferenceConstant.STEP_OTP, false)
                || TextUtils.isEmpty(YonaApplication.getAppContext().getUserPreferences().getString(PreferenceConstant.YONA_PASSCODE, ""))) {
            startNewActivity(OTPActivity.class);
        } else if (!YonaApplication.getUserPreferences().getBoolean(PreferenceConstant.STEP_PASSCODE, false)) {
            startNewActivity(PasscodeActivity.class);
        } else if (!TextUtils.isEmpty(YonaApplication.getUserPreferences().getString(PreferenceConstant.YONA_PASSCODE, ""))) {
            startNewActivity(PinActivity.class);
        }

        findViewById(R.id.join).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(SignupActivity.class);
            }
        });

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(LoginActivity.class);
            }
        });
    }

    public void startNewActivity(Class mClass) {
        startActivity(new Intent(LaunchActivity.this, mClass));
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        finish();
    }

}
