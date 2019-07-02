/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.signup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.analytics.AnalyticsConstant;
import nu.yona.app.analytics.YonaAnalytics;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.customview.YonaFontEditTextView;
import nu.yona.app.customview.YonaFontNumberTextView;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.customview.YonaPhoneWatcher;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.utils.AppUtils;
import nu.yona.app.utils.MobileNumberFormatter;

import static nu.yona.app.YonaApplication.getSharedAppDataState;

/**
 * Created by kinnarvasa on 25/03/16.
 */
public class StepTwoFragment extends BaseFragment implements EventChangeListener
{


	private YonaFontNumberTextView mobileNumber, countryCode;
	private YonaFontEditTextView nickName;
	private TextInputLayout nickNameLayout;
	private LinearLayout mobileNumberLayout;
	private YonaFontTextView mobileErrorTextview;
	private SignupActivity activity;
	private AppBarLayout appbar;
	private boolean isAdding;

	private final TextWatcher watcher = new TextWatcher()
	{
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after)
		{

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{
			isAdding = count == 1 ? true : false;
			nickName.setError(null);
			nickNameLayout.setErrorEnabled(false);
		}

		@Override
		public void afterTextChanged(Editable s)
		{
			if (s != null && s.length() > 0 && (s.length() == 1 || s.charAt(s.length() - 1) == ' ') && isAdding)
			{
				nickName.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
			}
		}
	};

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.signup_steptwo_fragment, null);

		activity = (SignupActivity) getActivity();

		((YonaFontTextView) view.findViewById(R.id.toolbar_title)).setText(R.string.join);

		mobileNumber = (YonaFontNumberTextView) view.findViewById(R.id.mobileNumber);
		countryCode = (YonaFontNumberTextView) view.findViewById(R.id.countryCode);
		nickName = (YonaFontEditTextView) view.findViewById(R.id.nick_name);
		nickName.addTextChangedListener(watcher);

		mobileNumberLayout = (LinearLayout) view.findViewById(R.id.mobile_number_layout);
		nickNameLayout = (TextInputLayout) view.findViewById(R.id.nick_name_layout);

		appbar = (AppBarLayout) view.findViewById(R.id.appbar);
		mobileErrorTextview = (YonaFontTextView) view.findViewById(R.id.mobile_error_text);

		if (activity.getDeepLinkUserInfo() != null && activity.getDeepLinkUserInfo().getMobileNumber() != null)
		{

			String number = activity.getDeepLinkUserInfo().getMobileNumber();
			try
			{
				Phonenumber.PhoneNumber numberProto = PhoneNumberUtil.getInstance().parse(number, "");
				String ccode = "+" + numberProto.getCountryCode(); // phone must begin with '+'
				countryCode.setText(ccode);
				mobileNumber.setText(number.substring(ccode.length(), number.length()));

			}
			catch (NumberParseException e)
			{
				AppUtils.reportException(StepTwoFragment.class, e, Thread.currentThread());
			}
		}
		else
		{
			countryCode.setText(R.string.country_code);
		}

		mobileNumber.requestFocus();
		activity.showKeyboard(mobileNumber);
		mobileNumber.addTextChangedListener(new YonaPhoneWatcher(mobileNumber, getActivity(), mobileErrorTextview));

		mobileNumberLayout.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				activity.showKeyboard(mobileNumber);
			}
		});

		nickNameLayout.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				activity.showKeyboard(nickName);
			}
		});

		nickName.setOnEditorActionListener(new EditText.OnEditorActionListener()
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
		YonaApplication.getEventChangeManager().registerListener(this);
		setHook(new YonaAnalytics.BackHook(AnalyticsConstant.BACK_FROM_REGISTRATION_STEP_TWO));
		return view;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		appbar.setExpanded(true);
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
		if (eventType == EventChangeManager.EVENT_SIGNUP_STEP_TWO_NEXT)
		{
			goToNext();
		}
	}

	private void goToNext()
	{
		String formattedNumber = MobileNumberFormatter.format(countryCode.getText().toString(), mobileNumber.getText().toString());
		if (validateMobileNumber(formattedNumber) && validateNickName())
		{
			YonaAnalytics.createTapEvent(getString(R.string.next));
			getSharedAppDataState().getRegisterUser().setMobileNumber(formattedNumber);
			getSharedAppDataState().getRegisterUser().setNickName(nickName.getText().toString());
			YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_SIGNUP_STEP_TWO_ALLOW_NEXT, null);
		}
	}

	private boolean validateMobileNumber(String number)
	{
		if (!APIManager.getInstance().getAuthenticateManager().isMobileNumberValid(number))
		{
			mobileErrorTextview.setVisibility(View.VISIBLE);
			mobileErrorTextview.setText(getString(R.string.enternumbervalidation));
			activity.showKeyboard(mobileNumber);
			mobileNumber.requestFocus();
			return false;
		}
		return true;
	}

	private boolean validateNickName()
	{
		if (!APIManager.getInstance().getAuthenticateManager().validateText(nickName.getText().toString()))
		{
			nickNameLayout.setErrorEnabled(true);
			nickNameLayout.setError(getString(R.string.enternicknamevalidation));
			activity.showKeyboard(nickName);
			nickName.requestFocus();
			return false;
		}
		return true;
	}

	@Override
	public String getAnalyticsCategory()
	{
		return AnalyticsConstant.REGISTRATION_STEP_TWO;
	}
}
