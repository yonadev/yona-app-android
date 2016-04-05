/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.ui.signup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.customview.YonaFontEditTextView;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.BaseFragment;

/**
 * Created by kinnarvasa on 25/03/16.
 */
public class StepTwo extends BaseFragment implements EventChangeListener {

    private YonaFontEditTextView mobileNumber, nickName;
    private TextInputLayout mobileNumberLayout;
    private SignupActivity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signup_steptwo_fragment, null);

        activity = (SignupActivity) getActivity();

        mobileNumber = (YonaFontEditTextView) view.findViewById(R.id.mobile_number);
        nickName = (YonaFontEditTextView) view.findViewById(R.id.nick_name);

        mobileNumber.setText(R.string.country_code_with_zero);
        mobileNumber.requestFocus();
        activity.showKeyboard(mobileNumber);
        mobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().length() < getString(R.string.country_code_with_zero).length()
                        || !s.toString().startsWith(getString(R.string.country_code_with_zero))
                        || s.toString().equals((getString(R.string.country_code_with_zero) + "0"))) {
                    mobileNumber.setText(R.string.country_code_with_zero);
                    mobileNumber.setSelection(mobileNumber.getText().length());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mobileNumberLayout = (TextInputLayout) view.findViewById(R.id.mobile_number_layout);

        YonaApplication.getEventChangeManager().registerListener(this);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        YonaApplication.getEventChangeManager().unRegisterListener(this);
    }

    @Override
    public void onStateChange(int eventType, Object object) {
        if (eventType == EventChangeManager.EVENT_SIGNUP_STEP_TWO_NEXT) {
            String number = getString(R.string.country_code) + mobileNumber.getText().toString().substring(getString(R.string.country_code_with_zero).length());
            if (validateMobileNumber(number)) {
                activity.getRegisterUser().setMobileNumber(number);
                activity.getRegisterUser().setNickName(nickName.getText().toString());
                YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_SIGNUP_STEP_TWO_ALLOW_NEXT, null);
            }
        }
    }

    private boolean validateMobileNumber(String number) {
        if (!activity.getAuthenticateManager().validateMobileNumber(number)) {
            mobileNumberLayout.setErrorEnabled(true);
            mobileNumberLayout.setError(getString(R.string.enter_number_validation));
            activity.showKeyboard(mobileNumber);
            mobileNumber.requestFocus();
            return false;
        }
        return true;
    }
}
