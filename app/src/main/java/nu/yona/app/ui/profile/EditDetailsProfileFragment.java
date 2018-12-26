/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.analytics.AnalyticsConstant;
import nu.yona.app.analytics.YonaAnalytics;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.Href;
import nu.yona.app.api.model.RegisterUser;
import nu.yona.app.api.model.User;
import nu.yona.app.api.utils.ServerErrorCode;
import nu.yona.app.customview.CustomAlertDialog;
import nu.yona.app.customview.YonaFontEditTextView;
import nu.yona.app.customview.YonaFontNumberTextView;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.customview.YonaPhoneWatcher;
import nu.yona.app.listener.DataLoadListenerImpl;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.ui.signup.OTPActivity;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.AppUtils;
import nu.yona.app.utils.PreferenceConstant;

import static nu.yona.app.YonaApplication.sharedAppDataState;
import static nu.yona.app.YonaApplication.sharedUserPreferences;

/**
 * Created by kinnarvasa on 10/05/16.
 */
public class EditDetailsProfileFragment extends BaseProfileFragment implements EventChangeListener
{
	private YonaFontEditTextView firstName, lastName, nickName;
	private YonaFontNumberTextView mobileNumber;
	private TextInputLayout firstNameLayout, lastNameLayout, nickNameLayout, mobileNumberLayout;
	private ImageView updateProfileImage;
	private CircleImageView profileImage;
	private View.OnClickListener changeProfileImageClickListener;
	private TextWatcher textWatcher;
	private String oldUserNumber;
	private RegisterUser registerUser;
	private boolean isAdding;
	private YonaFontTextView profileImageTxt;
	private boolean isProfileUpdated;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.edit_profile_detail_fragment, null);
		setupActivityRootView(view);
		setupToolbar(view);
		changeProfileImageClickListener = (profileImageView) -> YonaActivity.getActivity().chooseImage();
		setupTextWatcher();
		inflateView(view);
		setHook(new YonaAnalytics.BackHook(AnalyticsConstant.BACK_FROM_EDIT_PROFILE));
		YonaApplication.getEventChangeManager().registerListener(this);
		return view;
	}

	private static void onFocusChange(View view, boolean hasFocus)
	{
		if (hasFocus)
		{
			((EditText) view).setSelection(((EditText) view).getText().length());
		}
	}

	private void setupActivityRootView(View view)
	{
		View activityRootView = view.findViewById(R.id.main_content);
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
			if (!isAdded())
			{
				return;
			}
			if (AppUtils.checkKeyboardOpen(activityRootView))
			{
				((YonaActivity) getActivity()).changeBottomTabVisibility(false);
			}
			else
			{
				((YonaActivity) getActivity()).changeBottomTabVisibility(true);
			}
		});
	}

	private void setupTextWatcher()
	{
		textWatcher = new TextWatcher()
		{
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				isAdding = count == 1;
				hideErrorMessages();
			}

			@Override
			public void afterTextChanged(Editable s)
			{
				if (s != null && s.length() > 0 && (s.length() == 1 || s.charAt(s.length() - 1) == ' ') && isAdding)
				{
					firstName.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
					lastName.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
					nickName.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
				}
			}
		};
	}

	private void inflateView(View view)
	{
		setupFirstNameLayout(view);
		setupLastNameLayout(view);
		setupNickNameLayout(view);
		setupMobileNumberLayout(view);
		profileImage = view.findViewById(R.id.profileImage);
		updateProfileImage = view.findViewById(R.id.updateProfileImage);
		profileImageTxt = view.findViewById(R.id.profileIcon);
		profileEditMode();
	}

	private void setupFirstNameLayout(View view)
	{
		firstNameLayout = view.findViewById(R.id.first_name_layout);
		firstName = view.findViewById(R.id.first_name);
		firstName.addTextChangedListener(textWatcher);
		firstName.setOnFocusChangeListener(EditDetailsProfileFragment::onFocusChange);
		firstNameLayout.setOnClickListener(v -> YonaActivity.getActivity().showKeyboard(firstName));
	}

	private void setupLastNameLayout(View view)
	{
		lastNameLayout = view.findViewById(R.id.last_name_layout);
		lastName = view.findViewById(R.id.last_name);
		lastName.addTextChangedListener(textWatcher);
		lastName.setOnFocusChangeListener(EditDetailsProfileFragment::onFocusChange);
		lastNameLayout.setOnClickListener(v -> YonaActivity.getActivity().showKeyboard(lastName));
	}

	private void setupNickNameLayout(View view)
	{
		nickNameLayout = view.findViewById(R.id.nick_name_layout);
		nickName = view.findViewById(R.id.nick_name);
		nickName.addTextChangedListener(textWatcher);
		nickName.setOnFocusChangeListener(EditDetailsProfileFragment::onFocusChange);
		nickNameLayout.setOnClickListener(v -> YonaActivity.getActivity().showKeyboard(nickName));
	}

	private void setupMobileNumberLayout(View view)
	{
		mobileNumberLayout = view.findViewById(R.id.mobile_number_layout);
		mobileNumber = view.findViewById(R.id.mobile_number);
		mobileNumber.requestFocus();
		YonaActivity.getActivity().showKeyboard(mobileNumber);
		mobileNumber.addTextChangedListener(new YonaPhoneWatcher(mobileNumber, getActivity(), null));
		mobileNumberLayout.setOnClickListener(v -> YonaActivity.getActivity().showKeyboard(mobileNumber));
		mobileNumber.setOnEditorActionListener((v, actionId, event) -> {
			if (actionId == EditorInfo.IME_ACTION_DONE)
			{
				goToNext();
			}
			return false;
		});
	}


	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		YonaApplication.getEventChangeManager().unRegisterListener(this);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		setTitleAndIcon();
	}

	private void goToNext()
	{
		if (validateFields() && userDetailsChanged())
		{
			YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.SCREEN_EDIT_PROFILE, AnalyticsConstant.SAVE);
			updateUserProfile();
		}
		else if (isProfileUpdated)
		{
			YonaActivity.getActivity().onBackPressed();
			YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_USER_UPDATE, sharedAppDataState.getUser());
		}
		else
		{
			YonaActivity.getActivity().onBackPressed();
		}
	}

	private static boolean editTextEquals(EditText editText, String value)
	{
		return editText.getText().toString().equals(value);
	}

	private boolean userDetailsChanged()
	{
		User loggedInUser = sharedAppDataState.getUser();
		return !(editTextEquals(firstName, loggedInUser.getFirstName()) &&
				editTextEquals(lastName, loggedInUser.getLastName()) &&
				editTextEquals(nickName, loggedInUser.getNickname()) &&
				editTextEquals(mobileNumber, loggedInUser.getMobileNumber()));
	}


	private void setTitleAndIcon()
	{
		new Handler().postDelayed(() -> {
			profileCircleImageView.setVisibility(View.GONE);
			toolbarTitle.setText(getString(R.string.edit_profile));
			rightIcon.setVisibility(View.VISIBLE);
			rightIcon.setImageResource(R.drawable.icn_create);
			rightIcon.setOnClickListener(v -> goToNext());
		}, AppConstant.TIMER_DELAY_HUNDRED);
	}

	private void profileEditMode()
	{
		profileImage.setOnClickListener(changeProfileImageClickListener);
		updateProfileImage.setOnClickListener(changeProfileImageClickListener);
		profileImageTxt.setBackground(ContextCompat.getDrawable(YonaActivity.getActivity(), R.drawable.bg_big_friend_round));
		setupUserDetailsInUI();
		Href userPhoto = sharedAppDataState.getUser().getLinks().getUserPhoto();
		if (userPhoto != null)
		{
			displayProfileImage(userPhoto.getHref());
		}
	}

	private void setupUserDetailsInUI()
	{
		firstName.setText(TextUtils.isEmpty(sharedAppDataState.getUser().getFirstName()) ? getString(R.string.blank) : sharedAppDataState.getUser().getFirstName());
		lastName.setText(TextUtils.isEmpty(sharedAppDataState.getUser().getLastName()) ? getString(R.string.blank) : sharedAppDataState.getUser().getLastName());
		nickName.setText(TextUtils.isEmpty(sharedAppDataState.getUser().getNickname()) ? getString(R.string.blank) : sharedAppDataState.getUser().getNickname());
		String number = sharedAppDataState.getUser().getMobileNumber();
		if (!TextUtils.isEmpty(number))
		{
			oldUserNumber = number;
		}
		mobileNumber.setText(number);
		firstName.requestFocus();
	}

	private boolean validateFields()
	{
		String number = mobileNumber.getText().toString();
		String phonenumber = number.replaceAll(getString(R.string.space), getString(R.string.blank));
		if (!validateDataInEditTextView())
		{
			return false;
		}
		else if (!APIManager.getInstance().getAuthenticateManager().isMobileNumberValid(phonenumber))
		{
			mobileNumberLayout.setErrorEnabled(true);
			mobileNumberLayout.setError(getString(R.string.enternumbervalidation));
			YonaActivity.getActivity().showKeyboard(mobileNumber);
			mobileNumber.requestFocus();
			return false;
		}
		return true;
	}

	private boolean validateDataInEditTextView()
	{
		if (validateYonaFontEditTextView(firstName))
		{
			showErrorMessageToUserUponInvalidData(firstNameLayout, firstName, R.string.enternamevalidation);
			return false;
		}
		if (validateYonaFontEditTextView(lastName))
		{
			showErrorMessageToUserUponInvalidData(lastNameLayout, lastName, R.string.enternamevalidation);
			return false;
		}
		if (validateYonaFontEditTextView(nickName))
		{
			showErrorMessageToUserUponInvalidData(nickNameLayout, nickName, R.string.enternicknamevalidation);
			return false;
		}
		return true;
	}

	private boolean validateYonaFontEditTextView(YonaFontEditTextView yonaFontEditTextView)
	{
		return !APIManager.getInstance().getAuthenticateManager().validateText(yonaFontEditTextView.getText().toString());
	}

	private void showErrorMessageToUserUponInvalidData(TextInputLayout textInputLayout, YonaFontEditTextView yonaFontEditTextView, int resId)
	{
		textInputLayout.setErrorEnabled(true);
		textInputLayout.setError(getString(resId));
		YonaActivity.getActivity().showKeyboard(yonaFontEditTextView);
		yonaFontEditTextView.requestFocus();
	}

	private void hideErrorMessages()
	{
		firstNameLayout.setError(null);
		lastNameLayout.setError(null);
		nickNameLayout.setError(null);
		mobileNumberLayout.setError(null);
	}

	private void updateUserProfile()
	{
		if (getActivity() == null)
		{
			return;
		}
		registerUser = new RegisterUser();
		registerUser.setFirstName(firstName.getText().toString());
		registerUser.setLastName(lastName.getText().toString());
		registerUser.setNickName(nickName.getText().toString());
		String number = mobileNumber.getText().toString();
		registerUser.setMobileNumber(number.replace(" ", ""));
		YonaActivity.getActivity().showLoadingView(true, null);
		DataLoadListenerImpl dataLoadListener = new DataLoadListenerImpl((result) -> redirectToNextPage(), result -> showError(result), null);
		APIManager.getInstance().getAuthenticateManager().registerUser(registerUser, true, dataLoadListener);
	}

	private Object redirectToNextPage()
	{
		YonaActivity.getActivity().showLoadingView(false, null);
		if (sharedAppDataState.getUser() != null && oldUserNumber.equalsIgnoreCase(sharedAppDataState.getUser().getMobileNumber()))
		{
			YonaActivity.getActivity().onBackPressed();
			YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_USER_UPDATE, sharedAppDataState.getUser());
		}
		else
		{
			showMobileVerificationScreen(null);
		}
		return null; // Dummy return value to allow use as data load handler
	}

	@Override
	public void onStateChange(int eventType, final Object payload)
	{
		switch (eventType)
		{
			case EventChangeManager.EVENT_RECEIVED_PHOTO:
				displayProfileImage((String) payload);
				isProfileUpdated = true;
				break;
			default:
				break;
		}
	}

	private void displayProfileImage(String path)
	{
		Picasso.with(getContext()).load(path).noFade().into(profileImage);
		profileImage.setVisibility(View.VISIBLE);
	}

	private Object showError(Object errorMessage)
	{
		ErrorMessage message = (ErrorMessage) errorMessage;
		YonaActivity.getActivity().showLoadingView(false, null);
		if (message.getCode() != null && message.getCode().equalsIgnoreCase(ServerErrorCode.USER_EXIST_ERROR))
		{
			CustomAlertDialog.show(YonaActivity.getActivity(), getString(R.string.useralreadyregister), getString(R.string.ok), (dialog, which) -> dialog.dismiss());
		}
		else
		{
			Snackbar.make(YonaActivity.getActivity().findViewById(android.R.id.content), message.getMessage(), Snackbar.LENGTH_LONG).show();
		}
		return null; // Dummy return value to allow use as data load handler
	}

	private void showMobileVerificationScreen(Bundle bundle)
	{
		removeStoredPassCode();
		Intent intent = new Intent(YonaActivity.getActivity(), OTPActivity.class);
		if (bundle != null)
		{
			intent.putExtras(bundle);
		}
		startActivity(intent);
		YonaActivity.getActivity().finish();
	}

	private void removeStoredPassCode()
	{
		SharedPreferences.Editor yonaPref = sharedUserPreferences.edit();
		yonaPref.putBoolean(PreferenceConstant.STEP_OTP, false);
		yonaPref.putBoolean(PreferenceConstant.PROFILE_OTP_STEP, true);
		yonaPref.commit();
	}


	@Override
	public String getAnalyticsCategory()
	{
		return AnalyticsConstant.SCREEN_EDIT_PROFILE;
	}
}
