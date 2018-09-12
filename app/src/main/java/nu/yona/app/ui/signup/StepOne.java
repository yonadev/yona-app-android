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
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.analytics.AnalyticsConstant;
import nu.yona.app.analytics.YonaAnalytics;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.model.YonaUser;
import nu.yona.app.customview.YonaFontEditTextView;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.BaseFragment;

/**
 * Created by kinnarvasa on 25/03/16.
 */
public class StepOne extends BaseFragment implements EventChangeListener
{

	private TextInputLayout firstNameLayout, lastNameLayout;
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
			firstNameLayout.setError(null);
			lastNameLayout.setError(null);
		}

		@Override
		public void afterTextChanged(Editable s)
		{
			if (s != null && s.length() > 0 && (s.length() == 1 || s.charAt(s.length() - 1) == ' ') && isAdding)
			{
				firstName.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
				lastName.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
			}
		}
	};

	private YonaFontEditTextView firstName, lastName;
	private SignupActivity activity;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.signup_stepone_fragment, null);

		activity = (SignupActivity) getActivity();

		((YonaFontTextView) view.findViewById(R.id.toolbar_title)).setText(R.string.join);

		firstNameLayout = (TextInputLayout) view.findViewById(R.id.first_name_layout);
		lastNameLayout = (TextInputLayout) view.findViewById(R.id.last_name_layout);

		firstName = (YonaFontEditTextView) view.findViewById(R.id.first_name);
		firstName.addTextChangedListener(watcher);

		lastName = (YonaFontEditTextView) view.findViewById(R.id.last_name);
		lastName.addTextChangedListener(watcher);

		firstNameLayout.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				activity.showKeyboard(firstName);
			}
		});

		lastNameLayout.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				activity.showKeyboard(lastName);
			}
		});

		lastName.setOnEditorActionListener(new EditText.OnEditorActionListener()
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

		appbar = (AppBarLayout) view.findViewById(R.id.appbar);

		YonaFontTextView privacyPolicy = (YonaFontTextView) view.findViewById(R.id.privacyPolicy);
		privacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());

		YonaApplication.getEventChangeManager().registerListener(this);
		setHook(new YonaAnalytics.BackHook(AnalyticsConstant.BACK_FROM_REGISTRATION_STEP_ONE));
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
		if (eventType == EventChangeManager.EVENT_SIGNUP_STEP_ONE_NEXT)
		{
			goToNext();
		}
	}

	private void goToNext()
	{
		if (validateFirstName() && validateLastName())
		{
			YonaAnalytics.createTapEvent(getString(R.string.next));
			YonaApplication.getEventChangeManager().getDataState().getRegisterUser().setFirstName(firstName.getText().toString());
			YonaApplication.getEventChangeManager().getDataState().getRegisterUser().setLastName(lastName.getText().toString());
			YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_SIGNUP_STEP_ONE_ALLOW_NEXT, null);
		}
	}

	private boolean validateFirstName()
	{
		if (!APIManager.getInstance().getAuthenticateManager().validateText(firstName.getText().toString()))
		{
			firstNameLayout.setErrorEnabled(true);
			firstNameLayout.setError(getString(R.string.enterfirstnamevalidation));
			activity.showKeyboard(firstName);
			firstName.requestFocus();
			return false;
		}
		return true;
	}

	private boolean validateLastName()
	{
		if (!APIManager.getInstance().getAuthenticateManager().validateText(lastName.getText().toString()))
		{
			lastNameLayout.setErrorEnabled(true);
			lastNameLayout.setError(getString(R.string.enterlastnamevalidation));
			activity.showKeyboard(lastName);
			lastName.requestFocus();
			return false;
		}
		return true;
	}

	@Override
	public String getAnalyticsCategory()
	{
		return AnalyticsConstant.REGISTRATION_STEP_ONE;
	}

	public void onDeepLinkDataReceived(YonaUser user)
	{
		if (user != null)
		{
			firstName.setText(user.getFirstName() != null ? user.getFirstName() : "");
			lastName.setText(user.getLastName() != null ? user.getLastName() : "");
		}
	}
}
