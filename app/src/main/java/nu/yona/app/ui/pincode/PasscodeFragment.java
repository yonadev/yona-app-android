/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.pincode;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.analytics.AnalyticsConstant;
import nu.yona.app.api.manager.impl.PasscodeManagerImpl;
import nu.yona.app.customview.YonaFontNumberTextView;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.utils.AppConstant;

/**
 * Created by bhargavsuthar on 4/3/16.
 */
public class PasscodeFragment extends BaseFragment implements EventChangeListener {

    private YonaFontNumberTextView passcode1, passcode2, passcode3, passcode4;
    private PasscodeManagerImpl passcodeManagerImpl;
    private YonaPasswordTransformationManager yonaPasswordTransformationManager;
    private FieldTextWatcher watcher;
    private int backgroundDrawable = R.drawable.passcode_edit_bg_grape;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.passcode_layout, container, false);


        YonaApplication.getEventChangeManager().registerListener(this);

        passcodeManagerImpl = new PasscodeManagerImpl();
        yonaPasswordTransformationManager = new YonaPasswordTransformationManager();
        watcher = new FieldTextWatcher();

        passcode1 = getLayout(view, R.id.passcode1);
        passcode2 = getLayout(view, R.id.passcode2);
        passcode3 = getLayout(view, R.id.passcode3);
        passcode4 = getLayout(view, R.id.passcode4);

        if (getArguments() != null) {
            if (getArguments().get(AppConstant.COLOR_CODE) != null) {
                view.setBackgroundColor(getArguments().getInt(AppConstant.COLOR_CODE));
            }
            if (getArguments().get(AppConstant.PASSCODE_TEXT_BACKGROUND) != null) {
                backgroundDrawable = getArguments().getInt(AppConstant.PASSCODE_TEXT_BACKGROUND);
            }
        }
        resetDigit();
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        passcode1.setBackgroundResource(backgroundDrawable);
        passcode2.setBackgroundResource(backgroundDrawable);
        passcode3.setBackgroundResource(backgroundDrawable);
        passcode4.setBackgroundResource(backgroundDrawable);
    }

    private YonaFontNumberTextView getLayout(View view, int id) {
        YonaFontNumberTextView textView = (YonaFontNumberTextView) view.findViewById(id);
        textView.setTransformationMethod(yonaPasswordTransformationManager);
//        textView.setOnKeyListener(keyListener);
        textView.addTextChangedListener(watcher);
        return textView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        YonaApplication.getEventChangeManager().unRegisterListener(this);
    }

    /**
     * Reset all fields
     */
    public void resetDigit() {
        passcode1.getText().clear();
        passcode1.setFocusableInTouchMode(true);
        passcode2.getText().clear();
        passcode2.setFocusableInTouchMode(false);
        passcode3.getText().clear();
        passcode3.setFocusableInTouchMode(false);
        passcode4.getText().clear();
        passcode4.setFocusableInTouchMode(false);
        passcode1.setFocusable(true);
        passcode1.requestFocus();

    }

    @Override
    public void onStateChange(int eventType, Object object) {
        switch (eventType) {
            case EventChangeManager.EVENT_PASSCODE_ERROR:
                resetDigit();
                break;
            case EventChangeManager.EVENT_KEY_BACK_PRESSED:
                clearLastDigit();
            default:
                break;
        }
    }

    private void clearLastDigit() {
        if (!TextUtils.isEmpty(passcode4.getText().toString())) {
            passcode4.getText().clear();
            passcode4.setFocusableInTouchMode(false);
            passcode3.requestFocus();
        } else if (!TextUtils.isEmpty(passcode3.getText().toString())) {
            passcode3.getText().clear();
            passcode4.setFocusableInTouchMode(false);
            passcode3.setFocusableInTouchMode(false);
            passcode2.requestFocus();
        } else if (!TextUtils.isEmpty(passcode2.getText().toString())) {
            passcode2.getText().clear();
            passcode4.setFocusableInTouchMode(false);
            passcode3.setFocusableInTouchMode(false);
            passcode2.setFocusableInTouchMode(false);
            passcode1.requestFocus();
        } else if (!TextUtils.isEmpty(passcode1.getText().toString())) {
            passcode1.getText().clear();
            passcode4.setFocusableInTouchMode(false);
            passcode3.setFocusableInTouchMode(false);
            passcode2.setFocusableInTouchMode(false);
            passcode1.requestFocus();
        }
    }

    private void setFocus(YonaFontNumberTextView passcode1, YonaFontNumberTextView passcode2) {
        passcode2.setFocusableInTouchMode(true);
        passcode2.requestFocus();
        passcode1.setFocusableInTouchMode(false);
    }

    /**
     * Disable Editable text , so User can not Enter Passcode into it
     */
    public void disableEditable() {
        passcode1.setEnabled(false);
        passcode2.setEnabled(false);
        passcode3.setEnabled(false);
        passcode4.setEnabled(false);
    }

    private final class FieldTextWatcher implements TextWatcher {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 1) {
                if (passcode1.hasFocus()) {
                    setFocus(passcode1, passcode2);
                } else if (passcode2.hasFocus()) {
                    setFocus(passcode2, passcode3);
                } else if (passcode3.hasFocus()) {
                    setFocus(passcode3, passcode4);
                }
            } else {
                if (passcode4.hasFocus()) {
                    setFocus(passcode4, passcode3);
                } else if (passcode3.hasFocus()) {
                    setFocus(passcode3, passcode2);
                } else if (passcode2.hasFocus()) {
                    setFocus(passcode2, passcode1);
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String code =
                    passcode1.getText().toString() + passcode2.getText().toString() + passcode3.getText().toString() + passcode4.getText().toString();
            if (passcode4.hasFocus() && passcodeManagerImpl.checkPasscodeLength(code)) {
                YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_PASSCODE_STEP_TWO, code);
            }
        }

    }
}