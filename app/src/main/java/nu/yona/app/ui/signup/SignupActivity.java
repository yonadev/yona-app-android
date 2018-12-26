/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.signup;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.analytics.YonaAnalytics;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.YonaUser;
import nu.yona.app.api.utils.ServerErrorCode;
import nu.yona.app.customview.CustomAlertDialog;
import nu.yona.app.customview.YonaFontButton;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.BaseActivity;
import nu.yona.app.ui.LaunchActivity;
import nu.yona.app.utils.AppConstant;

import static nu.yona.app.YonaApplication.sharedAppDataState;

/**
 * Created by kinnarvasa on 25/03/16.
 */
public class SignupActivity extends BaseActivity implements EventChangeListener
{

	private StepOne stepOne;
	private StepTwoFragment stepTwoFragment;
	private int SIGNUP_STEP = 0;
	private YonaFontButton prevButton;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup_layout);

		stepOne = new StepOne();
		stepTwoFragment = new StepTwoFragment();

		YonaFontButton nextButton = (YonaFontButton) findViewById(R.id.next);
		prevButton = (YonaFontButton) findViewById(R.id.previous);

		loadSteopOne();

		nextButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (SIGNUP_STEP == 0)
				{
					YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_SIGNUP_STEP_ONE_NEXT, null);
				}
				else
				{
					YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_SIGNUP_STEP_TWO_NEXT, null);
				}
			}
		});
		YonaApplication.getEventChangeManager().registerListener(this);
		findViewById(R.id.previous).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				doBack();
			}
		});

		readDeepLinkData(getIntent().getExtras().getString(AppConstant.DEEP_LINK));
	}

	@Override
	public void onResume()
	{
		super.onResume();
	}

	@Override
	public void onBackPressed()
	{
		doBack();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		YonaApplication.getEventChangeManager().unRegisterListener(this);
	}

	private void doBack()
	{
		if (SIGNUP_STEP == 1)
		{
			YonaAnalytics.createTapEvent(getString(R.string.previous));
			loadSteopOne();
		}
		else
		{
			startActivity(new Intent(this, LaunchActivity.class));
			finish();
		}
	}

	private void loadSteopOne()
	{
		SIGNUP_STEP = 0;
		prevButton.setVisibility(View.GONE);
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
		fragmentTransaction.replace(R.id.fragment_container, stepOne);
		fragmentTransaction.commit();
	}

	private void loadSteopTwo()
	{
		SIGNUP_STEP = 1;
		prevButton.setVisibility(View.VISIBLE);
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.fragment_container, stepTwoFragment);
		fragmentTransaction.commit();
	}

	private void doRegister()
	{
		showLoadingView(true, null);
		if (getIntent() != null && getIntent().getExtras() != null && !TextUtils.isEmpty(getIntent().getExtras().getString(AppConstant.URL)))
		{
			APIManager.getInstance().getAuthenticateManager().registerUser(getIntent().getExtras().getString(AppConstant.URL), sharedAppDataState.getRegisterUser(), new DataLoadListener()
			{
				@Override
				public void onDataLoad(Object result)
				{
					showLoadingView(false, null);
					showMobileVerificationScreen(null);
				}

				@Override
				public void onError(Object errorMessage)
				{
					showError(errorMessage);
				}
			});
		}
		else
		{
			APIManager.getInstance().getAuthenticateManager().registerUser(sharedAppDataState.getRegisterUser(), false, new DataLoadListener()
			{
				@Override
				public void onDataLoad(Object result)
				{
					showLoadingView(false, null);
					showMobileVerificationScreen(null);
				}

				@Override
				public void onError(Object errorMessage)
				{
					showError(errorMessage);
				}
			});
		}
	}

	private void showAlertForReRegisteruser(String title)
	{
		CustomAlertDialog.show(this, title, getString(R.string.useroverride, sharedAppDataState.getRegisterUser().getMobileNumber()), getString(R.string.yes), getString(R.string.no), new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialogInterface, int i)
			{
				OverrideUser();
			}
		}, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});
	}

	/**
	 * When user is already registered with same number and want to override same user.
	 */
	private void OverrideUser()
	{
		showLoadingView(true, null);
		APIManager.getInstance().getAuthenticateManager().requestUserOverride(sharedAppDataState.getRegisterUser().getMobileNumber(), new DataLoadListener()
		{

			@Override
			public void onDataLoad(Object result)
			{
				showLoadingView(false, null);
				Bundle bundle = new Bundle();
				bundle.putSerializable(AppConstant.USER, sharedAppDataState.getRegisterUser());
				showMobileVerificationScreen(bundle);
			}

			@Override
			public void onError(Object errorMessage)
			{
				showLoadingView(false, null);
				showError(errorMessage);
			}
		});
	}

	@Override
	public void onStateChange(int eventType, Object object)
	{
		switch (eventType)
		{
			case EventChangeManager.EVENT_SIGNUP_STEP_ONE_ALLOW_NEXT:
				loadSteopTwo();
				break;
			case EventChangeManager.EVENT_SIGNUP_STEP_TWO_ALLOW_NEXT:
				doRegister();
				break;
			case EventChangeManager.EVENT_CLOSE_ALL_ACTIVITY_EXCEPT_LAUNCH:
				finish();
				break;
			default:
				break;
		}
	}

	/**
	 * Show keyboard.
	 *
	 * @param editText the edit text
	 */
	@Override
	public void showKeyboard(EditText editText)
	{
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		inputMethodManager.toggleSoftInputFromWindow(editText.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
	}

	private void showMobileVerificationScreen(Bundle bundle)
	{
		Intent intent = new Intent(SignupActivity.this, OTPActivity.class);
		if (bundle != null)
		{
			intent.putExtras(bundle);
		}
		startActivity(intent);
	}

	private void showError(Object errorMessage)
	{
		showLoadingView(false, null);
		if (errorMessage instanceof ErrorMessage)
		{
			ErrorMessage message = (ErrorMessage) errorMessage;
			if (message.getCode() != null && (message.getCode().equalsIgnoreCase(ServerErrorCode.USER_EXIST_ERROR)
					|| message.getCode().equalsIgnoreCase(ServerErrorCode.ADD_BUDDY_USER_EXIST_ERROR)))
			{
				showAlertForReRegisteruser(message.getMessage());
			}
			else
			{
				Snackbar.make(findViewById(android.R.id.content), message.getMessage(), Snackbar.LENGTH_LONG).show();
			}
		}
		else
		{
			Snackbar.make(findViewById(android.R.id.content), (String) errorMessage, Snackbar.LENGTH_LONG).show();
		}
	}

	// Object will be read by @StepTwoFragment to display phone number read from deep link.
	private YonaUser deepLinkUserInfo = null;

	public YonaUser getDeepLinkUserInfo()
	{
		return deepLinkUserInfo;
	}

	/**
	 * q
	 * Following will request to read Deep link Data for requested URL.
	 *
	 * @param url request URL.
	 */
	private void readDeepLinkData(String url)
	{
		if (url == null)
		{
			return;
		}

		showLoadingView(true, null);
		if (AppConstant.URL != null)
		{

			APIManager.getInstance().getAuthenticateManager().readDeepLinkUserInfo(url, new DataLoadListener()
			{
				@Override
				public void onDataLoad(Object result)
				{
					showLoadingView(false, null);

					if (result != null)
					{
						deepLinkUserInfo = (YonaUser) result;
						stepOne.onDeepLinkDataReceived(deepLinkUserInfo);
					}
				}

				@Override
				public void onError(Object errorMessage)
				{
					showError(errorMessage);
				}
			});
		}
	}
}