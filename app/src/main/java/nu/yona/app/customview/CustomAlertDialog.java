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

import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.text.TextUtils;

import nu.yona.app.R;

/**
 * The type Custom alert dialog.
 */
public class CustomAlertDialog extends android.support.v7.app.AlertDialog.Builder {

    private CustomAlertDialog(Context context) {
        super(context);
    }

    /**
     * Instantiates a new Custom alert dialog.
     *
     * @param context the context
     * @param theme   the theme
     */
    public CustomAlertDialog(Context context, int theme) {
        super(context, theme);
    }

    /**
     * Show custom alert dialog.
     *
     * @param context          Context
     * @param title            Title of Alert
     * @param message          Message in Alert
     * @param positiveButton   possitve button text
     * @param negativeButton   negative button text
     * @param positiveListener positive button listener
     * @param negativeListener negative button listener
     * @return CustomAlertDialog object
     */
    public static synchronized CustomAlertDialog show(Context context, CharSequence title, CharSequence message, CharSequence positiveButton,
                                                      CharSequence negativeButton, OnClickListener positiveListener, OnClickListener negativeListener) {

        CustomAlertDialog customAlertDialog = new CustomAlertDialog(context, R.style.MyDialogTheme);
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

    /**
     * Show custom alert dialog.
     *
     * @param context          context
     * @param message          message for alert
     * @param positiveButton   positve button text
     * @param positiveListener positive button listener
     * @return custom alert dialog
     */
    public static CustomAlertDialog show(Context context, CharSequence message, CharSequence positiveButton, OnClickListener positiveListener) {
        return show(context, null, message, positiveButton, positiveListener);
    }

    private static CustomAlertDialog show(Context context, String title, CharSequence message, CharSequence positiveButton, OnClickListener positiveListener) {

        CustomAlertDialog customAlertDialog = new CustomAlertDialog(context, R.style.MyDialogTheme);
        customAlertDialog.setCancelable(false);
        customAlertDialog.setMessage(message);
        if (!TextUtils.isEmpty(title)) {
            customAlertDialog.setTitle(title);
        }
        customAlertDialog.setPositiveButton(positiveButton, positiveListener);
        customAlertDialog.show();
        return customAlertDialog;
    }

    /**
     * Show custom alert dialog.
     *
     * @param context            Context
     * @param message            message in alert
     * @param listItems          CharSeqance[] for list
     * @param itemChooseListener selected item from list listener
     * @param selectedItem       the selected item
     * @return CustomAlertDialog custom alert dialog
     */
    public static CustomAlertDialog show(Context context, CharSequence message,
                                         CharSequence[] listItems, OnClickListener itemChooseListener, int selectedItem) {
        CustomAlertDialog customAlertDialog = new CustomAlertDialog(context, R.style.MyDialogTheme);
        customAlertDialog.setCancelable(false);

        customAlertDialog.setTitle(message);
        customAlertDialog.setSingleChoiceItems(listItems, selectedItem, itemChooseListener);
        customAlertDialog.show();
        return customAlertDialog;
    }
}
