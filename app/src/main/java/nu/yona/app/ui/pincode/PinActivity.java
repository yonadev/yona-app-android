/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.pincode;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;

import java.util.Date;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.analytics.AnalyticsConstant;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.PinResetDelay;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.signup.OTPActivity;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.AppUtils;
import nu.yona.app.utils.PreferenceConstant;

/**
 * Created by bhargavsuthar on 3/30/16.
 */
public class PinActivity extends BasePasscodeActivity implements EventChangeListener {

    private PasscodeFragment passcodeFragment;
    private boolean isUserBlocked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        YonaApplication.getEventChangeManager().registerListener(this);
        passcode_error.setVisibility(View.GONE);
        passcodeFragment = new PasscodeFragment();
        passcodeFragment.setArguments(getIntent().getExtras());
        passcodeFragment.getArguments().putString(AppConstant.PASSCODE_SCREEN_NAME, AnalyticsConstant.PASSCODE_SCREEN);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.blank_container, passcodeFragment);
        fragmentTransaction.commit();

        isUserBlocked = YonaApplication.getEventChangeManager().getSharedPreference().getUserPreferences().getBoolean(PreferenceConstant.USER_BLOCKED, false);
        if (TextUtils.isEmpty(screenType)) {
            screenType = AppConstant.LOGGED_IN;
        }
        if (!isUserBlocked) {
            updateScreenUI();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        YonaApplication.getEventChangeManager().unRegisterListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (YonaApplication.getEventChangeManager().getSharedPreference().getUserPreferences().getBoolean(PreferenceConstant.USER_BLOCKED, false) && passcodeFragment != null) {
            updateBlockMsg();
        }
        updateData();
    }

    private void updateBlockMsg() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        passcodeFragment.disableEditable();
        blockUser();
    }

    @Override
    public void onStateChange(int eventType, Object object) {
        switch (eventType) {
            case EventChangeManager.EVENT_PASSCODE_STEP_TWO:
                String passcode = (String) object;
                if (APIManager.getInstance().getPasscodeManager().validatePasscode(passcode)) {
                    showChallengesScreen();
                } else if (APIManager.getInstance().getPasscodeManager().isWrongCounterReached()) {
                    passcode_error.setVisibility(View.GONE);
                    YonaApplication.getEventChangeManager().getSharedPreference().getUserPreferences().edit().putBoolean(PreferenceConstant.USER_BLOCKED, true).commit();
                    updateBlockMsg();
                    YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_CLOSE_YONA_ACTIVITY, null);
                } else {
                    passcode_error.setVisibility(View.VISIBLE);
                    YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_PASSCODE_ERROR, getString(R.string.passcodetryagain));
                }
                break;
            case EventChangeManager.EVENT_PASSCODE_RESET:
                doPinReset();
            case EventChangeManager.EVENT_PASSCODE_ERROR:
                passcode_error.setText((String) object);
                break;
            case EventChangeManager.EVENT_USER_NOT_EXIST:
            case EventChangeManager.EVENT_CLOSE_ALL_ACTIVITY_EXCEPT_LAUNCH:
                finish();
                break;
            default:
                break;
        }

    }

    private void showChallengesScreen() {
        if (!TextUtils.isEmpty(screenType) && screenType.equalsIgnoreCase(AppConstant.PIN_RESET_VERIFICATION)) {
            updatePin();
        }
        finish();
    }


    private void doPinReset() {
        showLoadingView(true, null);
        APIManager.getInstance().getAuthenticateManager().requestPinReset(new DataLoadListener() {
            @Override
            public void onDataLoad(Object result) {
                showLoadingView(false, null);
                if (result instanceof PinResetDelay) {
                    PinResetDelay delay = (PinResetDelay) result;
                    final Pair<String, Long> delayTime = AppUtils.getTimeForOTP(delay.getDelay());
                    SharedPreferences.Editor pref = YonaApplication.getEventChangeManager().getSharedPreference().getUserPreferences().edit();
                    pref.putLong(PreferenceConstant.USER_WAIT_TIME_IN_LONG, (new Date().getTime() + delayTime.second));
                    pref.putString(PreferenceConstant.USER_WAIT_TIME_IN_STRING, delayTime.first);
                    pref.commit();
                    APIManager.getInstance().getPasscodeManager().resetWrongCounter();
                    loadOTPScreen();
                }
            }

            @Override
            public void onError(Object errorMessage) {
                ErrorMessage message = (ErrorMessage) errorMessage;
                showLoadingView(false, null);
                Snackbar.make(findViewById(android.R.id.content), message.getMessage(), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.ok), null)
                        .show();
            }
        });
    }

    private void loadOTPScreen() {
        startActivity(new Intent(this, OTPActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        if (!(!TextUtils.isEmpty(screenType) && screenType.equalsIgnoreCase(AppConstant.PIN_RESET_VERIFICATION))) {
            YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_CLOSE_YONA_ACTIVITY, null);
        }
        finish();
    }

    private void updatePin() {
        Intent intent = new Intent(this, PasscodeActivity.class);
        Bundle bundle = new Bundle();
        if (getIntent() != null && getIntent().getExtras() != null) {
            bundle.putAll(getIntent().getExtras());
        }
        bundle.putBoolean(AppConstant.FROM_SETTINGS, true);
        bundle.putString(AppConstant.SCREEN_TYPE, AppConstant.PIN_RESET_FIRST_STEP);
        bundle.putInt(AppConstant.TITLE_BACKGROUND_RESOURCE, R.drawable.triangle_shadow_mango);
        bundle.putInt(AppConstant.COLOR_CODE, ContextCompat.getColor(this, R.color.mango));
        bundle.putInt(AppConstant.PROGRESS_DRAWABLE, R.drawable.pin_reset_progress_bar);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void updateData() {
        AppUtils.sendLogToServer(0);
        APIManager.getInstance().getActivityCategoryManager().getActivityCategoriesById(null);
    }
}
