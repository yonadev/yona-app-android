/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import io.fabric.sdk.android.Fabric;
import nu.yona.app.BuildConfig;
import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.analytics.AnalyticsConstant;
import nu.yona.app.analytics.YonaAnalytics;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.enums.EncryptionMethod;
import nu.yona.app.listener.DataLoadListenerImpl;
import nu.yona.app.ui.login.LoginActivity;
import nu.yona.app.ui.pincode.PasscodeActivity;
import nu.yona.app.ui.signup.OTPActivity;
import nu.yona.app.ui.signup.SignupActivity;
import nu.yona.app.ui.tour.YonaCarrouselActivity;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.PreferenceConstant;

import static nu.yona.app.YonaApplication.getSharedAppDataState;
import static nu.yona.app.YonaApplication.getSharedUserPreferences;
import static nu.yona.app.utils.PreferenceConstant.YONA_ENCRYPTION_METHOD;

public class LaunchActivity extends BaseActivity
{
	private Bundle bundle;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		initializeCrashlytics();
		validateYonaPasswordEncryption();
		setUpApplicationInitialView();
		navigateToValidActivity();
		setListeners();
		YonaAnalytics.trackCategoryScreen(AnalyticsConstant.LAUNCH_ACTIVITY, AnalyticsConstant.LAUNCH_ACTIVITY);
	}

	private void initializeCrashlytics()
	{
		// Set up Crashlytics, disabled for debug builds
		Crashlytics crashlyticsKit = new Crashlytics.Builder()
				.core(new CrashlyticsCore.Builder().disabled(BuildConfig.DISABLE_CRASHLYTICS).build())
				.build();
		Fabric.with(this, crashlyticsKit);
	}

	private void setUpApplicationInitialView()
	{
		setContentView(R.layout.launch_layout);
		bundle = new Bundle();
		if (getIntent() != null)
		{
			if (getIntent().getDataString() != null)
			{
				bundle.putString(AppConstant.URL, getIntent().getDataString());
				bundle.putString(AppConstant.DEEP_LINK, getIntent().getDataString());
				startNewActivity(bundle, SignupActivity.class);
				// and it will not launch tour for first time user and so can be marked true.
				getSharedUserPreferences().edit().putBoolean(PreferenceConstant.STEP_TOUR, true).commit();
				return;
			}
			else if (getIntent().getExtras() != null)
			{
				bundle = getIntent().getExtras();
			}
		}
	}

	private void navigateToValidActivity()
	{
		if (!getSharedUserPreferences().getBoolean(PreferenceConstant.STEP_TOUR, false))
		{
			startNewActivity(bundle, YonaCarrouselActivity.class);
		}
		else if (!getSharedUserPreferences().getBoolean(PreferenceConstant.STEP_REGISTER, false))
		{
			// We will skip here to load same activity
		}
		else if (getSharedUserPreferences().getBoolean(PreferenceConstant.STEP_REGISTER, false)
				&& !getSharedUserPreferences().getBoolean(PreferenceConstant.STEP_OTP, false))
		{
			startNewActivity(bundle, OTPActivity.class);
		}
		else if (!getSharedUserPreferences().getBoolean(PreferenceConstant.STEP_PASSCODE, false))
		{
			bundle.putInt(AppConstant.TITLE_BACKGROUND_RESOURCE, R.drawable.triangle_shadow_grape);
			bundle.putInt(AppConstant.COLOR_CODE, ContextCompat.getColor(LaunchActivity.this, R.color.grape));
			startNewActivity(bundle, PasscodeActivity.class);
		}
		else if (!TextUtils.isEmpty(getSharedUserPreferences().getString(PreferenceConstant.YONA_PASSCODE, "")))
		{
			startNewActivity(bundle, YonaActivity.class);
		}
	}


	private void setListeners()
	{
		findViewById(R.id.join).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				startNewActivity(bundle, SignupActivity.class);
			}
		});
		findViewById(R.id.login).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				startNewActivity(LoginActivity.class);
			}
		});
		findViewById(R.id.environmentSwitch).setOnLongClickListener(new View.OnLongClickListener()
		{
			@Override
			public boolean onLongClick(View v)
			{
				switchEnvironment();
				return true;
			}
		});
	}

	private void validateYonaPasswordEncryption()
	{
		// if App is older version and user is already logged in, upgrade the encryption.
		if ((getSharedUserPreferences().getInt(YONA_ENCRYPTION_METHOD, EncryptionMethod.INITIAL_METHOD.ordinal()) == EncryptionMethod.INITIAL_METHOD.ordinal()
				&& !TextUtils.isEmpty(getSharedUserPreferences().getString(PreferenceConstant.YONA_PASSCODE, ""))))
		{
			YonaApplication.getEventChangeManager().getSharedPreference().upgradeYonaPasswordEncryption();
		}
		SharedPreferences.Editor editor = getSharedUserPreferences().edit();
		editor.putInt(YONA_ENCRYPTION_METHOD, EncryptionMethod.ENHANCED_STILL_BASED_ON_SERIAL.ordinal());
		editor.commit();
	}

	/**
	 * This method is to run App on different environment as enterd by user.
	 */
	private void switchEnvironment()
	{
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View promptView = layoutInflater.inflate(R.layout.environment_switch, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setView(promptView);
		final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
		editText.setText(getSharedAppDataState().getServerUrl());
		alertDialogBuilder.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int id)
					{
						Log.d("Entered URL", "Hello, " + editText.getText());
						if (!(getSharedAppDataState().getServerUrl().equals(editText.getText().toString())))
						{
							validateEnvironment(editText.getText().toString());
						}
						else
						{
							Toast.makeText(LaunchActivity.this, YonaApplication.getAppContext().getString(R.string.same_environment_change), Toast.LENGTH_LONG).show();
						}
						dialog.dismiss();
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int id)
							{
								dialog.cancel();
							}
						});
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}

	boolean validateUrl(String enteredURL)
	{

		if (enteredURL.isEmpty())
		{
			return false;
		}
		else
		{
			try
			{
				new URL(enteredURL).toURI();
				return true;
			}
			catch (URISyntaxException e)
			{
				return false;
			}
			catch (MalformedURLException e)
			{
				return false;
			}
		}

	}

	void validateEnvironment(String newEnvironmentURL)
	{
		showLoadingView(true, null);
		String oldEnvironmentURL = getSharedAppDataState().getServerUrl();
		APIManager.getInstance().getActivityCategoryManager().updateNetworkAPIEnvironment(newEnvironmentURL);// initializes the network manager with the new host url from data state.
		DataLoadListenerImpl dataLoadListenerImpl = new DataLoadListenerImpl(((result) -> showEnvironmentSwitchSuccessMessageToUser(newEnvironmentURL, result)),
				((result) -> showEnvironmentSwitchFailureMessageToUser(oldEnvironmentURL, result)), null);
		APIManager.getInstance().getActivityCategoryManager().validateNewEnvironment(dataLoadListenerImpl);

	}

	public Object showEnvironmentSwitchSuccessMessageToUser(String newEnvironmentURL, Object result)
	{
		showLoadingView(false, null);
		Toast.makeText(LaunchActivity.this, YonaApplication.getAppContext().getString(R.string.new_environment_switch_success_msg) + newEnvironmentURL, Toast.LENGTH_LONG).show();
		return null;
	}

	public Object showEnvironmentSwitchFailureMessageToUser(String oldEnvironmentURL, Object errorMessage)
	{
		APIManager.getInstance().getActivityCategoryManager().updateNetworkAPIEnvironment(oldEnvironmentURL); // reverts the network manager with the old host url from data state.
		showLoadingView(false, null);
		Toast.makeText(LaunchActivity.this, YonaApplication.getAppContext().getString(R.string.environment_switch_error) + oldEnvironmentURL, Toast.LENGTH_LONG).show();
		return null;
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		finish();
	}
}
