/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.pincode;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.ProgressBar;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.customview.YonaFontButton;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.BaseActivity;
import nu.yona.app.utils.AppConstant;

/**
 * Created by bhargavsuthar on 03/06/16.
 */
public class BasePasscodeActivity extends BaseActivity implements View.OnClickListener {


    /**
     * The Txt title.
     */
    protected YonaFontTextView txtTitle;
    /**
     * The Color code.
     */
    protected int colorCode, /**
     * The Progress drawable.
     */
    progressDrawable;
    /**
     * The M tool bar.
     */
    protected Toolbar mToolBar;
    /**
     * The Is from settings.
     */
    protected boolean isFromSettings;
    /**
     * The Profile progress.
     */
    protected ProgressBar profile_progress;
    /**
     * The Accont image.
     */
    protected ImageView accont_image;
    /**
     * The Passcode title.
     */
    protected YonaFontTextView passcode_title, /**
     * The Passcode description.
     */
    passcode_description, /**
     * The Passcode error.
     */
    passcode_error, /**
     * The Passcode reset.
     */
    passcode_reset;
    /**
     * The Passcode reset btn.
     */
    protected YonaFontButton passcodeResetBtn;
    /**
     * The Screen title.
     */
    protected String screenTitle;
    /**
     * The Screen type.
     */
    protected String screenType;
    /**
     * The Passcode view.
     */
    protected View passcodeView;
    private AnimationSet animationView;
    protected boolean isPasscodeFlowRetry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank_container_layout);

        mToolBar = (Toolbar) findViewById(R.id.toolbar_layout);

        progressDrawable = R.drawable.progress_bar;

        passcode_title = (YonaFontTextView) findViewById(R.id.passcode_title);
        passcode_description = (YonaFontTextView) findViewById(R.id.passcode_description);
        passcode_error = (YonaFontTextView) findViewById(R.id.passcode_error);
        accont_image = (ImageView) findViewById(R.id.img_account_check);
        passcode_reset = (YonaFontTextView) findViewById(R.id.passcode_reset);
        passcode_reset.setOnClickListener(this);
        passcodeResetBtn = (YonaFontButton) findViewById(R.id.btnPasscodeReset);
        passcodeResetBtn.setOnClickListener(this);

        passcodeView = findViewById(R.id.blank_container);

        profile_progress = (ProgressBar) findViewById(R.id.profile_progress);
        if (getIntent() != null && getIntent().getExtras() != null) {

            if (getIntent().getExtras().get(AppConstant.SCREEN_TITLE) != null) {
                screenTitle = getIntent().getExtras().getString(AppConstant.SCREEN_TITLE);
            }
            if (getIntent().getExtras().get(AppConstant.PROGRESS_DRAWABLE) != null) {
                progressDrawable = getIntent().getExtras().getInt(AppConstant.PROGRESS_DRAWABLE);
            }
            if (!TextUtils.isEmpty(getIntent().getExtras().getString(AppConstant.SCREEN_TYPE))) {
                screenType = getIntent().getExtras().getString(AppConstant.SCREEN_TYPE);
            }
            isFromSettings = getIntent().getExtras().getBoolean(AppConstant.FROM_SETTINGS, false);
        }

        if (isFromSettings) {
            colorCode = ContextCompat.getColor(this, R.color.mango);
            mToolBar.setBackgroundResource(R.drawable.triangle_shadow_mango);
        } else {
            colorCode = ContextCompat.getColor(this, R.color.grape); // default color will be grape
            mToolBar.setBackgroundResource(R.drawable.triangle_shadow_grape); //default theme of toolbar
        }

        findViewById(R.id.pincode_layout).setBackgroundColor(colorCode);

        findViewById(R.id.main_content).setBackgroundColor(colorCode);


        txtTitle = (YonaFontTextView) findViewById(R.id.toolbar_title);
    }


    /**
     * Initialize animation.
     */
    protected void initializeAnimation() {
        final Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(AppConstant.ANIMATION_DURATION);
        animationView = new AnimationSet(true);
        in.setStartOffset(AppConstant.TIMER_DELAY);
        animationView.addAnimation(in);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(screenTitle)) {
            updateTitle(screenTitle);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.passcode_reset:
            case R.id.btnPasscodeReset:
                doPasscodeReset();
                break;
            default:
                break;
        }
    }

    private void doPasscodeReset() {
        if (screenType != null) {
            switch (screenType) {
                case AppConstant.OTP:
                    YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_OTP_RESEND, null);
                    break;
                case AppConstant.PIN_RESET_VERIFICATION:
                case AppConstant.LOGGED_IN:
                    YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_PASSCODE_RESET, null);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Update title.
     *
     * @param title the title
     */
    private void updateTitle(String title) {
        txtTitle.setText(title);
    }

    /**
     * Update screen ui.
     */
    protected void updateScreenUI() {
        unblockUserUpdateUI();
        if (!TextUtils.isEmpty(screenType)) {
            if (screenType.equalsIgnoreCase(AppConstant.LOGGED_IN)) {
                visibleLoginView();
                populateLoginView();
            } else if (screenType.equalsIgnoreCase(AppConstant.OTP)) {
                populateOTPView();
                visibleView();
            } else if (screenType.equalsIgnoreCase(AppConstant.PIN_RESET_VERIFICATION)) {
                populatePinResetVerificationView();
                visibleLoginView();
                visibleView();
            }
        }
    }

    private void populateOTPView() {
        accont_image.setImageResource(R.drawable.add_avatar);
        passcode_title.setText(getString(R.string.accountlogin));
        passcode_description.setText(getString(R.string.accountloginsecuritymessage));
        updateTitle(getString(R.string.join));
        passcode_error.setVisibility(View.GONE);
        profile_progress.setProgress(getResources().getInteger(R.integer.passcode_progress_sixty));
        profile_progress.setProgressDrawable(ContextCompat.getDrawable(this, progressDrawable));
        passcode_reset.setText(getString(R.string.sendotpagain));
        passcode_reset.setVisibility(View.VISIBLE);
    }

    private void showAnimation() {
        accont_image.setAnimation(animationView);
        passcode_reset.setAnimation(animationView);
        passcode_title.setAnimation(animationView);
        passcode_description.setAnimation(animationView);
        profile_progress.setAnimation(animationView);
        passcode_error.setAnimation(animationView);
        passcodeView.setAnimation(animationView);

    }

    private void populatePinResetVerificationView() {
        accont_image.setImageResource(R.drawable.icn_secure);
        passcode_title.setText(getString(R.string.settings_current_pin));
        passcode_description.setText(getString(R.string.settings_current_pin_message));
        updateTitle(getString(R.string.changepin));
        profile_progress.setProgress(getResources().getInteger(R.integer.passcode_progress_thirty));
        profile_progress.setProgressDrawable(ContextCompat.getDrawable(this, progressDrawable));
        passcode_reset.setVisibility(View.GONE);
    }

    /**
     * Populate pin reset first step.
     */
    protected void populatePinResetFirstStep() {
        accont_image.setImageResource(R.drawable.icn_account_created);
        passcode_title.setText(getString(R.string.settings_new_pincode));
        passcode_description.setText(getString(R.string.settings_new_pin_message));
        updateTitle(getString(R.string.changepin));
        profile_progress.setProgress(getResources().getInteger(R.integer.passcode_progress_sixty));
        profile_progress.setProgressDrawable(ContextCompat.getDrawable(this, progressDrawable));
        passcode_reset.setVisibility(View.GONE);
        showAnimation();
    }

    /**
     * Populate pin reset second step.
     */
    protected void populatePinResetSecondStep() {
        accont_image.setImageResource(R.drawable.icn_account_created);
        passcode_title.setText(getString(R.string.settings_confirm_new_pin));
        passcode_description.setText(getString(R.string.settings_confirm_new_pin_message));
        updateTitle(getString(R.string.changepin));
        profile_progress.setProgress(getResources().getInteger(R.integer.passcode_progress_complete));
        profile_progress.setProgressDrawable(ContextCompat.getDrawable(this, progressDrawable));
        passcode_reset.setVisibility(View.GONE);
        showAnimation();
    }

    /**
     * Visible view.
     */
    protected void visibleView() {
        passcode_title.setVisibility(View.VISIBLE);
        passcode_description.setVisibility(View.VISIBLE);
        profile_progress.setVisibility(View.VISIBLE);
    }

    private void visibleLoginView() {
        passcode_title.setVisibility(View.VISIBLE);
        passcode_description.setVisibility(View.GONE);
        profile_progress.setVisibility(View.GONE);
        passcode_error.setVisibility(View.GONE);
        passcode_reset.setVisibility(View.VISIBLE);
    }

    private void populateLoginView() {
        accont_image.setImageResource(R.drawable.icn_y);
        passcode_title.setText(getString(R.string.passcodetitle));
        passcode_reset.setText(getString(R.string.passcodereset));
    }

    /**
     * update screen's text as per account pincode's verification
     */
    protected void populateVerifyPasscodeView() {
        accont_image.setImageResource(R.drawable.icn_secure);
        passcode_title.setText(getString(R.string.passcodestep2title));
        passcode_description.setText(getString(R.string.passcodestep2desc));
        passcode_error.setVisibility(View.GONE);
        updateTitle(getString(R.string.pincode));
        profile_progress.setProgress(getResources().getInteger(R.integer.passcode_verify_progerss));
        showAnimation();
    }

    /**
     * update screen's text as per Account pincode creation
     */
    protected void populatePasscodeView() {
        accont_image.setImageResource(R.drawable.icn_account_created);
        if (isPasscodeFlowRetry) {
            passcode_title.setText(getString(R.string.passcodestep1retrytitle));
            passcode_description.setText(getString(R.string.passcodestep1retrydesc));
        } else {
            passcode_title.setText(getString(R.string.passcodestep1title));
            passcode_description.setText(getString(R.string.passcodestep1desc));
        }
        profile_progress.setProgress(getResources().getInteger(R.integer.passcode_create_progress));
        passcode_error.setVisibility(View.GONE);
        updateTitle(getString(R.string.pincode));
        showAnimation();
    }

    private void unblockUserUpdateUI() {
        passcode_reset.setVisibility(View.VISIBLE);
        passcodeResetBtn.setVisibility(View.GONE);
        passcodeView.setVisibility(View.VISIBLE);
        profile_progress.setVisibility(View.VISIBLE);
        passcode_error.setVisibility(View.VISIBLE);
    }

    /**
     * Block user.
     */
    protected void blockUser() {

        passcode_reset.setVisibility(View.GONE);
        passcodeResetBtn.setVisibility(View.VISIBLE);
        passcodeView.setVisibility(View.GONE);
        profile_progress.setVisibility(View.GONE);
        passcode_description.setText(getString(R.string.msgblockuser));
        passcode_description.setVisibility(View.VISIBLE);
        passcode_error.setVisibility(View.GONE);
        accont_image.setImageResource(R.drawable.icn_secure);
        passcode_title.setText(getString(R.string.msgblocktitle));
    }


}
