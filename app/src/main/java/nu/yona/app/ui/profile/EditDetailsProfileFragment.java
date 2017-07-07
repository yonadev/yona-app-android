/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.ui.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.analytics.AnalyticsConstant;
import nu.yona.app.analytics.YonaAnalytics;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.RegisterUser;
import nu.yona.app.api.utils.ServerErrorCode;
import nu.yona.app.customview.CustomAlertDialog;
import nu.yona.app.customview.YonaFontEditTextView;
import nu.yona.app.customview.YonaFontNumberTextView;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.customview.YonaPhoneWatcher;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.ui.signup.OTPActivity;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.AppUtils;
import nu.yona.app.utils.PreferenceConstant;

/**
 * Created by kinnarvasa on 10/05/16.
 */
public class EditDetailsProfileFragment extends BaseProfileFragment implements EventChangeListener {
    private YonaFontEditTextView firstName, lastName, nickName;
    private YonaFontNumberTextView mobileNumber;
    private TextInputLayout firstnameLayout, lastNameLayout, nickNameLayout, mobileNumberLayout;
    private ImageView profileImage, updateProfileImage;
    private View.OnClickListener listener;
    private TextWatcher textWatcher;
    private String oldUserNumber;
    private RegisterUser user;
    private View.OnFocusChangeListener onFocusChangeListener;
    private boolean isAdding;
    private YonaFontTextView profileImageTxt;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_profile_detail_fragment, null);
        final View activityRootView = view.findViewById(R.id.main_content);

        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (isAdded()) {
                    if (AppUtils.checkKeyboardOpen(activityRootView)) {
                        ((YonaActivity) getActivity()).changeBottomTabVisibility(false);
                    } else {
                        ((YonaActivity) getActivity()).changeBottomTabVisibility(true);
                    }
                }
            }
        });

        setupToolbar(view);

        listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YonaActivity.getActivity().chooseImage();
            }
        };
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isAdding = count == 1 ? true : false;
                hideErrorMessages();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && s.length() > 0 && (s.length() == 1 || s.charAt(s.length() - 1) == ' ') && isAdding) {
                    firstName.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                    lastName.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                    nickName.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                }
            }
        };

        onFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    ((EditText) view).setSelection(((EditText) view).getText().length());
                }
            }
        };
        inflateView(view);

        setHook(new YonaAnalytics.BackHook(AnalyticsConstant.BACK_FROM_EDIT_PROFILE));

        YonaApplication.getEventChangeManager().registerListener(this);
        return view;
    }

    private void inflateView(View view) {

        firstnameLayout = (TextInputLayout) view.findViewById(R.id.first_name_layout);
        lastNameLayout = (TextInputLayout) view.findViewById(R.id.last_name_layout);
        nickNameLayout = (TextInputLayout) view.findViewById(R.id.nick_name_layout);
        mobileNumberLayout = (TextInputLayout) view.findViewById(R.id.mobile_number_layout);

        firstName = (YonaFontEditTextView) view.findViewById(R.id.first_name);
        firstName.addTextChangedListener(textWatcher);
        firstName.setOnFocusChangeListener(onFocusChangeListener);

        lastName = (YonaFontEditTextView) view.findViewById(R.id.last_name);
        lastName.addTextChangedListener(textWatcher);
        lastName.setOnFocusChangeListener(onFocusChangeListener);

        nickName = (YonaFontEditTextView) view.findViewById(R.id.nick_name);
        nickName.addTextChangedListener(textWatcher);
        nickName.setOnFocusChangeListener(onFocusChangeListener);

        mobileNumber = (YonaFontNumberTextView) view.findViewById(R.id.mobile_number);
        mobileNumber.requestFocus();
        YonaActivity.getActivity().showKeyboard(mobileNumber);
        mobileNumber.addTextChangedListener(new YonaPhoneWatcher(mobileNumber, getActivity(), null));

        firstnameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YonaActivity.getActivity().showKeyboard(firstName);
            }
        });

        lastNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YonaActivity.getActivity().showKeyboard(lastName);
            }
        });

        mobileNumberLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YonaActivity.getActivity().showKeyboard(mobileNumber);
            }
        });

        nickNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YonaActivity.getActivity().showKeyboard(nickName);
            }
        });

        mobileNumber.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    goToNext();
                }
                return false;
            }
        });


        profileImage = (ImageView) view.findViewById(R.id.profileImage);
        profileImageTxt = (YonaFontTextView) view.findViewById(R.id.profileIcon);
        updateProfileImage = (ImageView) view.findViewById(R.id.updateProfileImage);
        //TODO following 2 lines are disable until server implements Image upload feature.
