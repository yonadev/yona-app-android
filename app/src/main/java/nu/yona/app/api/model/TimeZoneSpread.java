/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.api.model;

import android.content.ContentValues;

/**
 * Created by kinnarvasa on 09/06/16.
 */
public class TimeZoneSpread extends BaseEntity
{
	/**
	 * Sample:
	 * index        :  0, 1, 2, 3, 4, 5
	 * spread       : 10, 0, 5, 0, 0, 0
	 * spreadUsage  :  0, 1, 3
	 * So, output here will be:
	 * total items in sample (index 0 to 5) = 6 X 15 mins each = 90 mins
	 * Now, this class will return array of TimezoneSpread for 90 mins (in real, it will give array of 1440 minutes)
	 * index = 0, usedValue = 10, color = blue,  allowed = true
	 * index = 0, usedValue =  5, color = green, allowed = true
	 * index = 1, usedValue = 15, color = green, allowed = true
	 * index = 2, usedValue =  5, color = pink,  allowed = false
	 * index = 2, usedValue = 10, color = blank, allowed = false
	 * index = 3, usedvalue = 15, color = green, allowed = true
	 * index = 4, usedValue = 15, color = blank, allowed = false
	 * index = 5, usedValue = 15, color = blank, allowed = false
	 * So, for TimeFrameControl, just use usedValue and color for entire array to draw from 0 to 1440
	 * To draw spread control, use all 4 elements of this class.
	 */
// total values of used,
	int usedValue;
	/**
	 * The Index.
	 */
	int index;
	/**
	 * The Color.
	 */
	int color;
	/**
	 * The Allowed.
	 */
	boolean allowed;

	/**
	 * Is allowed boolean.
	 *
	 * @return the boolean
	 */
	public boolean isAllowed()
	{
		return this.allowed;
	}

	/**
	 * Sets allowed.
	 *
	 * @param allowed the allowed
	 */
	public void setAllowed(boolean allowed)
	{
		this.allowed = allowed;
	}

	/**
	 * Gets color.
	 *
	 * @return the color
	 */
	public int getColor()
	{
		return this.color;
	}

	/**
	 * Sets color.
	 *
	 * @param color the color
	 */
	public void setColor(int color)
	{
		this.color = color;
	}

	/**
	 * Gets used value.
	 *
	 * @return the used value
	 */
	public int getUsedValue()
	{
		return this.usedValue;
	}

	/**
	 * Sets used value.
	 *
	 * @param usedValue the used value
	 */
	public void setUsedValue(int usedValue)
	{
		this.usedValue = usedValue;
	}

	/**
	 * Gets index.
	 *
	 * @return the index
	 */
	public int getIndex()
	{
		return this.index;
	}

	/**
	 * Sets index.
	 * Sets index.
	 *
	 * @param index the index
	 */
	public void setIndex(int index)
	{
		this.index = index;
	}

	@Override
	public ContentValues getDbContentValues()
	{
		return null;
	}
}
