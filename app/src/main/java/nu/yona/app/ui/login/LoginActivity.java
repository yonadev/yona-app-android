/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.analytics.AnalyticsConstant;
import nu.yona.app.analytics.YonaAnalytics;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.customview.YonaFontEditTextView;
import nu.yona.app.customview.YonaFontNumberTextView;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.customview.YonaPhoneWatcher;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.BaseActivity;
import nu.yona.app.ui.LaunchActivity;
import nu.yona.app.ui.pincode.PasscodeActivity;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.AppUtils;
import nu.yona.app.utils.PreferenceConstant;

/**
 * Created by kinnarvasa on 13/04/16.
 * This Activity is used only when user is trying to add another device.
 */
public class LoginActivity extends BaseActivity implements EventChangeListener
{

	private final InputFilter filter = new InputFilter()
	{
		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
		{
			String blockCharacterSet = "~#^&|$%*!@/()-'\":;,?{}=!$^';,?×÷<>{}€£¥₩%~`¤♡♥_|《》¡¿°•○●□■◇◆♧♣▲▼▶◀↑↓←→☆★▪:-);-):-(:'(:";
			if (source != null && blockCharacterSet.contains(("" + source)))
			{
				return "";
			}
			return null;
		}
	};
	private YonaFontNumberTextView mobileNumber, countryCode;
	private YonaFontEditTextView passcode;
	private TextInputLayout passcodeLayout;
	private LinearLayout mobileNumberLayout;
	private YonaFontTextView mobileErrorTextview;
	private final TextWatcher watcher = new TextWatcher()
	{
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after)
		{

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{
			passcodeLayout.setError(null);
		}

		@Override
		public void afterTextChanged(Editable s)
		{

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);

		((YonaFontTextView) findViewById(R.id.toolbar_title)).setText(R.string.inloggin);

		mobileNumberLayout = (LinearLayout) findViewById(R.id.mobile_number_layout);
		passcodeLayout = (TextInputLayout) findViewById(R.id.passcode_layout);
		mobileErrorTextview = (YonaFontTextView) findViewById(R.id.mobile_error_text);

		mobileNumber = (YonaFontNumberTextView) findViewById(R.id.mobileNumber);
		countryCode = (YonaFontNumberTextView) findViewById(R.id.countryCode);
		passcode = (YonaFontEditTextView) findViewById(R.id.passcode);
		passcode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(AppConstant.ADD_DEVICE_PASSWORD_CHAR_LIMIT), filter});
		passcode.addTextChangedListener(watcher);

		mobileNumberLayout.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				showKeyboard(mobileNumber);
			}
		});

		passcodeLayout.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				showKeyboard(passcode);
			}
		});

		passcode.setOnEditorActionListener(new EditText.OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
			{
				if (actionId == EditorInfo.IME_ACTION_DONE)
				{
					goToNext();
				}
				return false;
			}
		});

		findViewById(R.id.next).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				goToNext();
			}
		});

		countryCode.setText(R.string.country_code);
		mobileNumber.requestFocus();
		mobileNumber.addTextChangedListener(new YonaPhoneWatcher(mobileNumber, this, mobileErrorTextview));
		YonaApplication.getEventChangeManager().registerListener(this);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		YonaApplication.getEventChangeManager().unRegisterListener(this);
	}

	@Override
	public void onBackPressed()
	{
		doBack();
	}

	private void doBack()
	{
		YonaAnalytics.createTapEvent(AnalyticsConstant.BACK_FROM_LOGIN_SCREEN);
		startActivity(new Intent(LoginActivity.this, LaunchActivity.class));
		finish();
	}

	private boolean validateMobileNumber(String number)
	{
		String phonenumber = number.replace(" ", "");
		if (!APIManager.getInstance().getDeviceManager().validateMobileNumber(phonenumber))
		{
			mobileErrorTextview.setVisibility(View.VISIBLE);
			mobileErrorTextview.setText(getString(R.string.enternumbervalidation));
			showKeyboard(mobileNumber);
			mobileNumber.requestFocus();
			return false;
		}
		return true;
	}

	private boolean validatePasscode(String passcodeStr)
	{
		if (!APIManager.getInstance().getDeviceManager().validatePasscode(passcodeStr))
		{
			passcodeLayout.setErrorEnabled(true);
			passcodeLayout.setError(getString(R.string.enterpasscode));
			showKeyboard(passcode);
			passcode.requestFocus();
			return false;
		}
		return true;
	}

	/**
	 * Do login on server in background.
	 */
	private void doLogin()
	{
		showLoadingView(true, null);
		APIManager.getInstance().getDeviceManager().validateDevice(passcode.getText().toString(), countryCode.getText().toString() + mobileNumber.getText().toString().replaceAll(" ", ""), new DataLoadListener()
		{
			@Override
			public void onDataLoad(Object result)
			{
				showLoadingView(false, null);
				AppUtils.downloadCertificates();
				AppUtils.downloadVPNProfile();
				updateData();
				showPasscodeScreen();
			}

			@Override
			public void onError(Object errorMessage)
			{
				showLoadingView(false, null);
				ErrorMessage message = (ErrorMessage) errorMessage;
				showLoadingView(false, null);
				Snackbar.make(findViewById(android.R.id.content), message.getMessage(), Snackbar.LENGTH_LONG).show();
			}
		});
	}

	private void showPasscodeScreen()
	{
		YonaApplication.getEventChangeManager().getSharedPreference().getUserPreferences().edit().putBoolean(PreferenceConstant.STEP_CHALLENGES, true).commit();
		SharedPreferences.Editor editor = YonaApplication.getEventChangeManager().getSharedPreference().getUserPreferences().edit();
		editor.putBoolean(PreferenceConstant.STEP_REGISTER, true);
		editor.putBoolean(PreferenceConstant.STEP_OTP, true);
		editor.putBoolean(PreferenceConstant.STEP_PASSCODE, true);
		editor.commit();
		startActivity(new Intent(LoginActivity.this, PasscodeActivity.class));
		finish();
	}

	private void goToNext()
	{
		if (validateMobileNumber(countryCode.getText().toString() + mobileNumber.getText().toString())
				&& validatePasscode(passcode.getText().toString()))
		{
			YonaAnalytics.createTapEvent(getString(R.string.next));
			doLogin();
		}
	}

	private void updateData()
	{
		APIManager.getInstance().getActivityCategoryManager().getActivityCategoriesById(null);
		APIManager.getInstance().getGoalManager().getUserGoal(new DataLoadListener()
		{
			@Override
			public void onDataLoad(Object result)
			{
				// We don't need to handle this.
			}

			@Override
			public void onError(Object errorMessage)
			{
				// We don't need to handle this.
			}
		});
	}

	@Override
	public void onStateChange(int eventType, Object object)
	{
		switch (eventType)
		{
			case EventChangeManager.EVENT_CLOSE_ALL_ACTIVITY_EXCEPT_LAUNCH:
				finish();
				break;
			default:
				break;
		}
	}

	@Override
	public String getAnalyticsCategory()
	{
		return AnalyticsConstant.LOGIN_SCREEN;
	}

}
