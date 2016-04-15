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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.ui.login.LoginActivity;
import nu.yona.app.ui.pincode.PasscodeActivity;
import nu.yona.app.ui.pincode.PinActivity;
import nu.yona.app.ui.signup.OTPActivity;
import nu.yona.app.ui.signup.SignupActivity;
import nu.yona.app.utils.PreferenceConstant;

/**
 * Created by kinnarvasa on 25/03/16.
 */
public class LaunchActivity extends BaseActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch_layout);

        if (!YonaApplication.getUserPreferences().getBoolean(PreferenceConstant.STEP_REGISTER, false)) {
            //do nothing
        } else if (!YonaApplication.getUserPreferences().getBoolean(PreferenceConstant.STEP_OTP, false)) {
            startNewActivity(LaunchActivity.this, OTPActivity.class);
        } else if (!YonaApplication.getUserPreferences().getBoolean(PreferenceConstant.STEP_PASSCODE, false)) {
            startNewActivity(LaunchActivity.this, PasscodeActivity.class);
        } else {
            startNewActivity(LaunchActivity.this, PinActivity.class);
        }

        findViewById(R.id.join).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(LaunchActivity.this, SignupActivity.class);
            }
        });

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(LaunchActivity.this, LoginActivity.class);
            }
        });
    }

    public void startNewActivity(Context context, Class mClass) {
        startActivity(new Intent(context, mClass));
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        finish();
    }

}
