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

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.AuthenticateManager;
import nu.yona.app.api.manager.impl.AuthenticateManagerImpl;
import nu.yona.app.api.model.ErrorMessage;
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
    private AuthenticateManager authenticateManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank_container_layout);

        authenticateManager = new AuthenticateManagerImpl(this);

        txtTitle = (YonaFontTextView) findViewById(R.id.txt_nav_title);
        YonaApplication.getEventChangeManager().registerListener(this);
        loadOTPFragment();
    }

    private void loadOTPFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.blank_container, getOTPFragment());
        fragmentTransaction.commit();
    }

    private PasscodeFragment getOTPFragment() {
        if(otpFragment == null) {
            Bundle bPasscode = new Bundle();
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

    private void validateOTP(final String otpString) {
        showLoadingView(true, null);
        authenticateManager.verifyMobileNumber(otpString, new DataLoadListener() {
            @Override
            public void onDataLoad(Object result) {
                showLoadingView(false, null);
                showPasscodeScreen();
            }

            @Override
            public void onError(Object errorMessage) {
                if(errorMessage instanceof ErrorMessage) {
                    showLoadingView(false, null);
                    CustomAlertDialog.show(OTPActivity.this, ((ErrorMessage)errorMessage).getMessage(), getString(R.string.ok), new DialogInterface.OnClickListener() {
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

    private void resendOTP(){
        showLoadingView(true, null);
        otpFragment.resetDigit();
        authenticateManager.resendOTP(new DataLoadListener() {
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

    private void showPasscodeScreen(){
        startActivity(new Intent(OTPActivity.this, PasscodeActivity.class));
        finish();
    }

    public void updateTitle(String title) {
        txtTitle.setText(title);
    }
}
