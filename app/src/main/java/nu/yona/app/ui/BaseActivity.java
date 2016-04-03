/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;

import nu.yona.app.R;
import nu.yona.app.customview.CustomProgressDialog;

/**
 * Created by kinnarvasa on 18/03/16.
 */
public class BaseActivity extends AppCompatActivity {

    private CustomProgressDialog progressDialog;
    private InputMethodManager inputMethodManager;

    public void showLoadingView(boolean loading, String message) {

        if (this == null) {
            return;
        }

        String dialogText = getResources().getString(R.string.loading);
        if (!TextUtils.isEmpty(message)) {
            dialogText = message;
        }

        if (inputMethodManager == null) {
            inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        if (loading && progressDialog == null) {
            progressDialog = new CustomProgressDialog(this, dialogText, false);
            progressDialog.show();
        } else if (progressDialog != null && !loading) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    protected void onPause() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        super.onPause();
    }

    public void dismissActiveDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


}
