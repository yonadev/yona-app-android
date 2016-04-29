/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.ui.frinends;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import nu.yona.app.R;
import nu.yona.app.api.manager.BuddyManager;
import nu.yona.app.api.manager.impl.BuddyManagerImpl;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.customview.CustomAlertDialog;
import nu.yona.app.customview.YonaFontButton;
import nu.yona.app.customview.YonaFontEditTextView;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.utils.AppUtils;

/**
 * Created by kinnarvasa on 27/04/16.
 */
public class AddFriendManually extends BaseFragment {
    private YonaFontEditTextView firstName, lastName, email, mobileNumber;
    private TextInputLayout firstNameLayout, lastNameLayout, emailLayout, mobileNumberLayout;
    private YonaFontButton addFriendButton;
    private BuddyManager buddyManager;

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            hideErrorMessage();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_friend_manually_fragment, null);

        buddyManager = new BuddyManagerImpl(getActivity());

        getView(view);
        addTextWatcher();
        addButtonListener();
        return view;
    }

    private void getView(View view) {
        firstName = (YonaFontEditTextView) view.findViewById(R.id.first_name);
        lastName = (YonaFontEditTextView) view.findViewById(R.id.last_name);
        email = (YonaFontEditTextView) view.findViewById(R.id.email);
        mobileNumber = (YonaFontEditTextView) view.findViewById(R.id.mobile_number);

        firstName.addTextChangedListener(textWatcher);
        lastName.addTextChangedListener(textWatcher);
        email.addTextChangedListener(textWatcher);

        firstNameLayout = (TextInputLayout) view.findViewById(R.id.first_name_layout);
        lastNameLayout = (TextInputLayout) view.findViewById(R.id.last_name_layout);
        emailLayout = (TextInputLayout) view.findViewById(R.id.email_layout);
        mobileNumberLayout = (TextInputLayout) view.findViewById(R.id.mobile_number_layout);

        addFriendButton = (YonaFontButton) view.findViewById(R.id.addFriendButton);

        firstName.setFilters(new InputFilter[]{AppUtils.getFilter()});
        lastName.setFilters(new InputFilter[]{AppUtils.getFilter()});
    }

    private void addButtonListener() {
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    addFriend();
                }
            }
        });
    }

    private boolean validateFields() {
        if (!buddyManager.validateText(firstName.getText().toString())) {
            firstNameLayout.setErrorEnabled(true);
            firstNameLayout.setError(getString(R.string.enterfirstnamevalidation));
            showKeyboard(firstName);
            return false;
        } else if (!buddyManager.validateText(lastName.getText().toString())) {
            lastNameLayout.setErrorEnabled(true);
            lastNameLayout.setError(getString(R.string.enterlastnamevalidation));
            showKeyboard(lastName);
            return false;
        } else if (!buddyManager.validateEmail(email.getText().toString())) {
            emailLayout.setErrorEnabled(true);
            emailLayout.setError(getString(R.string.enteremailvalidation));
            showKeyboard(email);
            return false;
        } else if (!buddyManager.validateMobileNumber(mobileNumber.getText().toString())) {
            mobileNumberLayout.setErrorEnabled(true);
            mobileNumberLayout.setError(getString(R.string.enternumbervalidation));
            showKeyboard(mobileNumber);
            return false;
        }
        return true;
    }

    private void addFriend() {
        ((YonaActivity) getActivity()).showLoadingView(true, null);
        buddyManager.addBuddy(firstName.getText().toString(), lastName.getText().toString(), email.getText().toString(), mobileNumber.getText().toString(), new DataLoadListener() {
            @Override
            public void onDataLoad(Object result) {
                ((YonaActivity) getActivity()).showLoadingView(false, null);
                getActivity().onBackPressed();
            }

            @Override
            public void onError(Object errorMessage) {
                ErrorMessage message = (ErrorMessage) errorMessage;
                ((YonaActivity) getActivity()).showLoadingView(false, null);

                CustomAlertDialog.show(getActivity(), message.getMessage(), getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void hideErrorMessage() {
        firstNameLayout.setError(null);
        lastNameLayout.setError(null);
        emailLayout.setError(null);
    }

    private void addTextWatcher() {
        mobileNumber.setText(R.string.country_code_with_zero);
        mobileNumber.addTextChangedListener(new TextWatcher() {

            private boolean backspacingFlag = false;
            private boolean editedFlag = false;
            private int cursorComplement;


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                cursorComplement = s.length() - mobileNumber.getSelectionStart();
                backspacingFlag = count > after;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().length() < getString(R.string.country_code_with_zero).length()
                        || !s.toString().startsWith(getString(R.string.country_code_with_zero))
                        || s.toString().equals((getString(R.string.country_code_with_zero) + "0"))) {
                    mobileNumber.setText(R.string.country_code_with_zero);
                    mobileNumber.setSelection(mobileNumber.getText().length());
                }

                String string = s.toString();
                String phone = string.replaceAll("[^\\d]", "");

                if (!editedFlag) {
                    editedFlag = true;
                    String ans = "";
                    if (!backspacingFlag) {
                        if (phone.length() >= 13) {
                            ans = getString(R.string.country_code_with_zero) + phone.substring(3, 6) + " " + phone.substring(6, 9) + " " + phone.substring(9, 13);
                        } else if (phone.length() > 10) {
                            ans = getString(R.string.country_code_with_zero) + phone.substring(3, 6) + " " + phone.substring(6, 9) + " " + phone.substring(9);
                        } else if (phone.length() > 7) {
                            ans = getString(R.string.country_code_with_zero) + phone.substring(3, 6) + " " + phone.substring(6);
                        } else if (phone.length() >= 3) {
                            ans = getString(R.string.country_code_with_zero) + phone.substring(3);
                        }
                        mobileNumber.setText(ans);
                        mobileNumber.setSelection(mobileNumber.getText().length() - cursorComplement);
                    }
                } else {
                    editedFlag = false;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void showKeyboard(EditText editText) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(editText.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
    }
}
