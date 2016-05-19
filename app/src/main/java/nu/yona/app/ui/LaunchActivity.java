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

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.customview.CustomAlertDialog;
import nu.yona.app.ui.login.LoginActivity;
import nu.yona.app.ui.pincode.PasscodeActivity;
import nu.yona.app.ui.signup.OTPActivity;
import nu.yona.app.ui.signup.SignupActivity;
import nu.yona.app.ui.tour.YonaCarrouselActivity;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.PreferenceConstant;

/**
 * Created by kinnarvasa on 25/03/16.
 */
public class LaunchActivity extends BaseActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch_layout);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        if (!YonaApplication.getUserPreferences().getBoolean(PreferenceConstant.STEP_TOUR, false)) {
            startNewActivity(YonaCarrouselActivity.class);
        } else if (!YonaApplication.getUserPreferences().getBoolean(PreferenceConstant.STEP_REGISTER, false)) {
            // continue on same page. We need to keep on this position, so this step don't get ignore.
        } else if (YonaApplication.getUserPreferences().getBoolean(PreferenceConstant.STEP_REGISTER, false)
                && !YonaApplication.getUserPreferences().getBoolean(PreferenceConstant.STEP_OTP, false)) {
            startNewActivity(OTPActivity.class);
        } else if (!YonaApplication.getUserPreferences().getBoolean(PreferenceConstant.STEP_PASSCODE, false)) {
            Bundle bundle = new Bundle();
            bundle.putInt(AppConstant.TITLE_BACKGROUND_RESOURCE, R.drawable.triangle_shadow_grape);
            bundle.putInt(AppConstant.COLOR_CODE, ContextCompat.getColor(this, R.color.grape));
            startNewActivity(bundle, PasscodeActivity.class);
        } else if (!TextUtils.isEmpty(YonaApplication.getUserPreferences().getString(PreferenceConstant.YONA_PASSCODE, ""))) {
            startNewActivity(YonaActivity.class);
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

        findViewById(R.id.environmentSwitch).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (getResources().getBoolean(R.bool.allowEnvironmentSwitch)) {
                    switchEnvironment();
                }
                return true;
            }
        });
    }

    /**
     * This method is just for testing purpose on different environment.
     */
    private void switchEnvironment() {

        int selectedEnvironment = 0;
        for (int i = 0; i < AppConstant.environmentList.length; i++) {
            if (AppConstant.environemntPath[i].toString().equalsIgnoreCase(YonaApplication.getServerUrl())) {
                selectedEnvironment = i;
                break;
            }
        }
        CustomAlertDialog.show(this, getString(R.string.choose_environment), AppConstant.environmentList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                YonaApplication.setServerUrl(AppConstant.environemntPath[which].toString());
                Toast.makeText(LaunchActivity.this, "You are now in :" + AppConstant.environmentList[which].toString(), Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        }, selectedEnvironment);
    }

    private void startNewActivity(Class mClass) {
        startNewActivity(null, mClass);
    }

    private void startNewActivity(Bundle bundle, Class mClass) {
        Intent intent = new Intent(LaunchActivity.this, mClass);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        finish();
    }

}
