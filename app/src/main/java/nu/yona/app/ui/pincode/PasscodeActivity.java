/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.pincode;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.impl.PasscodeManagerImpl;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.BaseActivity;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.utils.AppConstant;

/**
 * Created by bhargavsuthar on 4/3/16.
 */
public class PasscodeActivity extends BaseActivity implements EventChangeListener {

    private PasscodeFragment passcodeFragment;
    private PasscodeFragment verifyPasscodeFragment;
    private int PASSCODE_STEP = 0;
    private String first_passcode;
    private PasscodeManagerImpl passcodeMangerImpl;
    private YonaFontTextView txtTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank_container_layout);

        passcodeMangerImpl = new PasscodeManagerImpl();

        txtTitle = (YonaFontTextView) findViewById(R.id.txt_nav_title);
        YonaApplication.getEventChangeManager().registerListener(this);
        loadPasscodeView();
    }

    public void updateTitle(String title) {
        txtTitle.setText(title);
    }

    @Override
    public void onBackPressed() {
        doBack();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        YonaApplication.getEventChangeManager().unRegisterListener(this);
    }

    private void doBack() {
        if (PASSCODE_STEP == 1) {
            loadPasscodeView();
        } else {
            finish();
        }
    }

    private Fragment getPasscodeFragment() {
        Bundle bPasscode = new Bundle();
        bPasscode.putString(AppConstant.SCREEN_TYPE, AppConstant.PASSCODE);
        passcodeFragment = new PasscodeFragment();
        passcodeFragment.setArguments(bPasscode);
        return passcodeFragment;
    }

    private Fragment getPasscodeVerifyFragment() {
        Bundle bVerifyPasscode = new Bundle();
        bVerifyPasscode.putString(AppConstant.SCREEN_TYPE, AppConstant.PASSCODE_VERIFY);
        verifyPasscodeFragment = new PasscodeFragment();
        verifyPasscodeFragment.setArguments(bVerifyPasscode);
        return verifyPasscodeFragment;
    }


    private void loadPasscodeView() {
        PASSCODE_STEP = 0;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.blank_container, getPasscodeFragment());
        fragmentTransaction.commit();
    }

    private void loadVerifyPasscodeView() {
        PASSCODE_STEP = 1;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.blank_container, getPasscodeVerifyFragment());
        fragmentTransaction.commit();
    }

    @Override
    public void onStateChange(int eventType, Object object) {
        switch (eventType) {
            case EventChangeManager.EVENT_PASSCODE_STEP_ONE:
                loadPasscodeView();
                break;
            case EventChangeManager.EVENT_PASSCODE_STEP_TWO:
                loadVerifyPasscodeView();
                if (TextUtils.isEmpty(first_passcode)) {
                    first_passcode = (String) object;
                } else {
                    validatePasscode((String) object);
                }
                break;
            default:
                break;
        }
    }


    /**
     * Validate the passcode
     *
     * @param code
     */
    private void validatePasscode(String code) {

        if (passcodeMangerImpl.validateTwoPasscode(first_passcode, code)) {
            showChallengesScreen();
        } else {
            doBack();
            first_passcode = "";

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_PASSCODE_ERROR, getString(R.string.passcode_tryagain));
                }
            }, AppConstant.TIMER_DELAY);

        }

    }

    public void showChallengesScreen() {
        startActivity(new Intent(PasscodeActivity.this, YonaActivity.class).putExtra(AppConstant.FROM_LOGIN, true));
        finish();
    }
}


