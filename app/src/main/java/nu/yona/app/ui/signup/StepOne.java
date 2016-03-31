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
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.customview.YonaFontEditTextView;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.BaseFragment;

/**
 * Created by kinnarvasa on 25/03/16.
 */
public class StepOne extends BaseFragment implements EventChangeListener{
    private TextInputLayout firstNameLayout, lastNameLayout;
    private YonaFontEditTextView firstName, lastName;
    private TextView privacyPolicy;
    private SignupActivity activity;
    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            firstNameLayout.setErrorEnabled(false);
            lastNameLayout.setErrorEnabled(false);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signup_stepone_fragment, null);

        activity = (SignupActivity) getActivity();

        firstNameLayout = (TextInputLayout) view.findViewById(R.id.first_name_layout);
        lastNameLayout = (TextInputLayout) view.findViewById(R.id.last_name_layout);

        firstName = (YonaFontEditTextView) view.findViewById(R.id.first_name);
        firstName.addTextChangedListener(watcher);

        lastName = (YonaFontEditTextView) view.findViewById(R.id.last_name);
        lastName.addTextChangedListener(watcher);

        privacyPolicy = (TextView) view.findViewById(R.id.privacyPolicy);
        privacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());

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
        if(eventType == EventChangeManager.EVENT_SIGNUP_STEP_ONE_NEXT){
            if(validateFirstName() && validateLastName()) {
                activity.getRegisterUser().setFirstName(firstName.getText().toString());
                activity.getRegisterUser().setLastName(lastName.getText().toString());
                YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_SIGNUP_STEP_ONE_ALLOW_NEXT, null);
            }
        }
    }

    private boolean validateFirstName(){
        if(!activity.getSignupManager().validateText(firstName.getText().toString())){
            firstNameLayout.setErrorEnabled(true);
            firstNameLayout.setError(getString(R.string.enter_name_validation));
            showKeyboard(firstName);
            firstName.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validateLastName(){
        if(!activity.getSignupManager().validateText(lastName.getText().toString())){
            lastNameLayout.setErrorEnabled(true);
            lastNameLayout.setError(getString(R.string.enter_name_validation));
            showKeyboard(lastName);
            lastName.requestFocus();
            return false;
        }
        return true;
    }

    private void showKeyboard(EditText editText){
        InputMethodManager inputMethodManager =  (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(editText.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
    }
}
