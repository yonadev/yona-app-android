/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.pincode;

import android.text.method.PasswordTransformationMethod;
import android.view.View;

/**
 * Created by bhargavsuthar on 4/1/16.
 */
class YonaPasswordTransformationManager extends PasswordTransformationMethod
{

	@Override
	public CharSequence getTransformation(CharSequence source, View view)
	{
		return new PasswordCharSequence(source);
	}

	private class PasswordCharSequence implements CharSequence
	{
		private final CharSequence mSource;

		/**
		 * Instantiates a new Password char sequence.
		 *
		 * @param source the source
		 */
		public PasswordCharSequence(CharSequence source)
		{
			mSource = source; // Store char sequence
		}

		@Override
		public char charAt(int index)
		{
			return '*'; // This is the important part
		}

		@Override
		public int length()
		{
			return mSource.length(); // Return default
		}

		@Override
		public CharSequence subSequence(int start, int end)
		{
			return mSource.subSequence(start, end); // Return default
		}
	}
}
