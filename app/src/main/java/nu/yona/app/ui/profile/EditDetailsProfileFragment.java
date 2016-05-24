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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.RegisterUser;
import nu.yona.app.api.utils.ServerErrorCode;
import nu.yona.app.customview.CustomAlertDialog;
import nu.yona.app.customview.YonaFontEditTextView;
import nu.yona.app.customview.YonaPhoneWatcher;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.ui.signup.OTPActivity;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.PreferenceConstant;

/**
 * Created by kinnarvasa on 10/05/16.
 */
public class EditDetailsProfileFragment extends BaseProfileFragment implements EventChangeListener {
    private YonaFontEditTextView firstName, lastName, nickName, mobileNumber;
    private TextInputLayout firstnameLayout, lastNameLayout, nickNameLayout, mobileNumberLayout;
    private ImageView profileImage, updateProfileImage;
    private View.OnClickListener listener;
    private TextWatcher textWatcher;
    private String oldUserNumber;
    private RegisterUser user;
    private View.OnFocusChangeListener onFocusChangeListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_profile_detail_fragment, null);


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
                hideErrorMessages();
            }

            @Override
            public void afterTextChanged(Editable s) {

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

        mobileNumber = (YonaFontEditTextView) view.findViewById(R.id.mobile_number);
        mobileNumber.setText(R.string.country_code_with_zero);
        mobileNumber.requestFocus();
        YonaActivity.getActivity().showKeyboard(mobileNumber);
        mobileNumber.setNotEditableLength(getString(R.string.country_code_with_zero).length());
        mobileNumber.addTextChangedListener(new YonaPhoneWatcher(mobileNumber, getString(R.string.country_code_with_zero), getActivity(), mobileNumberLayout));

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
        profileImage.setOnClickListener(listener);

        updateProfileImage = (ImageView) view.findViewById(R.id.updateProfileImage);
        updateProfileImage.setOnClickListener(listener);
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
            updateUserProfile();
        }
    }

    private void setTitleAndIcon() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                YonaActivity.getActivity().getLeftIcon().setVisibility(View.GONE);
                YonaActivity.getActivity().updateTitle(R.string.edit_profile);
                YonaActivity.getActivity().getRightIcon().setVisibility(View.VISIBLE);
                YonaActivity.getActivity().getRightIcon().setImageDrawable(YonaActivity.getActivity().getDrawable(R.drawable.icn_create));

                YonaActivity.getActivity().getRightIcon().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToNext();
                    }
                });
            }
        }, AppConstant.TIMER_DELAY_HUNDRED);

    }

    private void profileViewMode() {
        profileImage.setBackground(getImage(null, true, R.color.mid_blue, YonaApplication.getUser().getFirstName(), YonaApplication.getUser().getLastName()));

        firstName.setText(TextUtils.isEmpty(YonaApplication.getUser().getFirstName()) ? getString(R.string.blank) : YonaApplication.getUser().getFirstName());
        lastName.setText(TextUtils.isEmpty(YonaApplication.getUser().getLastName()) ? getString(R.string.blank) : YonaApplication.getUser().getLastName());
        nickName.setText(TextUtils.isEmpty(YonaApplication.getUser().getNickname()) ? getString(R.string.blank) : YonaApplication.getUser().getNickname());
        int NUMBER_LENGTH = 9;

        String number = YonaApplication.getUser().getMobileNumber();
        if (!TextUtils.isEmpty(number) && number.length() > NUMBER_LENGTH) {
            oldUserNumber = number;
            number = number.substring(number.length() - NUMBER_LENGTH);
            number = number.substring(0, 3) + getString(R.string.space) + number.substring(3, 6) + getString(R.string.space) + number.substring(6, 9);
            mobileNumber.setText(getString(R.string.country_code_with_zero) + number);
        }
        firstName.requestFocus();
    }

    private boolean validateFields() {
        String number = YonaActivity.getActivity().getString(R.string.country_code) + mobileNumber.getText().toString().substring(YonaActivity.getActivity().getString(R.string.country_code_with_zero).length());
        String phonenumber = number.replace(getString(R.string.space), getString(R.string.blank));
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
            String number = getString(R.string.country_code) + mobileNumber.getText().toString().substring(getString(R.string.country_code_with_zero).length());
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
        if (YonaApplication.getUser() != null && oldUserNumber.equalsIgnoreCase(YonaApplication.getUser().getMobileNumber())) {
            YonaActivity.getActivity().onBackPressed();
            YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_USER_UPDATE, YonaApplication.getUser());
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
                        profileImage.setBackground(getImage((Bitmap) object, true, R.color.mid_blue, YonaApplication.getUser().getFirstName(), YonaApplication.getUser().getLastName()));
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
            CustomAlertDialog.show(YonaActivity.getActivity(), getString(R.string.appname), getString(R.string.useroverride, user.getMobileNumber()),
                    getString(R.string.yes), getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            OverrideUser();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        } else {
            Snackbar.make(YonaActivity.getActivity().findViewById(android.R.id.content), message.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * When user is already registered with same number and want to override same user.
     */
    private void OverrideUser() {
        YonaActivity.getActivity().showLoadingView(true, null);
        APIManager.getInstance().getAuthenticateManager().requestUserOverride(user.getMobileNumber(), new DataLoadListener() {

            @Override
            public void onDataLoad(Object result) {
                YonaActivity.getActivity().showLoadingView(false, null);
                Bundle bundle = new Bundle();
                bundle.putSerializable(AppConstant.USER, user);
                showMobileVerificationScreen(bundle);
            }

            @Override
            public void onError(Object errorMessage) {
                YonaActivity.getActivity().showLoadingView(false, null);
                showError(errorMessage);
            }
        });
    }

    private void showMobileVerificationScreen(Bundle bundle) {
        removeStoredPassCode();
        Intent intent = new Intent(YonaActivity.getActivity(), OTPActivity.class);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        YonaActivity.getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        YonaActivity.getActivity().finish();
    }

    private void removeStoredPassCode() {
        SharedPreferences.Editor yonaPref = YonaApplication.getUserPreferences().edit();
        yonaPref.putString(PreferenceConstant.YONA_PASSCODE, getString(R.string.blank)); // remove user's passcode from device.
        yonaPref.putBoolean(PreferenceConstant.STEP_PASSCODE, false);
        yonaPref.putBoolean(PreferenceConstant.STEP_OTP, false);
        yonaPref.commit();
    }
}
