/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.ui.signup;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.RegisterUser;
import nu.yona.app.enums.IntentEnum;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.ui.pincode.BasePasscodeActivity;
import nu.yona.app.ui.pincode.PasscodeActivity;
import nu.yona.app.ui.pincode.PasscodeFragment;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.AppUtils;
import nu.yona.app.utils.PreferenceConstant;

/**
 * Created by kinnarvasa on 04/04/16.
 */
public class OTPActivity extends BasePasscodeActivity implements EventChangeListener {

    private PasscodeFragment otpFragment;
    private RegisterUser user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        YonaApplication.getEventChangeManager().registerListener(this);
        loadOTPFragment();
        screenTitle = getString(R.string.join);
        screen_type = AppConstant.OTP;
        if (getIntent() != null && getIntent().getExtras() != null) {
            user = (RegisterUser) getIntent().getExtras().getSerializable(AppConstant.USER);
        }
        updateScreenUI();
    }

    private void loadOTPFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.blank_container, getOTPFragment());
        fragmentTransaction.commit();
    }

    private PasscodeFragment getOTPFragment() {
        if (otpFragment == null) {
            Bundle bPasscode = new Bundle();
            bPasscode.putInt(AppConstant.COLOR_CODE, ContextCompat.getColor(this, R.color.grape));
            bPasscode.putString(AppConstant.SCREEN_TYPE, AppConstant.OTP);
            otpFragment = new PasscodeFragment();
            otpFragment.setArguments(bPasscode);
        }
        return otpFragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        YonaApplication.getEventChangeManager().unRegisterListener(this);
    }

    @Override
    public void onStateChange(int eventType, Object object) {
        switch (eventType) {
            case EventChangeManager.EVENT_PASSCODE_STEP_TWO:
                validateOTP(object.toString());
                break;
            case EventChangeManager.EVENT_OTP_RESEND:
                resendOTP();
                break;
            case EventChangeManager.EVENT_CLOSE_ALL_ACTIVITY_EXCEPT_LAUNCH:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * @param otpString User's entered OTP
     */
    private void validateOTP(final String otpString) {
        showLoadingView(true, null);
        APIManager.getInstance().getAuthenticateManager().verifyOTP(user, otpString, new DataLoadListener() {
            @Override
            public void onDataLoad(Object result) {
                AppUtils.downloadCertificates();
                getActivityCategories();
                showLoadingView(false, null);
                if (YonaApplication.getEventChangeManager().getSharedPreference().getUserPreferences().getBoolean(PreferenceConstant.PROFILE_OTP_STEP, false)) {
                    YonaApplication.getEventChangeManager().getSharedPreference().getUserPreferences().edit().putBoolean(PreferenceConstant.STEP_OTP, true).apply();
                    showProfileScreen();
                } else {
                    showPasscodeScreen();
                }
            }

            @Override
            public void onError(Object errorMessage) {
                if (errorMessage instanceof ErrorMessage) {
                    showLoadingView(false, null);
                    Snackbar.make(findViewById(android.R.id.content), ((ErrorMessage) errorMessage).getMessage(), Snackbar.LENGTH_INDEFINITE)
                            .setAction(getString(R.string.ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    otpFragment.resetDigit();
                                }
                            })
                            .show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /*
         * Get all activity categories
         */
    private void getActivityCategories() {
        APIManager.getInstance().getActivityCategoryManager().getActivityCategoriesById(null);
    }

    private void resendOTP() {
        showLoadingView(true, null);
        otpFragment.resetDigit();
        APIManager.getInstance().getAuthenticateManager().resendOTP(new DataLoadListener() {
            @Override
            public void onDataLoad(Object result) {
                showLoadingView(false, null);
            }

            @Override
            public void onError(Object errorMessage) {
                showLoadingView(false, null);
            }
        });
    }

    private void showPasscodeScreen() {
        startActivity(new Intent(OTPActivity.this, PasscodeActivity.class));
        finish();
    }

    private void showProfileScreen() {
        Intent intent = new Intent(OTPActivity.this, YonaActivity.class);
        intent.setAction(IntentEnum.ACTION_PROFILE.getActionString());
        intent.putExtra(AppConstant.FROM_LOGIN, true);
        ActivityCompat.startActivity(this, intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
        finish();
    }

}
