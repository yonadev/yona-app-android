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
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.PinResetDelay;
import nu.yona.app.customview.CustomAlertDialog;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.BaseActivity;
import nu.yona.app.ui.signup.OTPActivity;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.AppUtils;
import nu.yona.app.utils.PreferenceConstant;

/**
 * Created by bhargavsuthar on 3/30/16.
 */
public class PinActivity extends BaseActivity implements EventChangeListener {

    private TextView txtTitle;
    private PasscodeFragment passcodeFragment;
    private Toolbar mToolBar;
    private String screenType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank_container_layout);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        YonaApplication.getEventChangeManager().registerListener(this);

        mToolBar = (Toolbar) findViewById(R.id.toolbar_layout);
        txtTitle = (TextView) findViewById(R.id.toolbar_title);

        if (getIntent() != null && getIntent().getExtras() != null) {
            if (getIntent().getExtras().get(AppConstant.TITLE_BACKGROUND_RESOURCE) != null) {
                mToolBar.setBackgroundResource(getIntent().getExtras().getInt(AppConstant.TITLE_BACKGROUND_RESOURCE));
            } else {
                mToolBar.setBackgroundResource(R.drawable.triangle_shadow_grape); //default theme of toolbar
            }
            if (!TextUtils.isEmpty(getIntent().getExtras().getString(AppConstant.SCREEN_TYPE))) {
                screenType = getIntent().getExtras().getString(AppConstant.SCREEN_TYPE);
            }
            if (getIntent().getExtras().get(AppConstant.PASSCODE_TEXT_BACKGROUND) != null) {
                findViewById(R.id.main_content).setBackground(ContextCompat.getDrawable(this, getIntent().getExtras().getInt(AppConstant.PASSCODE_TEXT_BACKGROUND)));
            }
        } else {
            mToolBar.setBackgroundResource(R.drawable.triangle_shadow_grape); //default theme of toolbar
        }

        passcodeFragment = new PasscodeFragment();
        passcodeFragment.setArguments(getIntent().getExtras());
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out);
        fragmentTransaction.replace(R.id.blank_container, passcodeFragment);
        fragmentTransaction.commit();
    }

    /**
     * Update title.
     *
     * @param title the title
     */
    public void updateTitle(String title) {
        txtTitle.setText(title);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        YonaApplication.getEventChangeManager().unRegisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(screenType) && screenType.equalsIgnoreCase(AppConstant.PIN_RESET_VERIFICATION)) {
            updateTitle(getString(R.string.pincode));
        } else {
            updateTitle(getString(R.string.login));
        }
        if (YonaApplication.getUserPreferences().getBoolean(PreferenceConstant.USER_BLOCKED, false) && passcodeFragment != null) {
            updateBlockMsg();
        }
    }

    private void updateBlockMsg() {
        YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_PASSCODE_ERROR, getString(R.string.msgblockuser));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        passcodeFragment.disableEditable();
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
                } else {
                    YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_PASSCODE_ERROR, getString(R.string.passcodetryagain));
                }
                break;
            case EventChangeManager.EVENT_PASSCODE_RESET:
                doPinReset();
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
        this.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        startActivity(intent);
    }
}
