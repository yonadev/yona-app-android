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

import nu.yona.app.R;
import nu.yona.app.analytics.AnalyticsConstant;
import nu.yona.app.analytics.Categorizable;
import nu.yona.app.analytics.YonaAnalytics;
import nu.yona.app.customview.CustomProgressDialog;
import nu.yona.app.utils.Logger;

/**
 * Created by kinnarvasa on 18/03/16.
 */
public class BaseActivity extends AppCompatActivity implements Categorizable {

    private CustomProgressDialog progressDialog;
    private InputMethodManager inputMethodManager;
    private Runnable backPressListener = null;
    private PauseResumeHook hook;

    /**
     * Show loading view.
     *
     * @param loading the loading
     * @param message the message
     */
    public void showLoadingView(boolean loading, String message) {
        try {
            String dialogText = getResources().getString(R.string.loading);
            if (!TextUtils.isEmpty(message)) {
                dialogText = message;
            }

       /* if (inputMethodManager == null) {
            inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        }*/
            if (loading && progressDialog == null) {
                progressDialog = new CustomProgressDialog(this, false);
                progressDialog.show();
            } else if (progressDialog != null && !loading) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        } catch (Exception e) {
            Logger.loge(BaseActivity.class.getSimpleName(), e.getMessage());
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

    @Override
    public void onPause() {
        super.onPause();
        if (hook != null) {
            hook.onPause(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        YonaAnalytics.updateScreen(this);
        if (hook != null) {
            hook.onResume(this);
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

    @Override
    public void onBackPressed() {
        if (backPressListener != null) {
            backPressListener.run();
        }

        super.onBackPressed();
    }

    public void addBackPressListener(Runnable r) {
        backPressListener = r;
    }

    public void clearBackPressListener() {
        backPressListener = null;
    }

    @Override
    public String getAnalyticsCategory() {
        return AnalyticsConstant.SCREEN_BASE_FRAGMENT;
    }

    public void setHook(PauseResumeHook hook) {
        this.hook = hook;
    }
}
