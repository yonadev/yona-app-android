/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.customview;

/**
 * Created by bhargavsuthar on 11/04/16.
 */

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import nu.yona.app.R;

/**
 * The type Yona phone watcher.
 */
public class YonaPhoneWatcher implements TextWatcher {

    private boolean backspacingFlag = false;
    private boolean editedFlag = false;
    private int cursorComplement;
    private EditText mobileNumber;
    private String prefixText;
    private Context mContext;

    /**
     * Instantiates a new Yona phone watcher.
     *
     * @param editText the edit text
     * @param prefix   the prefix
     * @param context  the context
     */
    public YonaPhoneWatcher(EditText editText, String prefix, Context context) {
        super();
        mobileNumber = editText;
        prefixText = prefix;
        mContext = context;
    }

    @Override
    public synchronized void beforeTextChanged(CharSequence s, int start, int count, int after) {
        cursorComplement = s.length() - mobileNumber.getSelectionStart();
        backspacingFlag = count > after;
    }

    @Override
    public synchronized void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.toString().length() < prefixText.length()
                || !s.toString().startsWith(prefixText)
                || s.toString().equals(prefixText + "0")) {
            mobileNumber.setText(R.string.country_code_with_zero);
            mobileNumber.setSelection(mobileNumber.getText().length());
        }

        String string = s.toString();
        String phone = string.replaceAll("[^\\d]", mContext.getString(R.string.blank));

        if (!editedFlag) {
            editedFlag = true;
            String ans = "";
            if (!backspacingFlag) {
                if (phone.length() >= 13) {
                    ans = prefixText + phone.substring(3, 6) + mContext.getString(R.string.space) + phone.substring(6, 9) + mContext.getString(R.string.space) + phone.substring(9, 13);
                } else if (phone.length() > 10) {
                    ans = prefixText + phone.substring(3, 6) + mContext.getString(R.string.space) + phone.substring(6, 9) + mContext.getString(R.string.space) + phone.substring(9);
                } else if (phone.length() > 7) {
                    ans = prefixText + phone.substring(3, 6) + mContext.getString(R.string.space) + phone.substring(6);
                } else if (phone.length() >= 3) {
                    ans = prefixText + phone.substring(3);
                }
                mobileNumber.setText(ans);
                mobileNumber.setSelection(mobileNumber.getText().length() - cursorComplement);
            }
        } else {
            editedFlag = false;
        }
    }

    public synchronized void afterTextChanged(Editable s) {

    }
}