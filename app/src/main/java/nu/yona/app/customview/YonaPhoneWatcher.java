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
import android.view.View;
import android.widget.EditText;

/**
 * The type Yona phone watcher.
 */
public class YonaPhoneWatcher implements TextWatcher
{

	private EditText mobileNumber;
	private Context mContext;
	private YonaFontTextView mobileNumberErrView;
	private YonaFontNumberTextView mobileNumberView;
	private int textlength = 0;


	/**
	 * Instantiates a new Yona phone watcher.
	 *
	 * @param editText            the edit text
	 * @param context             the context
	 * @param mobileNumberErrView the mobile number layout
	 */
	public YonaPhoneWatcher(EditText editText, Context context, YonaFontTextView mobileNumberErrView)
	{
		super();
		this.mobileNumber = editText;
		mContext = context;
		this.mobileNumberErrView = mobileNumberErrView;
	}

	@Override
	public synchronized void beforeTextChanged(CharSequence s, int start, int count, int after)
	{
		if (mobileNumberErrView != null)
		{
			mobileNumberErrView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
	{


		String text = mobileNumber.getText().toString();
		textlength = mobileNumber.getText().length();

		if (text.endsWith(" "))
		{
			return;
		}

		if (textlength == 4)
		{
			mobileNumber.setText(new StringBuilder(text).insert(text.length() - 1, " ").toString());
			mobileNumber.setSelection(mobileNumber.getText().length());

		}
		else if (textlength == 8)
		{
			mobileNumber.setText(new StringBuilder(text).insert(text.length() - 1, " ").toString());
			mobileNumber.setSelection(mobileNumber.getText().length());
		}
	}

	public synchronized void afterTextChanged(Editable s)
	{

	}
}