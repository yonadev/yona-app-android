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
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.impl.PasscodeManagerImpl;
import nu.yona.app.customview.YonaFontEditTextView;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.BaseActivity;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.utils.AppConstant;

/**
 * Created by bhargavsuthar on 4/3/16.
 */
public class PasscodeFragment extends BaseFragment implements EventChangeListener {

    private YonaFontEditTextView passcode1, passcode2, passcode3, passcode4;
    private final View.OnKeyListener keyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (v.getId() == R.id.passcode4) {
                    if (passcode4.getText().length() == 0) {
                        setFocus(passcode4, passcode3);
                        passcode3.getText().clear();
                    }
                } else if (v.getId() == R.id.passcode3) {
                    if (passcode3.getText().length() == 0) {
                        setFocus(passcode3, passcode2);
                        passcode2.getText().clear();
                    }
                } else if (v.getId() == R.id.passcode2) {
                    if (passcode2.getText().length() == 0) {
                        setFocus(passcode2, passcode1);
                        passcode1.getText().clear();
                    }
                }
            }
            return false;
        }
    };
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
        ((BaseActivity) getActivity()).showKeyboard(passcode1);
    }
    /*
    @Override
    public void onPause() {
        super.onPause();
        ((BaseActivity) getActivity()).hideSoftInput();
    }*/

    private YonaFontEditTextView getLayout(View view, int id) {
        YonaFontEditTextView textView = (YonaFontEditTextView) view.findViewById(id);
        textView.setTransformationMethod(yonaPasswordTransformationManager);
        textView.setOnKeyListener(keyListener);
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
            default:
                break;
        }
    }

    private void setFocus(YonaFontEditTextView passcode1, YonaFontEditTextView passcode2) {
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