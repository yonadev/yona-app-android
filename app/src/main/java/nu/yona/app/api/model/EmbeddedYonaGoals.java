/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.model;

import android.content.ContentValues;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Embedded yona goals.
 */
public class EmbeddedYonaGoals extends BaseEntity
{

	@SerializedName("yona:goals")
	@Expose
	private List<YonaGoal> yonaGoals = new ArrayList<>();

	/**
	 * Gets yona goals.
	 *
	 * @return The yonaGoals
	 */
	public List<YonaGoal> getYonaGoals()
	{
		return yonaGoals;
	}

	/**
	 * Sets yona goals.
	 *
	 * @param yonaGoals The yona:goals
	 */
	public void setYonaGoals(List<YonaGoal> yonaGoals)
	{
		this.yonaGoals = yonaGoals;
	}

	@Override
	public ContentValues getDbContentValues()
	{
		return null;
	}
}
