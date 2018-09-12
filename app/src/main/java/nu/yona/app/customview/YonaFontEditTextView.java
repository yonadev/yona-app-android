/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.customview;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * Created by bhargavsuthar on 3/31/16.
 */
public class YonaFontEditTextView extends EditText
{

	private int lenghtBlock;

	/**
	 * Instantiates a new Yona font edit text view.
	 *
	 * @param context the context
	 */
	public YonaFontEditTextView(Context context)
	{
		super(context);
		setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
		YonaFontUtils.applyCustomFont(this, context, null);
	}

	/**
	 * Instantiates a new Yona font edit text view.
	 *
	 * @param context the context
	 * @param attrs   the attrs
	 */
	public YonaFontEditTextView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
		YonaFontUtils.applyCustomFont(this, context, attrs);
	}

	/**
	 * Instantiates a new Yona font edit text view.
	 *
	 * @param context  the context
	 * @param attrs    the attrs
	 * @param defStyle the def style
	 */
	public YonaFontEditTextView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
		YonaFontUtils.applyCustomFont(this, context, attrs);
	}

	@Override
	public void setSelection(int index)
	{
		super.setSelection(index);
	}

	@Override
	protected void onSelectionChanged(int selStart, int selEnd)
	{
		if (selStart < lenghtBlock || selEnd < lenghtBlock)
		{
			setSelection(getText().length());
		}
	}

	/**
	 * Sets not editable length.
	 *
	 * @param etLength the et length
	 */
	public void setNotEditableLength(int etLength)
	{
		this.lenghtBlock = etLength;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_DEL)
		{
			if (getText().toString().length() == 0)
			{
				setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
			}
		}
		return super.onKeyUp(keyCode, event);
	}
}
