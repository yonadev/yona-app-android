/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.pincode;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.WindowManager;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.PinResetDelay;
import nu.yona.app.customview.CustomAlertDialog;
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

        passcodeFragment = new PasscodeFragment();
        passcodeFragment.setArguments(getIntent().getExtras());
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out);
        fragmentTransaction.replace(R.id.blank_container, passcodeFragment);
        fragmentTransaction.commit();

        isUserBlocked = YonaApplication.getUserPreferences().getBoolean(PreferenceConstant.USER_BLOCKED, false);
        if (TextUtils.isEmpty(screen_type)) {
            screen_type = AppConstant.LOGGED_IN;
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
    protected void onResume() {
        super.onResume();
        if (YonaApplication.getUserPreferences().getBoolean(PreferenceConstant.USER_BLOCKED, false) && passcodeFragment != null) {
            updateBlockMsg();
        }
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
                    YonaApplication.getUserPreferences().edit().putBoolean(PreferenceConstant.USER_BLOCKED, true).commit();
                    updateBlockMsg();
                    YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_CLOSE_YONA_ACTIVITY, null);
                } else {
                    YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_PASSCODE_ERROR, getString(R.string.passcodetryagain));
                }
                break;
            case EventChangeManager.EVENT_PASSCODE_RESET:
                doPinReset();
            case EventChangeManager.EVENT_PASSCODE_ERROR:
                passcode_error.setText((String) object);
                break;
            default:
                break;
        }

    }

    private void showChallengesScreen() {
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
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
                    CustomAlertDialog.show(PinActivity.this, getString(R.string.resetpinrequest, AppUtils.getTimeForOTP(delay.getDelay())), getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_CLOSE_YONA_ACTIVITY, null);
                            loadOTPScreen();
                        }
                    });
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
        startActivity(new Intent(PinActivity.this, OTPActivity.class));
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
        this.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
}
