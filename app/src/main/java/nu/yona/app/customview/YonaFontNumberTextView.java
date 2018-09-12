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
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;

import nu.yona.app.YonaApplication;
import nu.yona.app.state.EventChangeManager;

/**
 * Created by kinnarvasa on 19/07/16.
 */

public class YonaFontNumberTextView extends EditText
{

	private int lenghtBlock;

	/**
	 * Instantiates a new Yona font edit text view.
	 *
	 * @param context the context
	 */
	public YonaFontNumberTextView(Context context)
	{
		super(context);
		YonaFontUtils.applyCustomFont(this, context, null);
	}

	/**
	 * Instantiates a new Yona font edit text view.
	 *
	 * @param context the context
	 * @param attrs   the attrs
	 */
	public YonaFontNumberTextView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		YonaFontUtils.applyCustomFont(this, context, attrs);
	}

	/**
	 * Instantiates a new Yona font edit text view.
	 *
	 * @param context  the context
	 * @param attrs    the attrs
	 * @param defStyle the def style
	 */
	public YonaFontNumberTextView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
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
	public InputConnection onCreateInputConnection(EditorInfo outAttrs)
	{
		return new YonaInputConnection(super.onCreateInputConnection(outAttrs),
				true);
	}

	private class YonaInputConnection extends InputConnectionWrapper
	{

		public YonaInputConnection(InputConnection target, boolean mutable)
		{
			super(target, mutable);
		}

		@Override
		public boolean sendKeyEvent(KeyEvent event)
		{
			if (event.getAction() == KeyEvent.ACTION_DOWN
					&& event.getKeyCode() == KeyEvent.KEYCODE_DEL)
			{
				YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_KEY_BACK_PRESSED, null);
			}
			return super.sendKeyEvent(event);
		}

	}
}
