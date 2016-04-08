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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.TextUtils;

import nu.yona.app.R;

public class CustomAlertDialog extends AlertDialog.Builder {

    public CustomAlertDialog(Context context) {
        super(context);
    }

    public CustomAlertDialog(Context context, int theme) {
        super(context, theme);
    }

    public static synchronized CustomAlertDialog show(Context context, CharSequence title, CharSequence message, CharSequence positiveButton) {

        CustomAlertDialog customAlertDialog = new CustomAlertDialog(context);
        customAlertDialog.setCancelable(false);

        // for setting title view in center
        YonaFontTextView titleTextView = new YonaFontTextView(context);
        titleTextView.setText(title);
        titleTextView.setPadding(context.getResources().getInteger(R.integer.margin_ten), context.getResources().getInteger(R.integer.margin_ten),
                context.getResources().getInteger(R.integer.margin_twenty), context.getResources().getInteger(R.integer.margin_twenty));
        customAlertDialog.setCustomTitle(titleTextView);
        customAlertDialog.setMessage(message);

        customAlertDialog.setPositiveButton(positiveButton, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        customAlertDialog.show();
        return customAlertDialog;
    }

    /*
    * This method is in exception to CustomAlertDialog. method returns AlertDialog instead of AlertDialog.Builder
    * AlertDialog's method of isShowing is useful for tracking whether same dialog already delivered to user or not.
    */
    public static synchronized AlertDialog showAlertDialog(Context context, CharSequence message, CharSequence positiveButton) {
        CustomAlertDialog customAlertDialog = new CustomAlertDialog(context);
        customAlertDialog.setCancelable(false);
        customAlertDialog.setMessage(message);

        customAlertDialog.setPositiveButton(positiveButton, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return customAlertDialog.show();
    }

    public static synchronized AlertDialog showReturningActualDialog(Context context,
                                                                     CharSequence title,
                                                                     CharSequence message,
                                                                     CharSequence positiveButton,
                                                                     CharSequence negativeButton,
                                                                     OnClickListener positiveListener,
                                                                     OnClickListener negativeListener) {

        CustomAlertDialog customAlertDialog = buildDialog(context, title, message, positiveButton, positiveListener, false);
        customAlertDialog.setNegativeButton(negativeButton, negativeListener);

        return initDialog(customAlertDialog);
    }

    private static CustomAlertDialog buildDialog(Context context,
                                                 CharSequence title,
                                                 CharSequence message,
                                                 CharSequence positiveButton,
                                                 OnClickListener positiveListener, boolean is2netReading) {

        CustomAlertDialog customAlertDialog = new CustomAlertDialog(context);
        customAlertDialog.setCancelable(false);
        if (!TextUtils.isEmpty(title)) {
            customAlertDialog.setTitle(title);
        }
        customAlertDialog.setMessage(message);
        customAlertDialog.setPositiveButton(positiveButton, positiveListener);

        return customAlertDialog;
    }

    private static AlertDialog initDialog(CustomAlertDialog customAlertDialog) {
        //shows then centers the dialog text
        AlertDialog dialog = customAlertDialog.show();

        return dialog;
    }

    public static synchronized AlertDialog showReturningActualDialog(Context context,
                                                                     CharSequence title,
                                                                     CharSequence message,
                                                                     CharSequence positiveButton,
                                                                     OnClickListener positiveListener, boolean is2netReading) {

        CustomAlertDialog customAlertDialog = buildDialog(context, title, message, positiveButton, positiveListener, is2netReading);

        return initDialog(customAlertDialog);
    }

    public static synchronized CustomAlertDialog show(Context context, CharSequence title, CharSequence message, CharSequence positiveButton,
                                                      CharSequence negativeButton, OnClickListener positiveListener, OnClickListener negativeListener) {

        CustomAlertDialog customAlertDialog = new CustomAlertDialog(context);
        customAlertDialog.setCancelable(false);
        if (!TextUtils.isEmpty(title)) {
            customAlertDialog.setTitle(title);
        }

        customAlertDialog.setMessage(message);

        if (!TextUtils.isEmpty(positiveButton)) {
            customAlertDialog.setPositiveButton(positiveButton, positiveListener);
        }

        if (!TextUtils.isEmpty(negativeButton)) {
            customAlertDialog.setNegativeButton(negativeButton, negativeListener);
        }

        customAlertDialog.show();
        return customAlertDialog;
    }

    public static CustomAlertDialog show(Context context, CharSequence title, CharSequence message, CharSequence positiveButton, OnClickListener positiveListener) {
        return show(context, title, message, positiveButton, null, positiveListener, null);
    }

    public static CustomAlertDialog show(Context context, CharSequence message, CharSequence positiveButton) {

        CustomAlertDialog customAlertDialog = new CustomAlertDialog(context);
        customAlertDialog.setCancelable(false);
        customAlertDialog.setMessage(message);
        customAlertDialog.setPositiveButton(positiveButton, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        customAlertDialog.show();
        return customAlertDialog;
    }

    public static CustomAlertDialog show(Context context, CharSequence message, CharSequence positiveButton, OnClickListener positiveListener) {
        return show(context, null, message, positiveButton, positiveListener);
    }

    public static CustomAlertDialog show(Context context, String title, CharSequence message, CharSequence positiveButton, OnClickListener positiveListener) {

        CustomAlertDialog customAlertDialog = new CustomAlertDialog(context);
        customAlertDialog.setCancelable(false);
        customAlertDialog.setMessage(message);
        if (!TextUtils.isEmpty(title)) {
            customAlertDialog.setTitle(title);
        }
        customAlertDialog.setPositiveButton(positiveButton, positiveListener);
        customAlertDialog.show();
        return customAlertDialog;
    }

    public static CustomAlertDialog show(Context context, CharSequence message,
                                         CharSequence[] listItems, CharSequence positiveButton,
                                         OnClickListener positiveListener, CharSequence negativeButton, OnClickListener negativeListener) {
        CustomAlertDialog customAlertDialog = new CustomAlertDialog(context);
        customAlertDialog.setCancelable(false);

        customAlertDialog.setTitle(message);
        customAlertDialog.setSingleChoiceItems(listItems, 0, null);
        customAlertDialog.setPositiveButton(positiveButton, positiveListener);
        customAlertDialog.setNegativeButton(negativeButton, negativeListener);

        customAlertDialog.show();
        return customAlertDialog;
    }
}