//        profileImage.setOnClickListener(listener);
//        updateProfileImage.setOnClickListener(listener);
        updateProfileImage.setVisibility(View.GONE);
        profileViewMode();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        YonaApplication.getEventChangeManager().unRegisterListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitleAndIcon();
    }

    private void goToNext() {
        if (validateFields()) {
            YonaAnalytics.createTapEventWithCategory(AnalyticsConstant.SCREEN_EDIT_PROFILE, AnalyticsConstant.SAVE);
            updateUserProfile();
        }
    }

    private void setTitleAndIcon() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                leftIcon.setVisibility(View.GONE);
                toolbarTitle.setText(getString(R.string.edit_profile));
                rightIcon.setVisibility(View.VISIBLE);
                rightIcon.setImageResource(R.drawable.icn_create);

                rightIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToNext();
                    }
                });
            }
        }, AppConstant.TIMER_DELAY_HUNDRED);

    }

    private void profileViewMode() {
//        profileImage.setImageDrawable(getImage(null, true, R.color.mid_blue, YonaApplication.getEventChangeManager().getDataState().getUser().getFirstName(), YonaApplication.getEventChangeManager().getDataState().getUser().getLastName()));
        profileImageTxt.setVisibility(View.VISIBLE);
        profileImageTxt.setBackground(ContextCompat.getDrawable(YonaActivity.getActivity(), R.drawable.bg_big_friend_round));
        profileImageTxt.setText(YonaApplication.getEventChangeManager().getDataState().getUser().getFirstName().substring(0, 1) + YonaApplication.getEventChangeManager().getDataState().getUser().getLastName().substring(0, 1));
        firstName.setText(TextUtils.isEmpty(YonaApplication.getEventChangeManager().getDataState().getUser().getFirstName()) ? getString(R.string.blank) : YonaApplication.getEventChangeManager().getDataState().getUser().getFirstName());
        lastName.setText(TextUtils.isEmpty(YonaApplication.getEventChangeManager().getDataState().getUser().getLastName()) ? getString(R.string.blank) : YonaApplication.getEventChangeManager().getDataState().getUser().getLastName());
        nickName.setText(TextUtils.isEmpty(YonaApplication.getEventChangeManager().getDataState().getUser().getNickname()) ? getString(R.string.blank) : YonaApplication.getEventChangeManager().getDataState().getUser().getNickname());

        String number = YonaApplication.getEventChangeManager().getDataState().getUser().getMobileNumber();
        mobileNumber.setText(number);
        firstName.requestFocus();
    }

    private boolean validateFields() {
        String number = mobileNumber.getText().toString();
        String phonenumber = number.replaceAll(getString(R.string.space), getString(R.string.blank));
        if (!APIManager.getInstance().getAuthenticateManager().validateText(firstName.getText().toString())) {
            firstnameLayout.setErrorEnabled(true);
            firstnameLayout.setError(getString(R.string.enternamevalidation));
            YonaActivity.getActivity().showKeyboard(firstName);
            firstName.requestFocus();
            return false;
        } else if (!APIManager.getInstance().getAuthenticateManager().validateText(lastName.getText().toString())) {
            lastNameLayout.setErrorEnabled(true);
            lastNameLayout.setError(getString(R.string.enternamevalidation));
            YonaActivity.getActivity().showKeyboard(lastName);
            lastName.requestFocus();
            return false;
        } else if (!APIManager.getInstance().getAuthenticateManager().validateText(nickName.getText().toString())) {
            nickNameLayout.setErrorEnabled(true);
            nickNameLayout.setError(getString(R.string.enternicknamevalidation));
            YonaActivity.getActivity().showKeyboard(nickName);
            nickName.requestFocus();
            return false;
        } else if (!APIManager.getInstance().getAuthenticateManager().validateMobileNumber(phonenumber)) {
            mobileNumberLayout.setErrorEnabled(true);
            mobileNumberLayout.setError(getString(R.string.enternumbervalidation));
            YonaActivity.getActivity().showKeyboard(mobileNumber);
            mobileNumber.requestFocus();
            return false;
        }
        return true;
    }

    private void hideErrorMessages() {
        firstnameLayout.setError(null);
        lastNameLayout.setError(null);
        nickNameLayout.setError(null);
        mobileNumberLayout.setError(null);
    }

    private void updateUserProfile() {
        if (getActivity() != null) {
            user = new RegisterUser();
            user.setFirstName(firstName.getText().toString());
            user.setLastName(lastName.getText().toString());
            user.setNickName(nickName.getText().toString());
            String number = mobileNumber.getText().toString();
            user.setMobileNumber(number.replace(" ", ""));
            YonaActivity.getActivity().showLoadingView(true, null);
            APIManager.getInstance().getAuthenticateManager().registerUser(user, true, new DataLoadListener() {
                @Override
                public void onDataLoad(Object result) {
                    YonaActivity.getActivity().showLoadingView(false, null);
                    redirectToNextPage();
                }

                @Override
                public void onError(Object errorMessage) {
                    showError(errorMessage);
                }
            });
        }
    }

    private void redirectToNextPage() {
        if (YonaApplication.getEventChangeManager().getDataState().getUser() != null && oldUserNumber.equalsIgnoreCase(YonaApplication.getEventChangeManager().getDataState().getUser().getMobileNumber())) {
            YonaActivity.getActivity().onBackPressed();
            YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_USER_UPDATE, YonaApplication.getEventChangeManager().getDataState().getUser());
        } else {
            showMobileVerificationScreen(null);
        }
    }

    @Override
    public void onStateChange(int eventType, final Object object) {
        switch (eventType) {
            case EventChangeManager.EVENT_RECEIVED_PHOTO:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        profileImage.setImageDrawable(getImage((Bitmap) object, true, R.color.mid_blue, YonaApplication.getEventChangeManager().getDataState().getUser().getFirstName(), YonaApplication.getEventChangeManager().getDataState().getUser().getLastName()));
                    }
                }, AppConstant.TIMER_DELAY_HUNDRED);
                break;
            default:
                break;
        }
    }

    private void showError(Object errorMessage) {
        ErrorMessage message = (ErrorMessage) errorMessage;
        YonaActivity.getActivity().showLoadingView(false, null);
        if (message.getCode() != null && message.getCode().equalsIgnoreCase(ServerErrorCode.USER_EXIST_ERROR)) {
            CustomAlertDialog.show(YonaActivity.getActivity(), getString(R.string.useralreadyregister), getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        } else {
            Snackbar.make(YonaActivity.getActivity().findViewById(android.R.id.content), message.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    private void showMobileVerificationScreen(Bundle bundle) {
        removeStoredPassCode();
        Intent intent = new Intent(YonaActivity.getActivity(), OTPActivity.class);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        YonaActivity.getActivity().finish();
    }

    private void removeStoredPassCode() {
        SharedPreferences.Editor yonaPref = YonaApplication.getEventChangeManager().getSharedPreference().getUserPreferences().edit();
        yonaPref.putBoolean(PreferenceConstant.STEP_OTP, false);
        yonaPref.putBoolean(PreferenceConstant.PROFILE_OTP_STEP, true);
        yonaPref.commit();
    }


    @Override
    public String getAnalyticsCategory() {
        return AnalyticsConstant.SCREEN_EDIT_PROFILE;
    }
}
