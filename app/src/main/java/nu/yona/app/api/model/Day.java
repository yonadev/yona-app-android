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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by kinnarvasa on 06/06/16.
 */
public class Day extends BaseEntity
{
	@SerializedName("_links")
	@Expose
	private Links links;
	@SerializedName("totalActivityDurationMinutes")
	@Expose
	private Integer totalActivityDurationMinutes;
	@SerializedName("goalAccomplished")
	@Expose
	private Boolean goalAccomplished;
	@SerializedName("totalMinutesBeyondGoal")
	@Expose
	private Integer totalMinutesBeyondGoal;

	/**
	 * Gets links.
	 *
	 * @return The links
	 */
	public Links getLinks()
	{
		return links;
	}

	/**
	 * Sets links.
	 *
	 * @param links The _links
	 */
	public void setLinks(Links links)
	{
		this.links = links;
	}

	/**
	 * Gets total activity duration minutes.
	 *
	 * @return The totalActivityDurationMinutes
	 */
	public Integer getTotalActivityDurationMinutes()
	{
		return totalActivityDurationMinutes;
	}

	/**
	 * Sets total activity duration minutes.
	 *
	 * @param totalActivityDurationMinutes The totalActivityDurationMinutes
	 */
	public void setTotalActivityDurationMinutes(Integer totalActivityDurationMinutes)
	{
		this.totalActivityDurationMinutes = totalActivityDurationMinutes;
	}

	/**
	 * Gets goal accomplished.
	 *
	 * @return The goalAccomplished
	 */
	public Boolean getGoalAccomplished()
	{
		return goalAccomplished;
	}

	/**
	 * Sets goal accomplished.
	 *
	 * @param goalAccomplished The goalAccomplished
	 */
	public void setGoalAccomplished(Boolean goalAccomplished)
	{
		this.goalAccomplished = goalAccomplished;
	}

	/**
	 * Gets total minutes beyond goal.
	 *
	 * @return The totalMinutesBeyondGoal
	 */
	public Integer getTotalMinutesBeyondGoal()
	{
		return totalMinutesBeyondGoal;
	}

	/**
	 * Sets total minutes beyond goal.
	 *
	 * @param totalMinutesBeyondGoal The totalMinutesBeyondGoal
	 */
	public void setTotalMinutesBeyondGoal(Integer totalMinutesBeyondGoal)
	{
		this.totalMinutesBeyondGoal = totalMinutesBeyondGoal;
	}

	@Override
	public ContentValues getDbContentValues()
	{
		return null;
	}
}
