/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.signup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;

import java.util.Date;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.analytics.AnalyticsConstant;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.RegisterUser;
import nu.yona.app.enums.IntentEnum;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.ui.pincode.BasePasscodeActivity;
import nu.yona.app.ui.pincode.PasscodeActivity;
import nu.yona.app.ui.pincode.PasscodeFragment;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.AppUtils;
import nu.yona.app.utils.PreferenceConstant;

/**
 * Created by kinnarvasa on 04/04/16.
 */
public class OTPActivity extends BasePasscodeActivity implements EventChangeListener
{

	private final SharedPreferences userPreferences = YonaApplication.getEventChangeManager().getSharedPreference().getUserPreferences();
	private PasscodeFragment otpFragment;
	private RegisterUser user;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		YonaApplication.getEventChangeManager().registerListener(this);
		loadOTPFragment();
		screenTitle = getString(R.string.join);
		screenType = AppConstant.OTP;
		if (getIntent() != null && getIntent().getExtras() != null)
		{
			user = (RegisterUser) getIntent().getExtras().getSerializable(AppConstant.USER);
		}
		updateScreenUI();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		if (userPreferences.getLong(PreferenceConstant.USER_WAIT_TIME_IN_LONG, 0) > new Date().getTime())
		{
			showTimer();
		}
		else
		{
			hideTimer();
		}
	}

	private void showTimer()
	{
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		showTimerToUser();
	}

	private void hideTimer()
	{
		hideTimerFromUser();
	}

	private void loadOTPFragment()
	{
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.blank_container, getOTPFragment());
		fragmentTransaction.commit();
	}

	private PasscodeFragment getOTPFragment()
	{
		if (otpFragment == null)
		{
			Bundle bPasscode = new Bundle();
			bPasscode.putInt(AppConstant.COLOR_CODE, ContextCompat.getColor(this, R.color.grape));
			bPasscode.putString(AppConstant.SCREEN_TYPE, AppConstant.OTP);
			bPasscode.putString(AppConstant.PASSCODE_SCREEN_NAME, AnalyticsConstant.PASSCODE_SCREEN);
			otpFragment = new PasscodeFragment();
			otpFragment.setArguments(bPasscode);
		}
		return otpFragment;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		YonaApplication.getEventChangeManager().unRegisterListener(this);
	}

	@Override
	public void onStateChange(int eventType, Object object)
	{
		switch (eventType)
		{
			case EventChangeManager.EVENT_PASSCODE_STEP_TWO:
				validateOTP(object.toString());
				break;
			case EventChangeManager.EVENT_OTP_RESEND:
				resendOTP();
				break;
			case EventChangeManager.EVENT_CLOSE_ALL_ACTIVITY_EXCEPT_LAUNCH:
				finish();
				break;
			case EventChangeManager.EVENT_RESUME_OTP_VIEW:
				onResume();
				break;
			default:
				break;
		}
	}

	/**
	 * @param otpString User's entered OTP
	 */
	private void validateOTP(final String otpString)
	{
		showLoadingView(true, null);
		APIManager.getInstance().getAuthenticateManager().verifyOTP(user, otpString, new DataLoadListener()
		{
			@Override
			public void onDataLoad(Object result)
			{
				AppUtils.downloadCertificates();
				AppUtils.downloadVPNProfile();
				getActivityCategories();
				showLoadingView(false, null);
				navigateToNextScreen();
			}

			@Override
			public void onError(Object errorMessage)
			{
				if (errorMessage instanceof ErrorMessage)
				{
					showLoadingView(false, null);
					Snackbar.make(findViewById(android.R.id.content), ((ErrorMessage) errorMessage).getMessage(), Snackbar.LENGTH_INDEFINITE)
							.setAction(getString(R.string.ok), new View.OnClickListener()
							{
								@Override
								public void onClick(View v)
								{
									otpFragment.resetDigit();
								}
							})
							.show();
				}
			}
		});
	}

	/**
	 * Get all activity categories
	 */
	private void getActivityCategories()
	{
		APIManager.getInstance().getActivityCategoryManager().getActivityCategoriesById(null);
	}

	private void resendOTP()
	{
		showLoadingView(true, null);
		otpFragment.resetDigit();
		APIManager.getInstance().getAuthenticateManager().resendOTP(new DataLoadListener()
		{
			@Override
			public void onDataLoad(Object result)
			{
				showLoadingView(false, null);
			}

			@Override
			public void onError(Object errorMessage)
			{
				showLoadingView(false, null);
			}
		});
	}

	/**
	 * Show Passcode Activity and clear back stack.
	 */
	private void showPasscodeScreen()
	{
		Intent intent = new Intent(OTPActivity.this, PasscodeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		finish();
	}

	private void showProfileScreen()
	{
		Intent intent = new Intent(OTPActivity.this, YonaActivity.class);
		intent.setAction(IntentEnum.ACTION_PROFILE.getActionString());
		intent.putExtra(AppConstant.FROM_LOGIN, true);
		ActivityCompat.startActivity(this, intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
		finish();
	}

	@Override
	protected void navigateToNextScreen()
	{
		if (userPreferences.getBoolean(PreferenceConstant.PROFILE_OTP_STEP, false))
		{
			userPreferences.edit().putBoolean(PreferenceConstant.STEP_OTP, true).apply();
			showProfileScreen();
		}
		else
		{
			showPasscodeScreen();
		}
	}

	@Override
	protected Object handlePostAppEventSuccess(Object result)
	{
		throw new UnsupportedOperationException("Implementation is delegated to PasscodeActivity and PinActivity");
	}
}
