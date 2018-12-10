/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by kinnarvasa on 21/04/16.
 */
public class PostBudgetYonaGoal extends PostYonaGoal
{

	@SerializedName("maxDurationMinutes")
	@Expose
	private long maxDurationMinutes;

	/**
	 * Gets max duration minutes.
	 *
	 * @return the max duration minutes
	 */
	public long getMaxDurationMinutes()
	{
		return maxDurationMinutes;
	}

	/**
	 * Sets max duration minutes.
	 *
	 * @param maxDurationMinutes the max duration minutes
	 */
	public void setMaxDurationMinutes(long maxDurationMinutes)
	{
		this.maxDurationMinutes = maxDurationMinutes;
	}
}
