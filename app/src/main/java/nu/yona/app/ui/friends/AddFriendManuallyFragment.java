/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.friends;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.analytics.AnalyticsConstant;
import nu.yona.app.analytics.YonaAnalytics;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.RegisterUser;
import nu.yona.app.customview.YonaFontButton;
import nu.yona.app.customview.YonaFontEditTextView;
import nu.yona.app.customview.YonaFontNumberTextView;
import nu.yona.app.customview.YonaPhoneWatcher;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.utils.Logger;
import nu.yona.app.utils.MobileNumberFormatter;

/**
 * Created by kinnarvasa on 27/04/16.
 */
public class AddFriendManuallyFragment extends BaseFragment implements EventChangeListener
{
	private YonaFontEditTextView firstName, lastName, email;
	private YonaFontNumberTextView mobileNumber;
	private TextInputLayout firstNameLayout, lastNameLayout, emailLayout, mobileNumberLayout;
	private boolean isAdding;

	private final TextWatcher textWatcher = new TextWatcher()
	{
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after)
		{

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{
			isAdding = count == 1 ? true : false;
			hideErrorMessage();
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
	private YonaFontButton addFriendButton;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.add_friend_manually_fragment, null);

		getView(view);
		addButtonListener();
		return view;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		YonaApplication.getEventChangeManager().registerListener(this);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		YonaApplication.getEventChangeManager().unRegisterListener(this);
	}

