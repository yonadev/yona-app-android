/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.ui.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.customview.CustomAlertDialog;
import nu.yona.app.customview.YonaFontEditTextView;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.customview.YonaPhoneWatcher;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.ui.BaseActivity;
import nu.yona.app.ui.LaunchActivity;
import nu.yona.app.ui.pincode.PasscodeActivity;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.PreferenceConstant;

/**
 * Created by kinnarvasa on 13/04/16.
 * This Activity is used only when user is trying to add another device.
 */
public class LoginActivity extends BaseActivity implements EventChangeListener {

    private final InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            String blockCharacterSet = "~#^&|$%*!@/()-'\":;,?{}=!$^';,?×÷<>{}€£¥₩%~`¤♡♥_|《》¡¿°•○●□■◇◆♧♣▲▼▶◀↑↓←→☆★▪:-);-):-D:-(:'(:";
            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };
    private YonaFontEditTextView mobileNumber, passcode;
    private TextInputLayout mobileNumberLayout, passcodeLayout;
    private final TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            passcodeLayout.setError(null);
            mobileNumberLayout.setError(null);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        ((YonaFontTextView) findViewById(R.id.toolbar_title)).setText(R.string.inloggin);

        mobileNumberLayout = (TextInputLayout) findViewById(R.id.mobile_number_layout);
        passcodeLayout = (TextInputLayout) findViewById(R.id.passcode_layout);

        mobileNumber = (YonaFontEditTextView) findViewById(R.id.mobile_number);
        passcode = (YonaFontEditTextView) findViewById(R.id.passcode);
        passcode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(AppConstant.ADD_DEVICE_PASSWORD_CHAR_LIMIT), filter});
        passcode.addTextChangedListener(watcher);
        passcode.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    goToNext();
                }
                return false;
            }
        });

        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNext();
            }
        });

        mobileNumber.setText(R.string.country_code_with_zero);
        mobileNumber.requestFocus();
        mobileNumber.setNotEditableLength(getString(R.string.country_code_with_zero).length());
        mobileNumber.addTextChangedListener(new YonaPhoneWatcher(mobileNumber, getString(R.string.country_code_with_zero), this, mobileNumberLayout));

        YonaApplication.getEventChangeManager().registerListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        YonaApplication.getEventChangeManager().unRegisterListener(this);
    }

    @Override
    public void onStateChange(int eventType, Object object) {

    }

    @Override
    public void onBackPressed() {
        doBack();
    }

    private void doBack() {
        startActivity(new Intent(LoginActivity.this, LaunchActivity.class));
        overridePendingTransition(R.anim.back_slide_in, R.anim.back_slide_out);
        finish();
    }

    private boolean validateMobileNumber(String number) {
        if (!APIManager.getInstance().getDeviceManager().validateMobileNumber(number)) {
            mobileNumberLayout.setErrorEnabled(true);
            mobileNumberLayout.setError(getString(R.string.enternumbervalidation));
            showKeyboard(mobileNumber);
            mobileNumber.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validatePasscode(String passcodeStr) {
        if (!APIManager.getInstance().getDeviceManager().validatePasscode(passcodeStr)) {
            passcodeLayout.setErrorEnabled(true);
            passcodeLayout.setError(getString(R.string.enterpasscode));
            showKeyboard(passcode);
            passcode.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * Do login on server in background.
     */
    private void doLogin() {
        showLoadingView(true, null);
        APIManager.getInstance().getDeviceManager().validateDevice(passcode.getText().toString(), getString(R.string.country_code) + mobileNumber.getText().toString().substring(getString(R.string.country_code_with_zero).length()).replace(" ", ""), new DataLoadListener() {
            @Override
            public void onDataLoad(Object result) {
                showLoadingView(false, null);
                showPasscodeScreen();
            }

            @Override
            public void onError(Object errorMessage) {
                showLoadingView(false, null);
                ErrorMessage message = (ErrorMessage) errorMessage;
                showLoadingView(false, null);
                CustomAlertDialog.show(LoginActivity.this, message.getMessage(), getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
            }
        });
    }

    private void showKeyboard(EditText editText) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(editText.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
    }

    private void showPasscodeScreen() {
        YonaApplication.getUserPreferences().edit().putBoolean(PreferenceConstant.STEP_CHALLENGES, true).commit();
        SharedPreferences.Editor editor = YonaApplication.getUserPreferences().edit();
        editor.putBoolean(PreferenceConstant.STEP_REGISTER, true);
        editor.putBoolean(PreferenceConstant.STEP_OTP, true);
        editor.putBoolean(PreferenceConstant.STEP_PASSCODE, true);
        editor.commit();
        startActivity(new Intent(LoginActivity.this, PasscodeActivity.class));
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        finish();
    }

    private void goToNext() {
        if (validateMobileNumber(getString(R.string.country_code) + mobileNumber.getText().toString().substring(getString(R.string.country_code_with_zero).length()).replace(getString(R.string.space), getString(R.string.blank)))
                && validatePasscode(passcode.getText().toString())) {
            doLogin();
        }
    }
}
