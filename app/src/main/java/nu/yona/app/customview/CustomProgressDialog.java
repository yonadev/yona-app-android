/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.customview;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import nu.yona.app.R;
import nu.yona.app.utils.AppUtils;

/**
 * The type Custom progress dialog.
 */
public class CustomProgressDialog extends Dialog {

    private Button okBtn;
    private TextView progressTxt;

    private CustomProgressDialog(Context context) {
        super(context);
        init(false, true);
    }

    /**
     * Instantiates a new Custom progress dialog.
     *
     * @param context        the context
     * @param message        the message
     * @param isErrorMessage the is error message
     */
    public CustomProgressDialog(Context context, String message, boolean isErrorMessage) {
        super(context);
        init(false, false);

        if (isErrorMessage) {
            findViewById(R.id.progress_bar).setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(message)) {
            ((TextView) this.findViewById(R.id.message)).setText(message);
        }
    }

    private CustomProgressDialog(Context context, boolean hideProgressBar, boolean hideDefaultMessage) {
        super(context);
        init(hideProgressBar, hideDefaultMessage);
    }

    private CustomProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    private CustomProgressDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private void init(boolean hideProgressBar, boolean hideDefaultMessage) {
        this.setCancelable(false);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.progressbar_dialog_layout);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressTxt = (TextView) this.findViewById(R.id.message);
        if (hideDefaultMessage) {
            progressTxt.setVisibility(View.GONE);
        }
        if (hideProgressBar) {
            this.findViewById(R.id.progress_bar).setVisibility(View.GONE);
        }
        okBtn = (Button) findViewById(R.id.okBtn);
    }

    @Override
    public void show() {
        super.show();
    }

    /**
     * Show.
     *
     * @param duration the duration
     */
    public void show(int duration) {
        try {
            super.show();
            hideOkBtn();
            Handler handler;
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    if (CustomProgressDialog.this.isShowing()) {
                        CustomProgressDialog.this.cancel();
                        CustomProgressDialog.this.dismiss();
                    }
                }
            }, duration);
        } catch (Exception e) {
            AppUtils.throwException(CustomProgressDialog.class.getSimpleName(), e, Thread.currentThread(), null);
        }
    }

    /**
     * Update progress message.
     *
     * @param message the message
     */
    public void updateProgressMessage(String message) {
        progressTxt.setText(message);
    }

    /**
     * Show ok btn.
     */
    public void showOkBtn() {
        okBtn.setVisibility(View.VISIBLE);
    }

    /**
     * Gets ok btn.
     *
     * @return the ok btn
     */
    public Button getOkBtn() {
        return okBtn;
    }

    private void hideOkBtn() {
        okBtn.setVisibility(View.GONE);
    }

}
