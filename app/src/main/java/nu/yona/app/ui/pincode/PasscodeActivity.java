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
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.enums.NotificationMessageEnum;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.BaseActivity;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.utils.AppConstant;

/**
 * Created by bhargavsuthar on 4/3/16.
 */
public class PasscodeActivity extends BaseActivity implements EventChangeListener {

    private int PASSCODE_STEP = 0;
    private String first_passcode;
    private YonaFontTextView txtTitle;
    private int colorCode;
    private Toolbar mToolBar;
    private boolean isFromSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank_container_layout);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        mToolBar = (Toolbar) findViewById(R.id.toolbar_layout);

        if (getIntent() != null && getIntent().getExtras() != null) {
            if (getIntent().getExtras().get(AppConstant.COLOR_CODE) != null) {
                colorCode = getIntent().getExtras().getInt(AppConstant.COLOR_CODE);
            } else {
                colorCode = ContextCompat.getColor(this, R.color.grape); // default color will be grape
            }
            if (getIntent().getExtras().get(AppConstant.TITLE_BACKGROUND_RESOURCE) != null) {
                mToolBar.setBackgroundResource(getIntent().getExtras().getInt(AppConstant.TITLE_BACKGROUND_RESOURCE));
            } else {
                mToolBar.setBackgroundResource(R.drawable.triangle_shadow_grape); //default theme of toolbar
            }
            isFromSettings = getIntent().getExtras().getBoolean(AppConstant.FROM_SETTINGS);
        } else {
            colorCode = ContextCompat.getColor(this, R.color.grape); // default color will be grape
            mToolBar.setBackgroundResource(R.drawable.triangle_shadow_grape); //default theme of toolbar
        }
        txtTitle = (YonaFontTextView) findViewById(R.id.toolbar_title);
        YonaApplication.getEventChangeManager().registerListener(this);
        loadPasscodeView(true);
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
            loadPasscodeView(false);
        } else {
            finish();
        }
    }

    private Fragment getPasscodeFragment() {
        Bundle bPasscode = new Bundle();
        bPasscode.putInt(AppConstant.COLOR_CODE, colorCode);
        if (isFromSettings) {
            bPasscode.putString(AppConstant.SCREEN_TYPE, AppConstant.PIN_RESET_FIRST_STEP);
        } else {
            bPasscode.putString(AppConstant.SCREEN_TYPE, AppConstant.PASSCODE);
        }
        PasscodeFragment passcodeFragment = new PasscodeFragment();
        passcodeFragment.setArguments(bPasscode);
        return passcodeFragment;
    }

    private Fragment getPasscodeVerifyFragment() {
        Bundle bVerifyPasscode = new Bundle();
        bVerifyPasscode.putInt(AppConstant.COLOR_CODE, colorCode);
        if (isFromSettings) {
            bVerifyPasscode.putString(AppConstant.SCREEN_TYPE, AppConstant.PIN_RESET_SECOND_STEP);
        } else {
            bVerifyPasscode.putString(AppConstant.SCREEN_TYPE, AppConstant.PASSCODE_VERIFY);
        }
        PasscodeFragment verifyPasscodeFragment = new PasscodeFragment();
        verifyPasscodeFragment.setArguments(bVerifyPasscode);
        return verifyPasscodeFragment;
    }


    private void loadPasscodeView(boolean isEntryAnim) {
        PASSCODE_STEP = 0;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentAnimation(fragmentTransaction, isEntryAnim);
        fragmentTransaction.replace(R.id.blank_container, getPasscodeFragment());
        fragmentTransaction.commit();
    }

    private void loadVerifyPasscodeView() {
        PASSCODE_STEP = 1;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentAnimation(fragmentTransaction, true);
        fragmentTransaction.replace(R.id.blank_container, getPasscodeVerifyFragment());
        fragmentTransaction.commit();
    }

    private void fragmentAnimation(FragmentTransaction animTransaction, boolean isEntryAnim) {
        if (isEntryAnim) {
            animTransaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out);
        } else {
            animTransaction.setCustomAnimations(R.anim.back_slide_in, R.anim.back_slide_out);
        }
    }

    @Override
    public void onStateChange(int eventType, Object object) {
        switch (eventType) {
            case EventChangeManager.EVENT_PASSCODE_STEP_ONE:
                loadPasscodeView(false);
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
        startActivity(new Intent(PasscodeActivity.this, YonaActivity.class).putExtra(AppConstant.FROM_LOGIN, true));
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        finish();
    }
}


