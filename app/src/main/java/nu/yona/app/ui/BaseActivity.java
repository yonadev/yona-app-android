/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.customview.CustomProgressDialog;

/**
 * Created by kinnarvasa on 18/03/16.
 */
public class BaseActivity extends AppCompatActivity {

    private CustomProgressDialog progressDialog;
    private InputMethodManager inputMethodManager;

    /**
     * Show loading view.
     *
     * @param loading the loading
     * @param message the message
     */
    public void showLoadingView(boolean loading, String message) {

        String dialogText = getResources().getString(R.string.loading);
        if (!TextUtils.isEmpty(message)) {
            dialogText = message;
        }

       /* if (inputMethodManager == null) {
            inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        }*/
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
//        if (progressDialog != null && progressDialog.isShowing()) {
//            progressDialog.dismiss();
//        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForCrashes();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterManagers();
    }

    private void checkForCrashes() {
        if (getResources().getBoolean(R.bool.enableHockyTracking)) {
            CrashManager.register(this, getResources().getString(R.string.hockey_app_key), YonaApplication.getYonaCustomCrashManagerListener());
        }
    }

    private void unregisterManagers() {
        if (getResources().getBoolean(R.bool.enableHockyTracking)) {
            UpdateManager.unregister();
        }
    }

    /**
     * Start new activity.
     *
     * @param mClass the m class
     */
    public void startNewActivity(Class mClass) {
        startNewActivity(null, mClass);
    }

    /**
     * Start new activity.
     *
     * @param bundle the bundle
     * @param mClass the m class
     */
    public void startNewActivity(Bundle bundle, Class mClass) {
        Intent intent = new Intent(this, mClass);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
//        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        finish();
    }

    /**
     * Show keyboard.
     *
     * @param editText the edit text
     */
    public void showKeyboard(EditText editText) {
        if (editText != null) {
            inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    /**
     * Hide the Keyboard
     */
    public void hideSoftInput() {
        View currentFocus = getCurrentFocus();
        if (inputMethodManager == null) {
            inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        if (currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }


}
