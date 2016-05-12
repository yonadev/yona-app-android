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

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.RegisterUser;
import nu.yona.app.customview.CustomAlertDialog;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.BaseActivity;
import nu.yona.app.ui.pincode.PasscodeActivity;
import nu.yona.app.ui.pincode.PasscodeFragment;
import nu.yona.app.utils.AppConstant;

/**
 * Created by kinnarvasa on 04/04/16.
 */
public class OTPActivity extends BaseActivity implements EventChangeListener {

    private PasscodeFragment otpFragment;
    private YonaFontTextView txtTitle;
    private RegisterUser user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank_container_layout);

        txtTitle = (YonaFontTextView) findViewById(R.id.toolbar_title);
        YonaApplication.getEventChangeManager().registerListener(this);
        loadOTPFragment();

        if (getIntent() != null && getIntent().getExtras() != null) {
            user = getIntent().getExtras().getParcelable(AppConstant.USER);
        }
    }

    private void loadOTPFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out, R.anim.back_slide_in, R.anim.back_slide_out);
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
                getActivityCategories();
                showLoadingView(false, null);
                showPasscodeScreen();
            }

            @Override
            public void onError(Object errorMessage) {
                if (errorMessage instanceof ErrorMessage) {
                    showLoadingView(false, null);
                    CustomAlertDialog.show(OTPActivity.this, ((ErrorMessage) errorMessage).getMessage(), getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            otpFragment.resetDigit();
                            dialogInterface.dismiss();
                        }
                    });
                }
            }
        });
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
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        finish();
    }

    /**
     * Update title.
     *
     * @param title Update title in Toolbar.
     */
    public void updateTitle(String title) {
        txtTitle.setText(title);
    }
}
