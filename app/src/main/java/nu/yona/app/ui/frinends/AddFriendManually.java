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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import nu.yona.app.R;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.utils.AppUtils;

/**
 * Created by kinnarvasa on 27/04/16.
 */
public class AddFriendManually extends BaseFragment {
    private View view;
    private EditText firstName, lastName, email, mobileNumber, nickName;
    private TextInputLayout firstNameLayout, lastNameLayout, emailLayout, mobileNumberLayout, nickNameLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.add_friend_manually_fragment, null);
        getView(view);
        addTextWatcher();
        return view;
    }

    private void getView(View view) {
        firstName = (EditText) view.findViewById(R.id.first_name);
        lastName = (EditText) view.findViewById(R.id.last_name);
        email = (EditText) view.findViewById(R.id.email);
        mobileNumber = (EditText) view.findViewById(R.id.mobile_number);
        nickName = (EditText) view.findViewById(R.id.nick_name);

        firstNameLayout = (TextInputLayout) view.findViewById(R.id.first_name_layout);
        lastNameLayout = (TextInputLayout) view.findViewById(R.id.last_name_layout);
        emailLayout = (TextInputLayout) view.findViewById(R.id.email_layout);
        mobileNumberLayout = (TextInputLayout) view.findViewById(R.id.mobile_number_layout);
        nickNameLayout = (TextInputLayout) view.findViewById(R.id.nick_name_layout);

        firstName.setFilters(new InputFilter[]{AppUtils.getFilter()});
        lastName.setFilters(new InputFilter[]{AppUtils.getFilter()});
        nickName.setFilters(new InputFilter[]{AppUtils.getFilter()});
    }

    private void addTextWatcher() {
        mobileNumber.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP && TextUtils.isEmpty(mobileNumber.getText())){
                    mobileNumber.setText(R.string.country_code_with_zero);
                }
                mobileNumber.setSelection(mobileNumber.getText().length());
                return false;
            }
        });
        mobileNumber.addTextChangedListener(new TextWatcher() {

            private boolean backspacingFlag = false;
            private boolean editedFlag = false;
            private int cursorComplement;


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                cursorComplement = s.length() - mobileNumber.getSelectionStart();
                if (count > after) {
                    backspacingFlag = true;
                } else {
                    backspacingFlag = false;
                }
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
}
