/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.model;

import android.content.ContentValues;

/**
 * Created by kinnarvasa on 25/04/16.
 */
public class PinResetDelay extends BaseEntity
{

	private String delay;

	/**
	 * Gets delay.
	 *
	 * @return the delay
	 */
	public String getDelay()
	{
		return delay;
	}

	/**
	 * Sets delay.
	 *
	 * @param delay the delay
	 */
	public void setDelay(String delay)
	{
		this.delay = delay;
	}

	@Override
	public ContentValues getDbContentValues()
	{
		return null;
	}
}
