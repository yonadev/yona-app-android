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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.utils.AppConstant;

/**
 * Created by bhargavsuthar on 4/3/16.
 */
public class PasscodeActivity extends BasePasscodeActivity implements EventChangeListener {

    private int PASSCODE_STEP = 0;
    private String first_passcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        YonaApplication.getEventChangeManager().registerListener(this);
        loadPasscodeView();
        initializeAnimation();
    }

    private void updateScreen() {
        visibleView();
        if (!isFromSettings) {
            if (PASSCODE_STEP == 0) {
                populatePasscodeView();
            } else {
                populateVerifyPasscodeView();
            }
        } else {
            if (PASSCODE_STEP == 0) {
                populatePinResetFirstStep();
            } else {
                populatePinResetSecondStep();
            }
        }
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
            first_passcode = null;
            loadPasscodeView();
        } else {
            finish();
        }
    }

    private Fragment getPasscodeFragment() {
        Bundle bPasscode = new Bundle();
        if (getIntent().getExtras() != null && getIntent().getExtras().get(AppConstant.COLOR_CODE) != null) {
            bPasscode.putAll(getIntent().getExtras());
        }
        bPasscode.putInt(AppConstant.COLOR_CODE, colorCode);
        PasscodeFragment passcodeFragment = new PasscodeFragment();
        passcodeFragment.setArguments(bPasscode);
        return passcodeFragment;
    }

    private Fragment getPasscodeVerifyFragment() {
        Bundle bVerifyPasscode = new Bundle();
        if (getIntent().getExtras() != null && getIntent().getExtras().get(AppConstant.COLOR_CODE) != null) {
            bVerifyPasscode.putAll(getIntent().getExtras());
        }
        bVerifyPasscode.putInt(AppConstant.COLOR_CODE, colorCode);
        PasscodeFragment verifyPasscodeFragment = new PasscodeFragment();
        verifyPasscodeFragment.setArguments(bVerifyPasscode);
        return verifyPasscodeFragment;
    }


    private void loadPasscodeView() {
        PASSCODE_STEP = 0;
        updateScreen();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.blank_container, getPasscodeFragment());
        fragmentTransaction.commit();
    }

    private void loadVerifyPasscodeView() {
        PASSCODE_STEP = 1;
        updateScreen();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.blank_container, getPasscodeVerifyFragment());
        fragmentTransaction.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftInput();
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
            case EventChangeManager.EVENT_PASSCODE_ERROR:
                passcode_error.setText((String) object);
                break;
            case EventChangeManager.EVENT_CLOSE_ALL_ACTIVITY_EXCEPT_LAUNCH:
                finish();
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

        if (APIManager.getInstance().getPasscodeManager().validateTwoPasscode(first_passcode, code)) {
            if (isFromSettings) {
                finish();
            } else {
                showChallengesScreen();
            }
        } else {
            doBack();
            first_passcode = "";

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_PASSCODE_ERROR, getString(R.string.passcodetryagain));
                }
            }, AppConstant.TIMER_DELAY);

        }

    }

    private void showChallengesScreen() {
        Intent intent = new Intent(PasscodeActivity.this, YonaActivity.class);
        intent.putExtra(AppConstant.FROM_LOGIN, true);
        ActivityCompat.startActivity(this, intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, AppConstant.TIMER_DELAY);

    }
}


