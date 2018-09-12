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
import android.database.Cursor;
import android.database.DatabaseUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import nu.yona.app.api.db.DBConstant;

/**
 * Created by kinnarvasa on 08/06/16.
 */
public class Activity extends BaseEntity
{
	@SerializedName("application")
	@Expose
	private String application;
	@SerializedName("startTime")
	@Expose
	private String startTime;
	@SerializedName("endTime")
	@Expose
	private String endTime;

	/**
	 * Instantiates a new Activity.
	 */
	public Activity()
	{

	}

	/**
	 * Instantiates a new Activity.
	 *
	 * @param c the c
	 */
	public Activity(Cursor c)
	{
		getEventsFromCursor(c);
	}

	/**
	 * Gets application.
	 *
	 * @return The application
	 */
	public String getApplication()
	{
		return application;
	}

	/**
	 * Sets application.
	 *
	 * @param application The application
	 */
	public void setApplication(String application)
	{
		this.application = application;
	}

	/**
	 * Gets start time.
	 *
	 * @return The startTime
	 */
	public String getStartTime()
	{
		return startTime;
	}

	/**
	 * Sets start time.
	 *
	 * @param startTime The startTime
	 */
	public void setStartTime(String startTime)
	{
		this.startTime = startTime;
	}

	/**
	 * Gets end time.
	 *
	 * @return The endTime
	 */
	public String getEndTime()
	{
		return endTime;
	}

	/**
	 * Sets end time.
	 *
	 * @param endTime The endTime
	 */
	public void setEndTime(String endTime)
	{
		this.endTime = endTime;
	}

	@Override
	public ContentValues getDbContentValues()
	{
		ContentValues values = new ContentValues();
		values.put(DBConstant.APPLICATION_NAME, application);
		values.put(DBConstant.APPLICATION_START_TIME, startTime);
		values.put(DBConstant.APPLICATION_END_TIME, endTime);
		return values;
	}

	private void getEventsFromCursor(Cursor c)
	{
		ContentValues args = new ContentValues();
		DatabaseUtils.cursorRowToContentValues(c, args);
		createFromContentValue(args);
	}

	private void createFromContentValue(ContentValues args)
	{
		String tempString;

		tempString = args.getAsString(DBConstant.APPLICATION_NAME);
		if (tempString != null)
		{
			setApplication(tempString);
		}
		tempString = args.getAsString(DBConstant.APPLICATION_START_TIME);
		if (tempString != null)
		{
			setStartTime(tempString);
		}
		tempString = args.getAsString(DBConstant.APPLICATION_END_TIME);
		if (tempString != null)
		{
			setEndTime(tempString);
		}
	}
}
