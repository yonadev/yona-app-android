/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.pincode;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.analytics.AnalyticsConstant;
import nu.yona.app.analytics.YonaAnalytics;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.customview.YonaFontButton;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.listener.DataLoadListenerImpl;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.BaseActivity;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.PreferenceConstant;

import static nu.yona.app.YonaApplication.getAppUser;
import static nu.yona.app.YonaApplication.getSharedUserPreferences;

/**
 * Created by bhargavsuthar on 03/06/16.
 */
public abstract class BasePasscodeActivity extends BaseActivity implements View.OnClickListener
{


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

	protected LinearLayout timerLayout;

	protected TextView hourText, minuteText, secondText;
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
	long totalTimerTime;
	private Timer timer = new Timer();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blank_container_layout);
		mToolBar = findViewById(R.id.toolbar_layout);
		progressDrawable = R.drawable.progress_bar;
		initializeViewAttributes();
		passcodeResetBtn.setOnClickListener(this);
		passcodeView = findViewById(R.id.blank_container);
		profile_progress = findViewById(R.id.profile_progress);
		addExtrasToIntent();
		setBackgroundForToolBar();
		txtTitle = findViewById(R.id.toolbar_title);
	}

	private void initializeViewAttributes()
	{
		passcode_title = findViewById(R.id.passcode_title);
		passcode_description = findViewById(R.id.passcode_description);
		passcode_error = findViewById(R.id.passcode_error);
		accont_image = findViewById(R.id.img_account_check);
		passcode_reset = findViewById(R.id.passcode_reset);
		passcode_reset.setOnClickListener(this);
		passcodeResetBtn = findViewById(R.id.btnPasscodeReset);
		timerLayout = findViewById(R.id.timerLayout);
		hourText = findViewById(R.id.hourText);
		minuteText = findViewById(R.id.minuteText);
		secondText = findViewById(R.id.secondText);
	}

	private void addExtrasToIntent()
	{
		if (getIntent() == null || getIntent().getExtras() == null)
		{
			return;
		}
		if (getIntent().getExtras().get(AppConstant.SCREEN_TITLE) != null)
		{
			screenTitle = getIntent().getExtras().getString(AppConstant.SCREEN_TITLE);
		}
		if (getIntent().getExtras().get(AppConstant.PROGRESS_DRAWABLE) != null)
		{
			progressDrawable = getIntent().getExtras().getInt(AppConstant.PROGRESS_DRAWABLE);
		}
		if (!TextUtils.isEmpty(getIntent().getExtras().getString(AppConstant.SCREEN_TYPE)))
		{
			screenType = getIntent().getExtras().getString(AppConstant.SCREEN_TYPE);
		}
		isFromSettings = getIntent().getExtras().getBoolean(AppConstant.FROM_SETTINGS, false);
	}

	private void setBackgroundForToolBar()
	{
		if (isFromSettings)
		{
			colorCode = ContextCompat.getColor(this, R.color.mango);
			mToolBar.setBackgroundResource(R.drawable.triangle_shadow_mango);
		}
		else
		{
			colorCode = ContextCompat.getColor(this, R.color.grape); // default color will be grape
			mToolBar.setBackgroundResource(R.drawable.triangle_shadow_grape); //default theme of toolbar
		}
		findViewById(R.id.pincode_layout).setBackgroundColor(colorCode);
		findViewById(R.id.main_content).setBackgroundColor(colorCode);
	}

	/**
	 * Initialize animation.
	 */
	protected void initializeAnimation()
	{
		final Animation in = new AlphaAnimation(0.0f, 1.0f);
		in.setDuration(AppConstant.ANIMATION_DURATION);
		animationView = new AnimationSet(true);
		in.setStartOffset(AppConstant.TIMER_DELAY);
		animationView.addAnimation(in);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		if (!TextUtils.isEmpty(screenTitle))
		{
			updateTitle(screenTitle);
		}
	}

	@Override
	public void onPause()
	{
		super.onPause();
		if (timer != null)
		{
			timer.cancel();
		}
	}

	@Override
	public void onBackPressed()
	{
		// No action if back button is pressed in pin reset screen
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.passcode_reset:
			case R.id.btnPasscodeReset:
				displayAlertForPasscodeReset();
				break;
			default:
				break;
		}
	}

	private void displayAlertForPasscodeReset()
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BasePasscodeActivity.this);
		alertDialogBuilder.setMessage(getString(R.string.resetpinalert));
		alertDialogBuilder.setPositiveButton(getString(R.string.yes), (dialog, which) -> doPasscodeReset());
		alertDialogBuilder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel());
		alertDialogBuilder.create().show();
	}

	private void doPasscodeReset()
	{
		if (screenType == null)
		{
			return;
		}
		switch (screenType)
		{
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

	/**
	 * Update title.
	 *
	 * @param title the title
	 */
	private void updateTitle(String title)
	{
		txtTitle.setText(title);
	}

	/**
	 * Update screen ui.
	 */
	protected void updateScreenUI()
	{
		unblockUserUpdateUI();
		if (TextUtils.isEmpty(screenType))
		{
			return;
		}
		if (screenType.equalsIgnoreCase(AppConstant.LOGGED_IN))
		{
			visibleLoginView();
			populateLoginView();
		}
		else if (screenType.equalsIgnoreCase(AppConstant.OTP))
		{
			populateOTPView();
			visibleView();
		}
		else if (screenType.equalsIgnoreCase(AppConstant.PIN_RESET_VERIFICATION))
		{
			populatePinResetVerificationView();
			visibleLoginView();
			visibleView();
		}
	}

	private void populateOTPView()
	{
		YonaAnalytics.trackCategoryScreen(AnalyticsConstant.OTP_SCREEN, AnalyticsConstant.OTP_SCREEN);
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

	private void showAnimation()
	{
		accont_image.setAnimation(animationView);
		passcode_reset.setAnimation(animationView);
		passcode_title.setAnimation(animationView);
		passcode_description.setAnimation(animationView);
		profile_progress.setAnimation(animationView);
		passcode_error.setAnimation(animationView);
		passcodeView.setAnimation(animationView);

	}

	private void populatePinResetVerificationView()
	{
		YonaAnalytics.trackCategoryScreen(AnalyticsConstant.VERIFY_PIN_BEFORE_RESET, AnalyticsConstant.VERIFY_PIN_BEFORE_RESET);
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
	protected void populatePinResetFirstStep()
	{
		YonaAnalytics.trackCategoryScreen(AnalyticsConstant.FIRST_PASSCODE_SCREEN, AnalyticsConstant.FIRST_PASSCODE_SCREEN);
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
	protected void populatePinResetSecondStep()
	{
		YonaAnalytics.trackCategoryScreen(AnalyticsConstant.SECOND_PASSCODE_SCREEN, AnalyticsConstant.SECOND_PASSCODE_SCREEN);
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
	protected void visibleView()
	{
		passcode_title.setVisibility(View.VISIBLE);
		passcode_description.setVisibility(View.VISIBLE);
		profile_progress.setVisibility(View.VISIBLE);
	}

	private void visibleLoginView()
	{
		passcode_title.setVisibility(View.VISIBLE);
		passcode_description.setVisibility(View.GONE);
		profile_progress.setVisibility(View.GONE);
		passcode_error.setVisibility(View.GONE);
		passcode_reset.setVisibility(View.VISIBLE);
	}

	private void populateLoginView()
	{
		YonaAnalytics.trackCategoryScreen(AnalyticsConstant.LOGIN_PASSCODE_SCREEN, AnalyticsConstant.LOGIN_PASSCODE_SCREEN);
		accont_image.setImageResource(R.drawable.icn_y);
		passcode_title.setText(getString(R.string.passcodetitle));
		passcode_reset.setText(getString(R.string.passcodereset));
	}

	/**
	 * update screen's text as per account pincode's verification
	 */
	protected void populateVerifyPasscodeView()
	{
		YonaAnalytics.trackCategoryScreen(AnalyticsConstant.SECOND_PASSCODE_SCREEN, AnalyticsConstant.SECOND_PASSCODE_SCREEN);
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
	protected void populatePasscodeView()
	{
		YonaAnalytics.trackCategoryScreen(AnalyticsConstant.FIRST_PASSCODE_SCREEN, AnalyticsConstant.FIRST_PASSCODE_SCREEN);
		accont_image.setImageResource(R.drawable.icn_account_created);
		if (isPasscodeFlowRetry)
		{
			passcode_title.setText(getString(R.string.passcodestep1retrytitle));
			passcode_description.setText(getString(R.string.passcodestep1retrydesc));
		}
		else
		{
			passcode_title.setText(getString(R.string.passcodestep1title));
			passcode_description.setText(getString(R.string.passcodestep1desc));
		}
		profile_progress.setProgress(getResources().getInteger(R.integer.passcode_create_progress));
		passcode_error.setVisibility(View.GONE);
		updateTitle(getString(R.string.pincode));
		showAnimation();
	}

	private void unblockUserUpdateUI()
	{
		passcode_reset.setVisibility(View.VISIBLE);
		passcodeResetBtn.setVisibility(View.GONE);
		passcodeView.setVisibility(View.VISIBLE);
		profile_progress.setVisibility(View.VISIBLE);
		passcode_error.setVisibility(View.VISIBLE);
	}

	/**
	 * Block user.
	 */
	protected void blockUser()
	{
		YonaAnalytics.trackCategoryScreen(AnalyticsConstant.USER_BLOCK_VIEW, AnalyticsConstant.USER_BLOCK_VIEW);
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


	protected void hideTimerFromUser()
	{
		YonaAnalytics.trackCategoryScreen(AnalyticsConstant.OTP_SCREEN, AnalyticsConstant.OTP_SCREEN);
		passcode_reset.setVisibility(View.VISIBLE);
		passcodeResetBtn.setVisibility(View.GONE);
		passcodeView.setVisibility(View.VISIBLE);
		profile_progress.setVisibility(View.VISIBLE);
		passcode_error.setVisibility(View.VISIBLE);
		timerLayout.setVisibility(View.GONE);
		populateOTPView();
		if (timer != null)
		{
			timer.cancel();
		}
	}

	protected void showTimerToUser()
	{
		YonaAnalytics.trackCategoryScreen(AnalyticsConstant.TIMER_VIEW, AnalyticsConstant.TIMER_VIEW);
		passcode_reset.setVisibility(View.GONE);
		timerLayout.setVisibility(View.VISIBLE);
		passcodeView.setVisibility(View.GONE);
		profile_progress.setVisibility(View.GONE);
		passcode_description.setText(getString(R.string.timer_wait_desc, getSharedUserPreferences().getString(PreferenceConstant.USER_WAIT_TIME_IN_STRING, "")));
		passcode_description.setVisibility(View.VISIBLE);
		passcode_error.setVisibility(View.GONE);
		accont_image.setImageResource(R.drawable.icn_secure);
		passcode_title.setText(getString(R.string.timer_wait_title));
		showTime();
	}

	private void showTime()
	{
		totalTimerTime = 0;
		long serverTime = getSharedUserPreferences().getLong(PreferenceConstant.USER_WAIT_TIME_IN_LONG, 0);
		if (serverTime > new Date().getTime())
		{
			totalTimerTime = serverTime - new Date().getTime();
			timer = new Timer();
			timer.schedule(new DelayTimer(), AppConstant.ONE_SECOND, AppConstant.ONE_SECOND);
		}
		else
		{
			YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_RESUME_OTP_VIEW, null);
		}
	}

	private class DelayTimer extends TimerTask
	{

		@Override
		public void run()
		{
			runOnUiThread(() -> {
				if (totalTimerTime > 0)
				{
					displayData();
				}
				else
				{
					timer.cancel();
					YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_RESUME_OTP_VIEW, null);
				}
			});
		}

	}

	private void displayData()
	{
		long remainingTime = totalTimerTime;
		final int HOUR = 3600000, MINUTE = 60000;
		totalTimerTime -= AppConstant.ONE_SECOND; // decreasing one second
		int hour = (int) remainingTime / HOUR;
		if (hour > 0)
		{
			remainingTime -= HOUR;
		}
		int minute = (int) (remainingTime % HOUR) / MINUTE;
		if (minute > 0)
		{
			remainingTime -= MINUTE;
		}
		int seconds = (int) (((remainingTime % HOUR) % MINUTE) / AppConstant.ONE_SECOND);
		setTimeValues(hour, minute, seconds);
	}

	private void setTimeValues(int hour, int minute, int seconds)
	{
		if (hour < 10)
		{
			hourText.setText("0" + hour);
		}
		else
		{
			hourText.setText("" + hour);
		}
		if (minute < 10)
		{
			minuteText.setText("0" + minute);
		}
		else
		{
			minuteText.setText("" + minute);
		}
		if (seconds < 10)
		{
			secondText.setText("0" + seconds);
		}
		else
		{
			secondText.setText("" + seconds);
		}
	}

	protected void postOpenAppEvent()
	{
		if (!getAppUser().isActive())
		{
			// The user account is apparently deleted
			return;
		}
		DataLoadListenerImpl listenerWrapper = new DataLoadListenerImpl((result) -> handlePostAppEventSuccess(result), (error) -> handlePostAppEventFailure(error), null);
		String yonaPassword = YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword();
		APIManager.getInstance().getYonaManager().postOpenAppEvent(getAppUser().getPostOpenAppEventLink(), yonaPassword, listenerWrapper);
	}

	protected abstract Object handlePostAppEventSuccess(Object result);

	private Object handlePostAppEventFailure(Object errorMessage)
	{
		String errorMessageStr = "";
		if (errorMessage instanceof ErrorMessage)
		{
			errorMessageStr = ((ErrorMessage) errorMessage).getMessage();
		}
		else if (errorMessage != null)
		{
			errorMessageStr = errorMessage.toString();
		}
		displayErrorMessageToUser(errorMessageStr);
		return null; // dummy return value, to allow use a data error handler
	}


	private void displayErrorMessageToUser(String errorMessageStr)
	{
		runOnUiThread(() -> {
			if (!this.isFinishing())
			{
				final AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(getString(R.string.generic_alert_title));
				builder.setMessage(errorMessageStr);
				builder.setPositiveButton(android.R.string.ok, (DialogInterface dialog, int which) -> navigateToNextScreen());
				builder.setCancelable(false);
				builder.create().show();
			}
		});
	}

	protected abstract void navigateToNextScreen();
}