	private void getView(View view)
	{
		View activityRootView = view.findViewById(R.id.addfriendLayout);
		udpateBottomTabVisibility(activityRootView);
		firstName = (YonaFontEditTextView) view.findViewById(R.id.first_name);
		lastName = (YonaFontEditTextView) view.findViewById(R.id.last_name);
		email = (YonaFontEditTextView) view.findViewById(R.id.email);
		mobileNumber = (YonaFontNumberTextView) view.findViewById(R.id.mobile_number);

		firstName.addTextChangedListener(textWatcher);
		lastName.addTextChangedListener(textWatcher);
		email.addTextChangedListener(textWatcher);

		firstNameLayout = (TextInputLayout) view.findViewById(R.id.first_name_layout);
		lastNameLayout = (TextInputLayout) view.findViewById(R.id.last_name_layout);
		emailLayout = (TextInputLayout) view.findViewById(R.id.email_layout);
		mobileNumberLayout = (TextInputLayout) view.findViewById(R.id.mobile_number_layout);

		addFriendButton = (YonaFontButton) view.findViewById(R.id.addFriendButton);

		mobileNumber.addTextChangedListener(new YonaPhoneWatcher(mobileNumber, getActivity(), null));

		firstNameLayout.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				YonaActivity.getActivity().showKeyboard(firstName);
			}
		});

		lastNameLayout.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				YonaActivity.getActivity().showKeyboard(lastName);
			}
		});

		mobileNumberLayout.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				YonaActivity.getActivity().showKeyboard(mobileNumber);
			}
		});

		emailLayout.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				YonaActivity.getActivity().showKeyboard(email);
			}
		});

		mobileNumber.setOnEditorActionListener(new EditText.OnEditorActionListener()
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

		mobileNumber.setOnFocusChangeListener(new View.OnFocusChangeListener()
		{
			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				if (hasFocus && TextUtils.isEmpty(mobileNumber.getText()))
				{
					mobileNumber.setText(R.string.country_code);
					mobileNumber.setSelection(getString(R.string.country_code).length());
				}
			}
		});

		firstName.requestFocus();
	}

	private void goToNext()
	{
		if (validateFields())
		{
			addFriend();
		}
	}

	private void addButtonListener()
	{
		addFriendButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				goToNext();
			}
		});
	}

	/**
	 * Mobile number validation referenced here in ticket APPDEV-1055.
	 * http://jira.yona.nu/browse/APPDEV-1055?focusedCommentId=13497&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#comment-13497
	 *
	 * @return true if filed validation true.
	 */
	private boolean validateFields()
	{
		if (!APIManager.getInstance().getBuddyManager().validateText(firstName.getText().toString()))
		{
			updateErrorView(firstNameLayout, getString(R.string.enterfirstnamevalidation), firstName);
			return false;
		}
		else if (!APIManager.getInstance().getBuddyManager().validateText(lastName.getText().toString()))
		{
			updateErrorView(lastNameLayout, getString(R.string.enterlastnamevalidation), lastName);
			return false;
		}
		else if (!APIManager.getInstance().getBuddyManager().validateEmail(email.getText().toString()))
		{
			updateErrorView(emailLayout, getString(R.string.enteremailvalidation), email);
			return false;
		}
		else if (!APIManager.getInstance().getBuddyManager().validateMobileNumber(mobileNumber.getText().toString().trim()))
		{
			updateErrorView(mobileNumberLayout, getString(R.string.enternumbervalidation), mobileNumber);
			return false;
		}
		else if (APIManager.getInstance().getBuddyManager().validateMobileNumber(mobileNumber.getText().toString().trim()))
		{
			String number = mobileNumber.getText().toString().trim();

			if (number.startsWith(NUMBER_VALIDATION_START_PLUS_0))
			{
				updateErrorView(mobileNumberLayout, getString(R.string.entercountrycode), mobileNumber);
				return false;
			}

			if (number.substring(0, 2).equals(START_06) || number.substring(0, 1).equals(START_6) || number.substring(0, 1).equals(START_PLUS))
			{
				return true;
			}

			updateErrorView(mobileNumberLayout, getString(R.string.entercountrycode), mobileNumber);
			return false;
		}

		return true;
	}

	private final String NUMBER_VALIDATION_START_PLUS_0 = "+0";
	private final String START_06 = "06";
	private final String START_6 = "6";
	private final String START_PLUS = "+";
	private final String START_31 = "+31";
	private final String START_0 = "0";

	private void updateErrorView(final TextInputLayout mInputLayout, final String mErrorMsg, final YonaFontEditTextView mEditText)
	{
		mInputLayout.setErrorEnabled(true);
		mInputLayout.setError(mErrorMsg);
		mInputLayout.setFocusable(true);
		YonaActivity.getActivity().showKeyboard(mEditText);
		mEditText.requestFocus();
	}

	private void updateErrorView(final TextInputLayout mInputLayout, final String mErrorMsg, final YonaFontNumberTextView mEditText)
	{
		mInputLayout.setErrorEnabled(true);
		mInputLayout.setError(mErrorMsg);
		mInputLayout.setFocusable(true);
		YonaActivity.getActivity().showKeyboard(mEditText);
		mEditText.requestFocus();
	}

	private void addFriend()
	{
		((YonaActivity) getActivity()).displayLoadingView();
		YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.ADD_FRIEND, getString(R.string.invitefriend));
		String formattedMobileNumber = MobileNumberFormatter.format(mobileNumber.getText().toString());
		mobileNumber.setText(formattedMobileNumber);
		Logger.logi(AddFriendManuallyFragment.class, formattedMobileNumber);
		makeAddFriendAPIRequest(firstName.getText().toString(), lastName.getText().toString(), email.getText().toString(), formattedMobileNumber);
	}

	private void makeAddFriendAPIRequest(String firstName, String lastName, String email, String mobileNumber)
	{
		APIManager.getInstance().getBuddyManager().addBuddy(firstName, lastName, email, mobileNumber, new DataLoadListener()
		{
			@Override
			public void onDataLoad(Object result)
			{
				YonaActivity.getActivity().dismissLoadingView();
				YonaActivity.getActivity().onBackPressed();
				YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_UPDATE_FRIEND_OVERVIEW, null);
			}

			@Override
			public void onError(Object errorMessage)
			{
				ErrorMessage message = (ErrorMessage) errorMessage;
				YonaActivity.getActivity().dismissLoadingView();
				Snackbar.make(YonaActivity.getActivity().findViewById(android.R.id.content), message.getMessage(), Snackbar.LENGTH_LONG).show();
			}
		});
	}

	private void hideErrorMessage()
	{
		firstNameLayout.setError(null);
		lastNameLayout.setError(null);
		emailLayout.setError(null);
		mobileNumberLayout.setError(null);
	}

	@Override
	public void onStateChange(int eventType, Object object)
	{
		switch (eventType)
		{
			case EventChangeManager.EVENT_CONTAT_CHOOSED:
				if (object instanceof RegisterUser)
				{
					updateUser((RegisterUser) object);
				}
				break;
			default:
				break;
		}
	}

	private void updateUser(RegisterUser user)
	{
		firstName.setText(user.getFirstName());
		lastName.setText(user.getLastName());
		email.setText(user.getEmailAddress());
		if (!TextUtils.isEmpty(user.getMobileNumber()))
		{
			String number = user.getMobileNumber().replace(getString(R.string.space), getString(R.string.blank));
			mobileNumber.setText(number);
		}
		else if (user.getMultipleNumbers() != null && user.getMultipleNumbers().size() > 1)
		{
			showNumberChooser(user.getMultipleNumbers(), user);
		}
		else
		{
			mobileNumber.setText(getString(R.string.country_code));
		}
	}

	private void showNumberChooser(final List<String> numbers, final RegisterUser user)
	{
		final CharSequence[] charSequences = numbers.toArray(new CharSequence[numbers.size()]);
		AlertDialog.Builder builder = new AlertDialog.Builder(YonaActivity.getActivity());
		builder.setTitle(getString(R.string.choose_number, user.getFirstName()));
		builder.setItems(charSequences, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int item)
			{
				// Do something with the selection
				user.setMobileNumber(charSequences[item].toString());
				updateUser(user);
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
}
