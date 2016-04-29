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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.impl.PasscodeManagerImpl;
import nu.yona.app.customview.YonaFontEditTextView;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.signup.OTPActivity;
import nu.yona.app.utils.AppConstant;

/**
 * Created by bhargavsuthar on 4/3/16.
 */
public class PasscodeFragment extends BaseFragment implements EventChangeListener, View.OnClickListener {

    private YonaFontTextView passcode_title, passcode_description, passcode_error, passcode_reset;
    private YonaFontEditTextView passcode1, passcode2, passcode3, passcode4;
    private ProgressBar profile_progress;
    private PasscodeManagerImpl passcodeManagerImpl;
    private ImageView accont_image;
    private String screen_type;
    private YonaPasswordTransformationManager yonaPasswordTransformationManager;
    private FieldTextWatcher watcher;

    private final View.OnKeyListener keyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (v.getId() == R.id.passcode4) {
                    if (passcode4.getText().length() == 0) {
                        setFocus(passcode4, passcode3);
                        passcode3.setText("");
                    }
                } else if (v.getId() == R.id.passcode3) {
                    if (passcode3.getText().length() == 0) {
                        setFocus(passcode3, passcode2);
                        passcode2.setText("");
                    }
                } else if (v.getId() == R.id.passcode2) {
                    if (passcode2.getText().length() == 0) {
                        setFocus(passcode2, passcode1);
                        passcode1.setText("");
                    }
                }
            }
            return false;
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pincode_layout, container, false);

        YonaApplication.getEventChangeManager().registerListener(this);

        if (getArguments() != null) {
            screen_type = getArguments().getString(AppConstant.SCREEN_TYPE);
        }

        passcode_title = (YonaFontTextView) view.findViewById(R.id.passcode_title);
        passcode_description = (YonaFontTextView) view.findViewById(R.id.passcode_description);
        passcode_error = (YonaFontTextView) view.findViewById(R.id.passcode_error);
        passcode_reset = (YonaFontTextView) view.findViewById(R.id.passcode_reset);
        accont_image = (ImageView) view.findViewById(R.id.img_account_check);
        passcode_reset = (YonaFontTextView) view.findViewById(R.id.passcode_reset);

        profile_progress = (ProgressBar) view.findViewById(R.id.profile_progress);

        passcodeManagerImpl = new PasscodeManagerImpl();
        yonaPasswordTransformationManager = new YonaPasswordTransformationManager();
        watcher = new FieldTextWatcher();

        passcode1 = getLayout(view, R.id.passcode1);
        passcode2 = getLayout(view, R.id.passcode2);
        passcode3 = getLayout(view, R.id.passcode3);
        passcode4 = getLayout(view, R.id.passcode4);

        passcode_reset.setOnClickListener(this);

        updateScreenUI();
        resetDigit();

        return view;

    }

    private YonaFontEditTextView getLayout(View view, int id) {
        YonaFontEditTextView textView = (YonaFontEditTextView) view.findViewById(id);
        textView.setTransformationMethod(yonaPasswordTransformationManager);
        textView.setOnKeyListener(keyListener);
        textView.addTextChangedListener(watcher);
        return textView;
    }

    private void updateScreenUI() {
        if (!TextUtils.isEmpty(screen_type)) {
            if (screen_type.equalsIgnoreCase(AppConstant.PASSCODE)) {
                populatePasscodeView();
                visibleView();
            } else if (screen_type.equalsIgnoreCase(AppConstant.PASSCODE_VERIFY)) {
                populateVerifyPasscodeView();
                visibleView();
            } else if (screen_type.equalsIgnoreCase(AppConstant.LOGGED_IN)) {
                visibleLoginView();
                populateLoginView();
            } else if (screen_type.equalsIgnoreCase(AppConstant.OTP)) {
                populateOTPView();
                visibleView();
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        YonaApplication.getEventChangeManager().unRegisterListener(this);
    }

    private void populateOTPView() {
        accont_image.setImageResource(R.drawable.add_avatar);
        passcode_title.setText(getString(R.string.accountlogin));
        passcode_description.setText(getString(R.string.accountloginsecuritymessage));
        ((OTPActivity) getActivity()).updateTitle(getString(R.string.join));
        profile_progress.setProgress(getResources().getInteger(R.integer.passcode_progress));
        passcode_reset.setText(getString(R.string.sendotpagain));
        passcode_reset.setVisibility(View.VISIBLE);
    }

    private void visibleView() {
        passcode_title.setVisibility(View.VISIBLE);
        passcode_description.setVisibility(View.VISIBLE);
        profile_progress.setVisibility(View.VISIBLE);
    }

    private void visibleLoginView() {
        passcode_title.setVisibility(View.VISIBLE);
        passcode_description.setVisibility(View.GONE);
        profile_progress.setVisibility(View.GONE);
        passcode_reset.setVisibility(View.VISIBLE);
    }

    private void populateLoginView() {
        accont_image.setImageResource(R.drawable.icn_y);
        passcode_title.setText(getString(R.string.passcodetitle));
    }

    /**
     * update screen's text as per account pincode's verification
     */
    private void populateVerifyPasscodeView() {
        accont_image.setImageResource(R.drawable.icn_secure);
        passcode_title.setText(getString(R.string.passcodestep2title));
        passcode_description.setText(getString(R.string.passcodestep2desc));
        ((PasscodeActivity) getActivity()).updateTitle(getString(R.string.pincode));
        profile_progress.setProgress(getResources().getInteger(R.integer.passcode_verify_progerss));
    }

    /**
     * update screen's text as per Account pincode creation
     */
    private void populatePasscodeView() {
        accont_image.setImageResource(R.drawable.icn_account_created);
        passcode_title.setText(getString(R.string.passcodestep1title));
        passcode_description.setText(getString(R.string.passcodestep1desc));
        profile_progress.setProgress(getResources().getInteger(R.integer.passcode_create_progress));
        ((PasscodeActivity) getActivity()).updateTitle(getString(R.string.pincode));
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
        passcode1.requestFocus();

    }

    @Override
    public void onStateChange(int eventType, Object object) {
        switch (eventType) {
            case EventChangeManager.EVENT_PASSCODE_ERROR:
                passcode_error.setText((String) object);
                resetDigit();
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.passcode_reset:
                doPasscodeReset();
                break;
            default:
                break;
        }
    }

    private void doPasscodeReset() {
        switch (screen_type) {
            case AppConstant.OTP:
                YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_OTP_RESEND, null);
                break;
            case AppConstant.LOGGED_IN:
                YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_PASSCODE_RESET, null);
                break;
            default:
                break;
        }
    }

    private void setFocus(YonaFontEditTextView passcode1, YonaFontEditTextView passcode2) {
        passcode1.setFocusableInTouchMode(false);
        passcode2.setFocusableInTouchMode(true);
        passcode2.requestFocus();
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