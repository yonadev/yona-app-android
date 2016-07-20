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
    private Bundle bundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch_layout);
        bundle = new Bundle();
        if (getIntent() != null) {
            if (getIntent().getDataString() != null) {
                bundle.putString(AppConstant.URL, getIntent().getDataString());
            } else if (getIntent().getExtras() != null) {
                bundle = getIntent().getExtras();
            }
        }
        if (!YonaApplication.getEventChangeManager().getSharedPreference().getUserPreferences().getBoolean(PreferenceConstant.STEP_TOUR, false)) {
            startNewActivity(bundle, YonaCarrouselActivity.class);
        } else if (!YonaApplication.getEventChangeManager().getSharedPreference().getUserPreferences().getBoolean(PreferenceConstant.STEP_REGISTER, false)) {
            // We will skip here to load same activity
        } else if (YonaApplication.getEventChangeManager().getSharedPreference().getUserPreferences().getBoolean(PreferenceConstant.STEP_REGISTER, false)
                && !YonaApplication.getEventChangeManager().getSharedPreference().getUserPreferences().getBoolean(PreferenceConstant.STEP_OTP, false)) {
            startNewActivity(bundle, OTPActivity.class);
        } else if (!YonaApplication.getEventChangeManager().getSharedPreference().getUserPreferences().getBoolean(PreferenceConstant.STEP_PASSCODE, false)) {
            bundle.putInt(AppConstant.TITLE_BACKGROUND_RESOURCE, R.drawable.triangle_shadow_grape);
            bundle.putInt(AppConstant.COLOR_CODE, ContextCompat.getColor(LaunchActivity.this, R.color.grape));
            startNewActivity(bundle, PasscodeActivity.class);
        } else if (!TextUtils.isEmpty(YonaApplication.getEventChangeManager().getSharedPreference().getUserPreferences().getString(PreferenceConstant.YONA_PASSCODE, ""))) {
            startNewActivity(bundle, YonaActivity.class);
        }

        findViewById(R.id.join).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(bundle, SignupActivity.class);
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
            if (AppConstant.environemntPath[i].toString().equalsIgnoreCase(YonaApplication.getEventChangeManager().getDataState().getServerUrl())) {
                selectedEnvironment = i;
                break;
            }
        }
        CustomAlertDialog.show(this, getString(R.string.choose_environment), AppConstant.environmentList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                YonaApplication.getEventChangeManager().getDataState().setServerUrl(AppConstant.environemntPath[which].toString());
                Toast.makeText(LaunchActivity.this, "You are now in :" + AppConstant.environmentList[which].toString(), Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        }, selectedEnvironment);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
