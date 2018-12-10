/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * The type Yona font text view.
 */
public class YonaFontTextView extends TextView
{

	/**
	 * Instantiates a new Yona font text view.
	 *
	 * @param context the context
	 */
	public YonaFontTextView(Context context)
	{
		super(context);

		YonaFontUtils.applyCustomFont(this, context, null);
	}

	/**
	 * Instantiates a new Yona font text view.
	 *
	 * @param context the context
	 * @param attrs   the attrs
	 */
	public YonaFontTextView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		YonaFontUtils.applyCustomFont(this, context, attrs);
	}

	/**
	 * Instantiates a new Yona font text view.
	 *
	 * @param context  the context
	 * @param attrs    the attrs
	 * @param defStyle the def style
	 */
	public YonaFontTextView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);

		YonaFontUtils.applyCustomFont(this, context, attrs);
	}
}